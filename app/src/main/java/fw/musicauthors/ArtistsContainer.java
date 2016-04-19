package fw.musicauthors;

import java.util.ArrayList;

/**
 * Created by John on 07.04.2016.
 */
public class ArtistsContainer {
    public static ArtistsContainer instance = new ArtistsContainer();

    private ArrayList<Artist> artists;
    private ArrayList<Artist> currentListArtists;

    private ArtistsContainer() {
        artists = new ArrayList<>(350);
        currentListArtists = artists;
    }

    public Artist getArtist(int position) {
        if (position < currentListArtists.size())
            return currentListArtists.get(position);
        else{
            return null;
        }
    }

    public void makeCurrentBySearch(String st) {
        currentListArtists = new ArrayList<Artist>();
        for (int i = 0; i < artists.size(); i++) {
            if (artists.get(i).hasInName(st)) {
                currentListArtists.add(artists.get(i));
            }
        }
    }

    public void setArtists(ArrayList<Artist> art){
        artists = art;
        currentListArtists = art;
    }

    public ArrayList<Artist> getArtists(){
        return currentListArtists;
    }

}
