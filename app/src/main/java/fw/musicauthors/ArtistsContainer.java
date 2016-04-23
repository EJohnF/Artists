package fw.musicauthors;

import java.util.ArrayList;

/**
 * Created by John on 07.04.2016.
 */
public class ArtistsContainer {
    public static ArtistsContainer instance = new ArtistsContainer();

    /**
     * All artists from bd or server
     */
    private ArrayList<Artist> artists;
    /**
     * list of artists which is displayed now
     */
    private ArrayList<Artist> currentListArtists;
    /**
     * last selected by user artists, or the first at the start of application
     */
    private Artist lastSelected = null;

    private ArtistsContainer() {
        artists = new ArrayList<>(350);
        currentListArtists = artists;
    }

    @Deprecated
    public Artist getArtistByPosition(int position) {
        if (currentListArtists.size() == 0){
            return artists.get(0);
        }
        if (position < currentListArtists.size())
            return currentListArtists.get(position);
        else{
            return null;
        }
    }

    /**
     * set selected Artist by position in current artists list
     * @param position
     */
    public void setSelectedArtist(int position){
        if (position < currentListArtists.size())
            lastSelected =  currentListArtists.get(position);
        else{
            lastSelected = artists.get(0);
        }
    }
    public Artist getSelectedArtist(){
        return lastSelected;
    }

    /**
     * Select to current ListArtist only such artist
     * which has substring st in their name
     * @param st
     */
    public void makeCurrentBySearch(String st) {
        currentListArtists = new ArrayList<Artist>();
        for (int i = 0; i < artists.size(); i++) {
            if (artists.get(i).hasInName(st)) {
                currentListArtists.add(artists.get(i));
            }
        }
    }

    /**
     * set whole list of artists
     * @param art
     */
    public void setArtists(ArrayList<Artist> art){
        artists = art;
        currentListArtists = art;
        if (lastSelected == null && art.size()>0){
            lastSelected = artists.get(0);
        }
    }

    public ArrayList<Artist> getCurrentArtists(){
        return currentListArtists;
    }

}
