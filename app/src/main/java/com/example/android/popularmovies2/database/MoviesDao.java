package com.example.android.popularmovies2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.popularmovies2.database.Movies;

import java.util.ArrayList;
import java.util.List;



@Dao
public interface MoviesDao {

    @Query("SELECT * FROM MOVIES")
    LiveData<List<Movies>> loadAllMovies();

    @Insert
    void insertMovie(Movies movies);

    @Delete
    void deleteMovie(Movies movies);

    @Query("SELECT * FROM MOVIES WHERE mId = :id")
    Movies loadMovieById(int id);

    @Query("SELECT COUNT(*) FROM MOVIES WHERE mId = :id")
    int isFavouriteMovie(int id);

    @Query("SELECT COUNT(*) FROM MOVIES")
    int isMovieAvailable();


}
