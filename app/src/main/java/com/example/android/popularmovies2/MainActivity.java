package com.example.android.popularmovies2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies2.Utilities.NetworkUtils;
import com.example.android.popularmovies2.Utilities.PopularMoviesUtils;
import com.example.android.popularmovies2.database.MovieReview;
import com.example.android.popularmovies2.database.Movies;
import com.example.android.popularmovies2.database.MoviesDatabase;
import com.example.android.popularmovies2.userpreference.SettingsActivity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<Movies>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private MoviesAdapter mAdapter;
    private MoviesAdapter mAdapter2;

    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private static final int MOVIES_LOADER_ID = 0;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    // Member variable for the Database
    private MoviesDatabase mDb;

    String keyForSortBy;
    String defaultUnits;
    String favourite;
    String PARAM_VALUE;
    String preferredSortBy;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //If preference is updated restart the loader.
        preferredSortBy = PreferenceManager.getDefaultSharedPreferences(this).getString(keyForSortBy, defaultUnits);

        if (favourite.equals(preferredSortBy))
            mRecyclerView.setAdapter(mAdapter2);
        else {
            mRecyclerView.setAdapter(mAdapter);
            //viewModel.getTasks().removeObserver(this);
        }
        Log.d(TAG, "onStart: preferences were updated");


        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            if (favourite.equals(preferredSortBy)) {
                loadDb();
            }else
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
                PREFERENCES_HAVE_BEEN_UPDATED = false;
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // binding view
        ButterKnife.bind(this);

        // setupDB Instance
        mDb = MoviesDatabase.getInstance(getApplicationContext());

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // SetupLoader
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        LoaderManager.LoaderCallbacks<ArrayList<Movies>> callback = MainActivity.this;

        //SetupPreference
        Log.d(TAG, "onCreate: registering preference changed listener");
        /* Register MainActivity as an OnPreferenceChangedListener.*/
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        setUpPreference();

        // Setup recyclerview
        setUpRecyclerView();

        if (favourite.equals(preferredSortBy))
            loadDb();
        else {
            Bundle bundleLoader = null;
            getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, bundleLoader, callback);
        }
    }


    @Override
    public void onClick(Movies currentMovie) {
        //Instantiate detail activity and pass the Movie object.
        Context context = this;
        Class intentClass = DetailActivity.class;

        //LogMovie(currentMovie);
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("parcel_data", currentMovie);
        startActivity(intent);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id     The ID whose loader is to be created.
     * @param bundle Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<ArrayList<Movies>> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<Movies>>(this) {
            ArrayList<Movies> mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This method will load and parse the JSON data
             * from WebURL in the background.
             *
             * @return Movies information from Web as an array of Movies.
             */
            @Override
            public ArrayList<Movies> loadInBackground() {
                URL popularMoviesUrl;
                URL trailerUrl;
                URL reviewUrl;
                ArrayList<Movies> mFinalMovieData = new ArrayList<>();

                popularMoviesUrl = NetworkUtils.buildAPIUrl(MainActivity.this);
                try {
                    Context context = MainActivity.this;
                    //Get preference (popular/top-rated/favorite) from user settings.
                    String results = NetworkUtils.getResponseFromHttpUrl(popularMoviesUrl);
                    mMovieData = PopularMoviesUtils.getMovieslistfromJSON(MainActivity.this, results);

                    if (mMovieData != null && mMovieData.size() > 0) {
                      for (Movies m : mMovieData) {
                            int id = m.getmId();
                            //Trailer
                            trailerUrl = NetworkUtils.buildTrailerUrl(MainActivity.this, id);
                            results = NetworkUtils.getResponseFromHttpUrl(trailerUrl);

                           // Log.i(TAG, "TrailerUrl :" + trailerUrl);
                           // Log.i(TAG, "Trailer Results :" + results);

                            ArrayList<String> trailerLinks = new ArrayList<String>();
                            trailerLinks = PopularMoviesUtils.getVideosFromMovieJSON(MainActivity.this, results);
                            m.setmVideos(PopularMoviesUtils.getVideosFromMovieJSON(MainActivity.this, results));

                            //Review
                            reviewUrl = NetworkUtils.buildReviewUrl(MainActivity.this, id);
                           // Log.i(TAG, "ReviewUrl :" + reviewUrl);
                            results = NetworkUtils.getResponseFromHttpUrl(reviewUrl);
                           // Log.i(TAG, "Review Results :" + results);
                            m.setmReview(PopularMoviesUtils.getReviewsFromMovieJSON(MainActivity.this, results));
                            if (m != null)
                                mFinalMovieData.add(m);
                        }
                    }
                    return mFinalMovieData;
                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(ArrayList<Movies> data) {
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<Movies>> loader, ArrayList<Movies> data) {
        UpdateUI(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<ArrayList<Movies>> loader) {

    }

    private void invalidateData() {
        mAdapter.setMovieData(null);
        mAdapter2.setMovieData(null);
    }

    private void showDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);

        preferredSortBy = PreferenceManager.getDefaultSharedPreferences(this).getString(keyForSortBy, defaultUnits);
        if (favourite.equals(preferredSortBy)) {
            mErrorMessageDisplay.setText(R.string.error_message_fav);
        }else{
            mErrorMessageDisplay.setText(R.string.error_message);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle Settings Menu
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            invalidateData();
            preferredSortBy = PreferenceManager.getDefaultSharedPreferences(this).getString(keyForSortBy, defaultUnits);
            if (favourite.equals(preferredSortBy))
                loadDb();
            else
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    public void loadDb() {
       final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getMovies().observe(MainActivity.this, new Observer<List<Movies>>() {
            @Override
            public void onChanged(@Nullable List<Movies> movies) {
                Log.d(TAG, "Receiving Database Updates from LiveData");
                if (movies != null && movies.size() > 0) {
                    if (favourite.equals(preferredSortBy))
                        mAdapter2.setMovieData(movies);
                    showDataView();
                } else {
                    showErrorMessage();
                }
            }
        });


    }

    public void UpdateUI(ArrayList<Movies> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null && data.size() > 0) {
                mAdapter.setMovieData(data);
            showDataView();
        } else {
            showErrorMessage();
        }
    }

    public void setUpPreference(){
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        keyForSortBy = this.getString(R.string.pref_sortby_key);
        defaultUnits = this.getString(R.string.pref_sortby_popular);
        favourite = this.getString(R.string.pref_sortby_favourite);

                preferredSortBy = prefs.getString(keyForSortBy, defaultUnits);

    }

    public void setUpRecyclerView(){
        // Set Grid Layout and Adapter to Recycler View
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        int noOfCol = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, noOfCol);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new MoviesAdapter(this);
        mAdapter2 = new MoviesAdapter(this);
        if (favourite.equals(preferredSortBy))
            mRecyclerView.setAdapter(mAdapter2);
        else
            mRecyclerView.setAdapter(mAdapter);
    }

    public void LogMovie(Movies currentMovie){
        Log.i(TAG,
                String.valueOf("ID: " +currentMovie.getmId()) +" \n"
                        +currentMovie.getmTitle() + " \n"
                +currentMovie.getmPlotSynopsis() + " \n"
                +currentMovie.getmReleaseData() + " \n"
                +currentMovie.getmThumbnail() + " \n"
                +currentMovie.getmUserRating() +" \n"
        +currentMovie.getmTrailer().size() + " \n"
        +currentMovie.getmReview().size());

        for(String s : currentMovie.getmTrailer())
            Log.i(TAG + " Trailer : ",
                    s + "\n");

        for(MovieReview s : currentMovie.getmReview())
            Log.i(TAG + " Review : ", "Author: " +
                    s.getAuthor() + "\n" + "Content: " + s.getContent());
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}