package fw.musicauthors;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by John on 07.04.2016.
 */
public class ArtistsContainer {
    public static ArtistsContainer instance = new ArtistsContainer();

     ArrayList<Artist> artists;

    ArrayList<Artist> currentListArtists;

    private ArtistsContainer(){
        artists = new ArrayList<>(350);
    }

    public void fillContainerByJSON(JSONArray jsonArray){
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                artists.add(new Artist(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(artists);
        currentListArtists = artists;
    }
    public boolean isEmpty(){
        if (artists == null || artists.size() == 0){
            return true;
        }
        return false;
    }

    public void setCurrentListArtists(ArrayList<Artist> currentList){
        currentListArtists = currentList;
    }
    public Artist getArtist(int position){
        return currentListArtists.get(position);
    }

    public void makeCurrentBySearch(String st){
        currentListArtists = new ArrayList<Artist>();
        for (int i = 0; i < artists.size(); i++){
            if (artists.get(i).hasInName(st)){
                currentListArtists.add(artists.get(i));
            }
        }
    }
}
