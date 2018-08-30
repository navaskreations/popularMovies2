package com.example.android.popularmovies2.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popularmovies2.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by navas on 6/18/2018.
 * These Utilities will be used to communicate with web to fetch the movie details.
 * Note:Please add API Key in Strings.xml( PARAM_VALUE field )before testing.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private final static String BASE_URL =
            "https://api.themoviedb.org/3";

    private final static String IMG_BASE_URL =
            "https://image.tmdb.org/t/p/";

    private final static String IMG_SIZE = "w185";
    private final static String MOVIE_PATH = "movie";
    private final static String POPULAR_MOVIE_PATH = "popular";
    private final static String TOP_RATED_MOVIE_PATH = "top_rated";
    private final static String VIDEOS_PATH = "videos";
    private final static String REVIEWS_PATH = "reviews";


    /**
     * Builds the Web URL used to query Popular/Top-Rated Movies.
     *
     * @return The URL to use to query the Popular/Top-Rated Movies.
     */

    public static URL buildAPIUrl(Context context) {

        URL url = null;

        //Get preference (popular/top-rated) from user settings.
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForSortBy = context.getString(R.string.pref_sortby_key);
        String defaultUnits = context.getString(R.string.pref_sortby_popular);
        String popular = context.getString(R.string.pref_sortby_popular);


        String preferredSortBy = prefs.getString(keyForSortBy, defaultUnits);

        Uri builtUri;

        if (popular.equals(preferredSortBy)) {
            // get popular movie
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(MOVIE_PATH)
                    .appendEncodedPath(POPULAR_MOVIE_PATH)
                    .appendQueryParameter(context.getString(R.string.api_key), context.getString(R.string.api_key_value))
                    .build();
        } else {
            //get movie based on rating
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(MOVIE_PATH)
                    .appendEncodedPath(TOP_RATED_MOVIE_PATH)
                    .appendQueryParameter(context.getString(R.string.api_key), context.getString(R.string.api_key_value))
                    .build();
        }

        try {
            url = new URL(builtUri.toString());
            //Test Print
            Log.i(TAG, url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the Image URL used to query Movie Thumbnail/Poster Image.
     *
     * @param posterPath The path of the poster
     * @return The Image URL to use to query the Popular/Top-Rated Movies Poster/Thumbnail.
     */
    public static URL buildIMGUrl(String posterPath) {

        URL url = null;
        Uri builtUri;

        builtUri = Uri.parse(IMG_BASE_URL).buildUpon()
                .appendEncodedPath(IMG_SIZE)
                .appendEncodedPath(posterPath)
                .build();

        try {
            url = new URL(builtUri.toString());
            //Test Print
            Log.i(TAG, url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildTrailerUrl(Context context, int id) {

        URL url = null;

        Uri builtUri;

        builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(MOVIE_PATH)
                .appendEncodedPath(String.valueOf(id))
                .appendEncodedPath(VIDEOS_PATH)
                .appendQueryParameter(context.getString(R.string.api_key), context.getString(R.string.api_key_value))
                .build();
        try {
            url = new URL(builtUri.toString());
            //Test Print
            Log.i(TAG, url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildReviewUrl(Context context, int id) {

        URL url = null;

        Uri builtUri;

        builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(MOVIE_PATH)
                .appendEncodedPath(String.valueOf(id))
                .appendEncodedPath(REVIEWS_PATH)
                .appendQueryParameter(context.getString(R.string.api_key), context.getString(R.string.api_key_value))
                .build();
        try {
            url = new URL(builtUri.toString());
            //Test Print
            Log.i(TAG, url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}