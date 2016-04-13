package fw.musicauthors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by John on 06.04.2016.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    ArrayList<Artist> artists;
    public RVAdapter(ArrayList<Artist> auth){
        artists = auth;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.author_on_first_screen, parent, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.personName.setText(artists.get(position).name);
        holder.personGenres.setText(artists.get(position).getGenresesString());
        holder.personTracks.setText(artists.get(position).getAlbumTrackString());
        artists.get(position).setSmallPicture(holder.personPhoto);
        holder.personTracks.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView personName;
        TextView personGenres;
        TextView personTracks;
        ImageView personPhoto;
        PersonViewHolder(View itemView) {
            super(itemView);
            personName = (TextView)itemView.findViewById(R.id.person_name);
            personGenres = (TextView)itemView.findViewById(R.id.person_genres);
            personTracks = (TextView)itemView.findViewById(R.id.person_tracks);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }
}

