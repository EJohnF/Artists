package fw.musicauthors;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AuthorActivity extends AppCompatActivity {

    private float x1, x2;
    static final int MIN_DISTANCE = 100;
    ImageView photo;
    Artist artist;
    TextView genres;
    TextView tracks;
    TextView description;
    TextView link;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        initialization();
        updateViews();
    }

    /**
     * initialize toolbar + find views
     */
    private void initialization(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        artist = ArtistsContainer.instance.getSelectedArtist();
        genres = (TextView) findViewById(R.id.textGenres);
        tracks = (TextView) findViewById(R.id.textTracks);
        description = (TextView) findViewById(R.id.textDescription);
        photo = (ImageView) findViewById(R.id.authorImageView);
        link = (TextView) findViewById(R.id.viewLinkToSite);
    }

    /**
     * Set content to views by artist field
     */
    private void updateViews() {
        if (artist != null) {
            setTitle(artist.name);
            artist.setBigPicture(photo);
            genres.setText(artist.getGenresesString());
            tracks.setText(artist.getAlbumTrackString());
            description.setText(artist.description);

            if (artist.link_string != ""){
                link.setVisibility(View.VISIBLE);
                link.setText("Сайт: " + artist.link_string);
            }
            else{
                link.setVisibility(View.INVISIBLE);
            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (deltaX > MIN_DISTANCE) {
                    finish();
                }
                break;
        }
        return false;
    }
}
