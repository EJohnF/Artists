package fw.musicauthors;

import android.database.Cursor;
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
    String link_string;
    String description;
    String smallCover_string;
    String bigCover_string;

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

    public String getAlbumTrackString() {
        String alb = "альбомов";
        String track = "песен";
        if ( (albums%10 == 1 && albums>20) || (albums == 1))
                alb = "альбом";
        if (( albums%10 > 1 && albums%10 < 5 && albums > 20) || (albums > 1 && albums < 5))
                alb = "альбома";
        if ( (tracks%10 == 1 && tracks>20) || (tracks == 1))
            track = "песня";
        if (( tracks%10 > 1 && tracks%10 < 5 && tracks > 20) || (tracks > 1 && tracks < 5))
            track = "песни";
        String res = albums + " " + alb + ", " + tracks + " " + track;
        return res;
    }

    public void setSmallPicture(ImageView imageView) {
        Picasso.with(imageView.getContext()).load(smallCover_string).into(imageView);
    }

    public void setBigPicture(ImageView imageView) {
        Picasso.with(imageView.getContext()).load(bigCover_string).into(imageView);
    }

    @Override
    public int compareTo(Artist another) {
        return name.compareTo(another.name);

    }

    public boolean hasInName(String st){
        String s1 = name.toLowerCase();
        String s2 = st.toLowerCase();
        return s1.contains(s2);
    }

}
