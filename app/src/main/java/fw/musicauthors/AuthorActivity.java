package fw.musicauthors;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class AuthorActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        int position = 0;
        if(extras != null)
        {
            position = extras.getInt("Author");
        }
        Artist artist = ArtistsContainer.instance.getArtist(position);
        TextView genres = (TextView) findViewById(R.id.textGenres);
        TextView tracks = (TextView) findViewById(R.id.textTracks);
        TextView description = (TextView) findViewById(R.id.textDescription);
        ImageView photo = (ImageView) findViewById(R.id.authorImageView);
        if (artist != null) {
            setTitle(artist.name);
            artist.setBigPicture(photo);
            genres.setText(artist.getGenresesString());
            tracks.setText(artist.getAlbumTrackString());
            description.setText(artist.description);
        }
    }

}
