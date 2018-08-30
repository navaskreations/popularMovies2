package com.example.android.popularmovies2.Utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.popularmovies2.database.MovieReview;
import com.example.android.popularmovies2.database.Movies;
import com.example.android.popularmovies2.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PopularMoviesUtils {
    private static String TAG = PopularMoviesUtils.class.getSimpleName();


    /**
     * This Method will get the json response, parse and populate the movies details into the Movies Object Instance.
     *
     * @param context
     * @param responseJsonStr The response from the weburl
     * @return ArrayList of Movies object, where Movies object holds the details about the movie.
     * @throws JSONException related to  parsing JSON string
     */
    public static ArrayList<Movies> getMovieslistfromJSON(Context context, String responseJsonStr)
            throws JSONException {

        ArrayList<Movies> parsedMovieList = new ArrayList<>();

        if (responseJsonStr != null) {
            try {
                JSONObject jsonResponse = new JSONObject(responseJsonStr);

                //Getting JSONArray of results
                JSONArray movieslist = jsonResponse.getJSONArray(context.getString(R.string.results));

                //Get details about the movies
                for (int i = 0; i < movieslist.length(); i++) {
                    JSONObject movie = movieslist.getJSONObject(i);
                    int id = movie.optInt(context.getString(R.string.id));
                    String title = movie.optString(context.getString(R.string.title));
                    String posterPath = movie.optString(context.getString(R.string.thumbnail));
                    String overview = movie.optString(context.getString(R.string.plot_synopsis));
                    int rating = movie.optInt(context.getString(R.string.user_rating));
                    String releaseDate = movie.optString(context.getString(R.string.releasedate));

                    //Populate the Movies object with movie details and stack up in arrayList.
                    parsedMovieList.add(new Movies(id, title, posterPath, overview, rating, releaseDate,null,null));
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }
        return parsedMovieList;
    }

    public static ArrayList<String> getVideosFromMovieJSON(Context context, String responseJsonStr)
            throws JSONException {

        ArrayList<String> parsedVideoList = new ArrayList<>();

        if (responseJsonStr != null) {
            try {
                JSONObject jsonResponse = new JSONObject(responseJsonStr);

                //Getting JSONArray of results
                JSONArray videosList = jsonResponse.getJSONArray(context.getString(R.string.results));

                //Get details about the movies
                for (int i = 0; i < videosList.length(); i++) {
                    JSONObject video = videosList.getJSONObject(i);
                    String type = video.optString(context.getString(R.string.type));
                    if (type.equalsIgnoreCase(context.getString(R.string.Trailer))) {
                        String key = video.optString(context.getString(R.string.key));
                        String site = video.optString(context.getString(R.string.site));
                        String temp;
                        if(site.equalsIgnoreCase(context.getString(R.string.YouTube))) {
                            //Populate the Movies object with movie details and stack up in arrayList.
                            Log.i(TAG, "id : " + key + "  https://www.youtube.com/watch?v=" + key);
                            // parsedMovieList.add(new Movies(id, title, posterPath, overview, rating, releaseDate));
                            temp = "https://www.youtube.com/watch?v=" + key;
                        }else{
                            temp = key;
                        }
                        parsedVideoList.add(temp);
                    }

                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }
        return parsedVideoList;
    }

    public static ArrayList<MovieReview> getReviewsFromMovieJSON(Context context, String responseJsonStr)
            throws JSONException {
        ArrayList<MovieReview> parsedReviewList = new ArrayList<MovieReview>();

        if (responseJsonStr != null) {
            try {
                JSONObject jsonResponse = new JSONObject(responseJsonStr);

                //Getting JSONArray of results
                JSONArray reviewList = jsonResponse.getJSONArray(context.getString(R.string.results));

                //Get details about the movies
                for (int i = 0; i < reviewList.length(); i++) {
                    JSONObject movie = reviewList.getJSONObject(i);
                    String author = movie.optString(context.getString(R.string.author));
                    String content = movie.optString(context.getString(R.string.content));
                    Log.i(TAG, "author : " +author + "  content: " +content);

                    //Populate the Movies object with movie details and stack up in arrayList.
                    // parsedMovieList.add(new Movies(id, title, posterPath, overview, rating, releaseDate));
                   // map.put(author,content);

                    parsedReviewList.add(new MovieReview(author,content));

                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }
        return parsedReviewList;
    }


    /**
     * This Method is used to dynamically populate the UI with movie poster from the image URL.
     *
     * @param imgview UI to populate with poster image.
     * @param url     Image URL to load from Web.
     */
    public static void fillPicaso(final ImageView imgview, final String url) {

        //Load Image using picasso
        Picasso.with(imgview.getContext())
                .load(url)
                .into(imgview, new Callback() {

                            @Override
                            public void onSuccess() {
                                //Success image already loaded into the view so do nothing
                            }

                            @Override
                            public void onError() {
                                //Error, do further handling of this situation here
                                imgview.setVisibility(View.GONE);
                                Toast.makeText(imgview.getContext(), "Error Loading Image link : " + url, Toast.LENGTH_LONG).show();

                            }
                        }
                );
    }
}
