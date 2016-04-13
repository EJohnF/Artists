package fw.musicauthors;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
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
    URL link;
    String description;
    URL toSmallCover;
    URL toBigCover;

    public Artist(JSONObject json) {

        try {
            id = json.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            name = json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray genreses = null;
        try {
            genreses = json.getJSONArray("genres");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        genres = new LinkedList<>();
        for (int i = 0; i < genreses.length(); i++) {
            try {
                genres.add(genreses.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            tracks = json.getInt("tracks");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            albums = json.getInt("albums");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            link = new URL(json.getString("link"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            description = json.getString("description");
            String cap = description.substring(0, 1).toUpperCase() + description.substring(1);
            description = cap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject covers = null;
        try {
            covers = json.getJSONObject("cover");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            toSmallCover = new URL(covers.getString("small"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            toBigCover = new URL(covers.getString("big"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "Author{" +
                "albums=" + albums +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", genres=" + genres +
                ", tracks=" + tracks +
                ", link=" + link +
                ", description='" + description + '\'' +
                ", toSmallCover=" + toSmallCover +
                ", toBigCover=" + toBigCover +
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
        String res = albums + " альбомов, " + tracks + " песен";
        return res;
    }

    public void setSmallPicture(ImageView imageView) {
        Picasso.with(imageView.getContext()).load(String.valueOf(toSmallCover)).into(imageView);
    }

    public void setBigPicture(ImageView imageView) {
        Picasso.with(imageView.getContext()).load(String.valueOf(toBigCover)).into(imageView);
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
