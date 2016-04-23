package fw.musicauthors;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by John on 06.04.2016.
 */
public class Artist implements Comparable<Artist>{
    int id;
    String name;
    LinkedList<String> genres;
    int tracks;
    int albums;
    String link_string = "";
    String description = "";
    String smallCover_string = "";
    String bigCover_string = "";

    public Artist(JSONObject json) {
        try {
            id = json.getInt("id");
            name = json.getString("name");
            JSONArray genreses = json.getJSONArray("genres");
            genres = new LinkedList<>();
            for (int i = 0; i < genreses.length(); i++) {
                    genres.add(genreses.getString(i));
            }
            tracks = json.getInt("tracks");
            albums = json.getInt("albums");
            description = json.getString("description");
            String cap = description.substring(0, 1).toUpperCase() + description.substring(1);
            description = cap;
            JSONObject covers = json.getJSONObject("cover");
            smallCover_string = covers.getString("small");
            bigCover_string = covers.getString("big");
            link_string = json.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Artist(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID));
        String tempGenres = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GENRES));
        genres = genresesFormString(tempGenres);
        name = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME));

        tracks = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ALBUMS));
        albums = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_TRACKS));

        link_string = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_LINK));
        smallCover_string = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SMALLPIC));
        bigCover_string = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_BIGPIC));
        description = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION));
    }

    public LinkedList<String> genresesFormString(String st){
        LinkedList<String> res = new LinkedList<String>();
        String[] strings = st.split(", ");
        for (int i = 0; i < strings.length; i++){
            res.add(strings[i]);
        }
        return res;
    }

    /**
     * Make one string which complitly descryde everything about author
     * @return
     */
    @Override
    public String toString() {
        return "Author{" +
                "albums=" + albums +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", genres=" + genres +
                ", tracks=" + tracks +
                ", link=" + link_string +
                ", description='" + description + '\'' +
                ", smallCover=" + smallCover_string +
                ", bigCover=" + bigCover_string +
                '}';
    }

    /**
     * Make a string all genreses of Artists with slit by ", "
     * @return
     */
    public String getGenresesString() {
        String res = "";
        if (genres.size() > 0) {
            res += genres.get(0);
        }
        for (int i = 1; i < genres.size(); i++) {
            res += ", " + genres.get(i);
        }
        return res;
    }

    /**
     * Make a string with pattern: № альбом(-ов)(-а), № песен(-я)(-и)
     * @return
     */
    public String getAlbumTrackString() {
        String alb = "альбомов";
        String track = "песен";
        if (albums%10 == 1 && albums%100 != 11)
                alb = "альбом";
        if ( albums%10 > 1 && albums%10 < 5  && (albums%100 < 10 || albums % 100 >20))
                alb = "альбома";
        if (tracks%10 == 1 && tracks%100 != 11)
            track = "песня";
        if ( tracks%10 > 1 && tracks%10 < 5  && (tracks%100 < 10 || tracks % 100 >20))
            track = "песни";
        String res = albums + " " + alb + ", " + tracks + " " + track;
        return res;
    }

    /**
     * if small Picture present, method set it to imageView
     * @param imageView
     */
    public void setSmallPicture(ImageView imageView) {
        if (smallCover_string != "")
            Picasso.with(imageView.getContext()).load(smallCover_string).into(imageView);
    }

    /**
     * if big picture present, method set it to imageView
     * @param imageView
     */
    public void setBigPicture(ImageView imageView) {
        Picasso.with(imageView.getContext()).load(bigCover_string).into(imageView);
    }

    /**
     * Comparing of two Artists corresponding their name field.
     * @param another
     * @return
     */
    @Override
    public int compareTo(Artist another) {
        return name.compareTo(another.name);

    }

    /**
     * Check, the input st is a substring of name Artist or not
     * @param st
     * @return
     */
    public boolean hasInName(String st){
        String s1 = name.toLowerCase();
        String s2 = st.toLowerCase();
        return s1.contains(s2);
    }

    public void compactToContentValue(ContentValues values) {
        values.put(DBHelper.KEY_ID, id);
        values.put(DBHelper.KEY_NAME, name);
        values.put(DBHelper.KEY_GENRES, getGenresesString());
        values.put(DBHelper.KEY_ALBUMS, albums);
        values.put(DBHelper.KEY_TRACKS, tracks);
        values.put(DBHelper.KEY_DESCRIPTION, description);
        values.put(DBHelper.KEY_LINK, link_string);
        values.put(DBHelper.KEY_SMALLPIC, smallCover_string);
        values.put(DBHelper.KEY_BIGPIC, bigCover_string);

    }

    public boolean equals(Bundle extras) throws NullPointerException{
        if (!extras.getString(DBHelper.KEY_NAME).equals(name)){
            return false;
        }
        if (!extras.getString(DBHelper.KEY_GENRES).equals(getGenresesString())){
            return false;
        }
        if (extras.getInt(DBHelper.KEY_ALBUMS) != albums){
            return false;
        }
        if (extras.getInt(DBHelper.KEY_TRACKS) != tracks){
            return false;
        }
        if (!extras.getString(DBHelper.KEY_DESCRIPTION).equals(description)){
            return false;
        }
        if (!extras.getString(DBHelper.KEY_LINK).equals(link_string)){
            return false;
        }
        if (!extras.getString(DBHelper.KEY_SMALLPIC).equals(smallCover_string)){
            return false;
        }
        if (!extras.getString(DBHelper.KEY_BIGPIC).equals(bigCover_string)){
            return false;
        }
        return true;
    }
}
