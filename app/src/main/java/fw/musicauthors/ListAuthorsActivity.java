package fw.musicauthors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


public class ListAuthorsActivity extends AppCompatActivity {
    RecyclerView recyclerAuthors;
    LinearLayout dontConnectionLayout;
    FrameLayout allAuthorthFrameLayout;
    ProgressBar progressBarBeforeList;
    TextView mainTitleTextView;
    GestureDetector mGestureDetector;

    DBManager dbManager;

    UpdateControl updateControl;
    Context context;

    public static String URL_TO_JSON = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_authors);

        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setExitTransition(fade);

        setInitialValues();
        initiateActionBar();
        setInitialRecyclerView();
        dbManager.read();
        updateControl = new UpdateControl();
        updateControl.everyHalfHour();
    }
    private void initiateActionBar() {
            SearchView searchView = (SearchView) findViewById(R.id.search_view);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ArtistsContainer.instance.makeCurrentBySearch(newText);
                    setListForRecyclerAuthorsView(ArtistsContainer.instance.getArtists());
                    return false;
                }
            });
    }

    private void setInitialValues(){
        context = this;
        dbManager = new DBManager(this);
        dbManager.addUpgradeListener(new DBManager.IDBUpgrade() {
            @Override
            public void onUpgrade(ArrayList<Artist> artists) {
                allAuthorthFrameLayout.setVisibility(View.VISIBLE);
                dontConnectionLayout.setVisibility(View.INVISIBLE);
                ArtistsContainer.instance.setArtists(artists);
                setListForRecyclerAuthorsView(artists);
            }
        });

        dbManager.addNoConnectionListener(new DBManager.INoInternet() {
            @Override
            public void noConnection() {
                allAuthorthFrameLayout.setVisibility(View.INVISIBLE);
                dontConnectionLayout.setVisibility(View.VISIBLE);
            }
        });

        progressBarBeforeList =  (ProgressBar)findViewById(R.id.progressBarBeforeList);
        dontConnectionLayout = (LinearLayout) findViewById(R.id.dontConnectLayout);
        allAuthorthFrameLayout = (FrameLayout) findViewById(R.id.authorsFrameLayout);
        mainTitleTextView = (TextView) findViewById(R.id.titleMainScreen);


        if (dbManager.isEmpty()){
            progressBarBeforeList.setVisibility(View.VISIBLE);
        }
        else{
            progressBarBeforeList.setVisibility(View.INVISIBLE);
        }
        dontConnectionLayout.setVisibility(View.INVISIBLE);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    private void setInitialRecyclerView(){
        recyclerAuthors = (RecyclerView)findViewById(R.id.recyclerAuthors);
        // set Layout manager
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerAuthors.setLayoutManager(llm);
        // set Adapter
        RVAdapter adapter = new RVAdapter(new ArrayList<Artist>());
        recyclerAuthors.setAdapter(adapter);
        //set divider between items
        recyclerAuthors.addItemDecoration(new DividerItemDecoration(this));
        recyclerAuthors.setHasFixedSize(true);

        recyclerAuthors.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mGestureDetector.onTouchEvent(e)) {
                    Intent myIntent = new Intent(ListAuthorsActivity.this, AuthorActivity.class);
                    myIntent.putExtra("Author", rv.getChildPosition(childView));
                    ListAuthorsActivity.this.startActivity(myIntent);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /**
     * Update recycler view for show the other list of artists (for example in case of searching)
     * @param artists list of artists
     */
    private void setListForRecyclerAuthorsView(ArrayList<Artist> artists) {
        RVAdapter adapter = new RVAdapter(artists);
        recyclerAuthors.setAdapter(adapter);
    }

    /**
     * For repeat connection to server for JSON-data
     * @param view
     */
    public void onClickRepeatConnectionButton(View view) {
        if (view.getId() == R.id.repeatConnectionButton) {
            dbManager.download();
        }
    }

    public void updateData() {
        dbManager.download();
        Toast.makeText(this,"Обновление данных", Toast.LENGTH_SHORT).show();
    }

    public class UpdateControl {
        private final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        public void everyHalfHour() {
            final Runnable notifier = new Runnable() {
                public void run() {
                    updateData();
                }};
            final ScheduledFuture notifierHandle =
                    scheduler.scheduleAtFixedRate(notifier, 10, 10, SECONDS);
            scheduler.schedule(new Runnable() {
                public void run () {
                    notifierHandle.cancel(true);
                }
            },1*60,SECONDS);
        }
    }
}