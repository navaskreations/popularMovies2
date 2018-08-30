package com.example.android.popularmovies2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.popularmovies2.database.Movies;
import com.example.android.popularmovies2.database.MoviesDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<Movies>> movieList;

    public MainViewModel(Application application) {
        super(application);
        MoviesDatabase database = MoviesDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        movieList = database.moviesDao().loadAllMovies();
    }

    public LiveData<List<Movies>> getMovies() {
               return movieList;
    }

}
