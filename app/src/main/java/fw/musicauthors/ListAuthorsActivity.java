package fw.musicauthors;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ListAuthorsActivity extends AppCompatActivity {
    LoadJSON loader;
    RecyclerView recyclerAuthors;
    LinearLayout dontConnectionLayout;
    FrameLayout allAuthorthFrameLayout;
    ProgressBar progressBarBeforeList;
    TextView mainTitleTextView;
    public static String URL_TO_JSON = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_authors);
        setInitialValues();
        initiateActionBar();
        if (ArtistsContainer.instance.isEmpty())
            loadAuthors();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mainTitleTextView.setVisibility(View.INVISIBLE);
        super.onNewIntent(intent);
    }

    private void initiateActionBar() {
            SearchView searchView = (SearchView) findViewById(R.id.search_view);
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //findViewById(R.id.titleMainScreen).setVisibility(View.INVISIBLE);
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //findViewById(R.id.titleMainScreen).setVisibility(View.VISIBLE);
                    return false;
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ArtistsContainer.instance.makeCurrentBySearch(newText);
                    setListForRecyclerAuthorsView(ArtistsContainer.instance.currentListArtists);
                    return false;
                }
            });
    }

    private void setInitialValues(){
        setTitle("Исполнители");

        progressBarBeforeList =  (ProgressBar)findViewById(R.id.progressBarBeforeList);
        progressBarBeforeList.setVisibility(View.VISIBLE);

        dontConnectionLayout = (LinearLayout) findViewById(R.id.dontConnectLayout);
        dontConnectionLayout.setVisibility(View.INVISIBLE);

        allAuthorthFrameLayout = (FrameLayout) findViewById(R.id.authorsFrameLayout);
        allAuthorthFrameLayout.setVisibility(View.VISIBLE);

        mainTitleTextView = (TextView) findViewById(R.id.titleMainScreen);

        setInitialRecyclerView();
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
        // add on item click listener -> create new activiry for more information
        recyclerAuthors.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent myIntent = new Intent(ListAuthorsActivity.this, AuthorActivity.class);
                myIntent.putExtra("Author", position);
                ListAuthorsActivity.this.startActivity(myIntent);
            }
        }));
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
     * when application is started this method download JSON file
     * about artists from server.
     */
    private void loadAuthors() {
        if (ArtistsContainer.instance.isEmpty()) {
            //do ansy task for download
            loader = new LoadJSON();
            loader.execute(URL_TO_JSON);
        }
        else {
            //for case when activity was re-created during using the application
            findViewById(R.id.progressBarBeforeList).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Make ArtistsContainer from given JSONArray. Set this date to recyclerView on 1 activitty
     * @param jsonArray
     */
    public void createAuthorsList(JSONArray jsonArray){
        //for case when activity was re-created during using the application
        if (!ArtistsContainer.instance.isEmpty())
            return;

        if (jsonArray != null) {
            ArtistsContainer.instance.fillContainerByJSON(jsonArray);
            findViewById(R.id.progressBarBeforeList).setVisibility(View.INVISIBLE);
            setListForRecyclerAuthorsView(ArtistsContainer.instance.artists);
            allAuthorthFrameLayout.setVisibility(View.VISIBLE);
            dontConnectionLayout.setVisibility(View.INVISIBLE);
        }
        else{
            //if connection was not establish - JSONArray is empty.
            //Show activity with information that connection is not establish
            allAuthorthFrameLayout.setVisibility(View.INVISIBLE);
            dontConnectionLayout.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_list_authors, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            if (isSearchingNow){
//                isSearchingNow = false;
//                searchTextField.setVisibility(View.INVISIBLE);
//                searchTextField.setEnabled(false);
//                findViewById(R.id.titleMainScreen).setVisibility(View.VISIBLE);
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(searchTextField.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//                File f = new File("minmap/delete.png");
//                if(f.exists()){
//                    Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
//                    BitmapDrawable drawable = new BitmapDrawable(myBitmap);
//                    action_search.setIcon(drawable);
//                }
//            }
//            else {
//                isSearchingNow = true;
//                searchTextField.setVisibility(View.VISIBLE);
//                searchTextField.setEnabled(true);
//                searchTextField.requestFocus();
//                findViewById(R.id.titleMainScreen).setVisibility(View.INVISIBLE);
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                File f = new File("minmap/search.png");
//                if(f.exists()){
//                    Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
//                    BitmapDrawable drawable = new BitmapDrawable(myBitmap);
//                    action_search.setIcon(drawable);
//                }
//
//            }
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * For repeat connection to server for JSON-data
     * @param view
     */
    public void onClickRepeatConnectionButton(View view) {
        if (view.getId() == R.id.repeatConnectionButton) {
            loadAuthors();
        }
    }

    public class LoadJSON extends AsyncTask<String, Void, JSONArray> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        @Override
        protected JSONArray doInBackground(String... urls) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
                JSONArray authorsJSON = null;
                authorsJSON = new JSONArray(resultJson);
                return authorsJSON;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            createAuthorsList(jsonArray);
        }
    }

}

class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}
