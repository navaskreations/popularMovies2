package com.example.android.popularmovies2;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmovies2.database.Movies;

/**
 * Created by navas on 6/18/2018.
 * Used to Display details about Movies
 */

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentThatStartedThisActivity = getIntent();

        // Get the Movie object and display the details to UI.
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("parcel_data")) {
                Movies currentMovie = intentThatStartedThisActivity.getExtras().getParcelable("parcel_data");

                if (currentMovie != null) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("movie",currentMovie);

                    DetailActivityFragment detailFragment = new DetailActivityFragment();
                    detailFragment.setArguments(arguments);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.Movie_detail_container, detailFragment).commit();
                }

            }
        }
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