package fw.musicauthors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by John on 19.04.2016.
 */
public class DBManager{
    private DBHelper helper;
    private SQLiteDatabase DBwrite;
    private SQLiteDatabase DBread;
    private Context context;

    private LoadJSON loader;
    private LinkedList<IDBUpgrade> upgradeListeners;
    private LinkedList<INoInternet> noConnectionListeners;

    ArrayList<Artist> artists;

    interface IDBUpgrade{
        void onUpgrade(ArrayList<Artist> artists);
    }

    interface INoInternet{
        void noConnection();
    }

    void addUpgradeListener(IDBUpgrade listener){
        upgradeListeners.add(listener);
    }

    void addNoConnectionListener(INoInternet listener){
        noConnectionListeners.add(listener);
    }

    public DBManager(Context context){
        this.context = context;
        helper = new DBHelper(context);
        DBwrite = helper.getWritableDatabase();
        DBread = helper.getReadableDatabase();

        upgradeListeners = new LinkedList<>();
        noConnectionListeners = new LinkedList<>();
    }

    private void notifyUpgradeListeners(ArrayList<Artist> artists){
        for (int i = 0; i < upgradeListeners.size(); i++){
            upgradeListeners.get(i).onUpgrade(artists);
        }
    }

    private void notifyNoConnectionListeners(){
        for (int i = 0; i < noConnectionListeners.size(); i++){
            noConnectionListeners.get(i).noConnection();
        }
    }

    public void read(){
        artists = new ArrayList<>(350);
        if (isEmpty()){
            download();
        }
        else {
            Cursor cursor = DBread.rawQuery("SELECT * FROM " + DBHelper.TABLE_ARTISTS, null);
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    artists.add(new Artist(cursor));
                    cursor.moveToNext();
                }
            }
            Collections.sort(artists);
            notifyUpgradeListeners(artists);
        }
    }

    public boolean isEmpty(){
        Cursor cursor = DBread.rawQuery("SELECT * FROM " + DBHelper.TABLE_ARTISTS, null);
        int number = 0;
        if (cursor .moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                number++;
                cursor.moveToNext();
                continue;
            }
        }
        return number == 0;
    }

    public void download(){
        loader = new LoadJSON();
        loader.execute(ListAuthorsActivity.URL_TO_JSON);
    }

    private void finishDownload(int numberUpdates){
        if (numberUpdates > 0) {
            notifyUpgradeListeners(artists);
        }
    }

    /**
     *
     * @param artist
     * @return 0 if update, 1 if insert
     */
    public int putArtistToDB(Artist artist){
        ContentValues values = new ContentValues();

        values.put(DBHelper.KEY_ID, artist.id);
        values.put(DBHelper.KEY_NAME, artist.name);
        values.put(DBHelper.KEY_GENRES, artist.getGenresesString());
        values.put(DBHelper.KEY_ALBUMS, artist.albums);
        values.put(DBHelper.KEY_TRACKS, artist.tracks);
        values.put(DBHelper.KEY_DESCRIPTION, artist.description);
        values.put(DBHelper.KEY_LINK, artist.link_string);
        values.put(DBHelper.KEY_SMALLPIC, artist.smallCover_string);
        values.put(DBHelper.KEY_BIGPIC, artist.bigCover_string);

        int n = DBwrite.update(DBHelper.TABLE_ARTISTS, values, DBHelper.KEY_ID + " = " + artist.id, null);
        if (n == 0) {
            DBwrite.insert(DBHelper.TABLE_ARTISTS, null, values);
            return 1;
        }
        else {
            return 0;
        }

    }


    public class LoadJSON extends AsyncTask<String, Void, Integer> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        @Override
        protected Integer doInBackground(String... urls) {
            // получаем данные с внешнего ресурса
            JSONArray jsonArray = null;
            try {
                URL url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
                jsonArray = new JSONArray(resultJson);
            } catch (Exception e) {
                notifyNoConnectionListeners();
                e.printStackTrace();
            }
            if (jsonArray == null)
                return 0;
            artists = new ArrayList<>(350);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    artists.add(new Artist(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            int numberUpdates = 0;
            for (int i = 0; i < artists.size(); i++) {
                numberUpdates += putArtistToDB(artists.get(i));
            }
            return numberUpdates;
        }

        @Override
        protected void onPostExecute(Integer numberUpdates) {
            super.onPostExecute(numberUpdates);
            finishDownload(numberUpdates);
        }
    }
}
