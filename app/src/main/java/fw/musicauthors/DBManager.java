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
 * I assume that id field is unique for each artist
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

    /**
     * if updates was more than 1
     * @param numberUpdates
     */
    private void finishDownload(int numberUpdates){
        if (numberUpdates > 0) {
            notifyUpgradeListeners(artists);
        }
    }

    /**
     * try to update or insert artist to BD
     * @param artist
     * @return 0 if nothing change, 1 if insert or update
     */
    public int putArtistToDB(Artist artist){
        ContentValues values = new ContentValues();
        artist.compactToContentValue(values);

        Cursor cursor = DBread.rawQuery("SELECT * FROM " + DBHelper.TABLE_ARTISTS + " WHERE " + DBHelper.KEY_ID + " = " + artist.id, null);
        if (cursor.getCount() != 0) {
            // check, something need to change or not
            boolean b1;
            try {
                b1 = artist.equals(cursor.getExtras());
            }
            catch (NullPointerException ex){
                ex.printStackTrace();
                b1 = false;
            }
            //something need to change
            if (!b1) {
                DBwrite.update(DBHelper.TABLE_ARTISTS, values, DBHelper.KEY_ID + " = " + artist.id, null);
                return 1;
            }
        }
        else {
            //if the id is not found
            DBwrite.insert(DBHelper.TABLE_ARTISTS, null, values);
            return 1;
        }
        return 0;
    }


    /**
     * Perfome download the data from server and update BD as AnsyTask
     */
    public class LoadJSON extends AsyncTask<String, Void, Integer> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        @Override
        protected Integer doInBackground(String... urls) {
            // получаем данные с внешнего ресурса
            JSONArray jsonArray = null;
            //Get jsonArray from server
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
            //make a list of downloaded artists
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    artists.add(new Artist(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(artists);
            // count how many new is in the server
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
