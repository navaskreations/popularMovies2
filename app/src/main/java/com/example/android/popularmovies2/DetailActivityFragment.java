package com.example.android.popularmovies2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies2.Utilities.NetworkUtils;
import com.example.android.popularmovies2.Utilities.PopularMoviesUtils;
import com.example.android.popularmovies2.database.MovieReview;
import com.example.android.popularmovies2.database.Movies;
import com.example.android.popularmovies2.database.MoviesDatabase;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.lang.String.valueOf;


public class DetailActivityFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = DetailActivityFragment.class.getSimpleName();

    @BindView(R.id.tv_detail_title) TextView mtvTitle;
    @BindView(R.id.tv_detail_thumbnail) ImageView mtvThumbnail;
    @BindView(R.id.tv_detail_plotsynopsis) TextView mtvPlotSynopsis;
    @BindView(R.id.tv_detail_rating) TextView mtvrating;
    @BindView(R.id.tv_detail_releasedate) TextView mtvReleaseDate;
    @BindView(R.id.iv_favourite) ImageView mivfavorite;
    @BindView(R.id.tv_trailer_label) TextView mtvTrailerLabel;
    @BindView(R.id.Trailer_container) LinearLayout mTrailer;
    @BindView(R.id.tv_review_label) TextView mtvReviewLabel;
    @BindView(R.id.Review_container) LinearLayout mReview;
    //@BindView(R.id.trailer) TextView tvTrailer;

    Movies movie;
    int count = 0;
    int Id;
    String Title;
    String Thumbnail;
    String PlotSynopsis;
    int UserRating;
    String ReleaseDate;


    // Member variable for the Database
    private MoviesDatabase mDb;

    //create DB Instance
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = MoviesDatabase.getInstance(getActivity().getApplicationContext());
    }

    //inflate layout and bind values
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail,container,false);
        ButterKnife.bind(this,rootView);
        mivfavorite.setOnClickListener(this);
        return rootView;
    }

    // Update UI
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null)
        {
            Movies movie = (Movies) getArguments().get("movie");
            if (movie != null)
            {
                mtvTrailerLabel.setVisibility(View.GONE);
                mTrailer.setVisibility(View.GONE);
                mtvReviewLabel.setVisibility(View.GONE);
                mReview.setVisibility(View.GONE);
                this.movie = movie;
                showMovieDetails(movie);

            }
        }
    }

    public void showMovieDetails(final Movies movie)
    {
        LogMovie(movie);
        Id = movie.getmId();
        Title = movie.getmTitle();
        Thumbnail = movie.getmThumbnail();
        PlotSynopsis = movie.getmPlotSynopsis();
        UserRating = movie.getmUserRating();
        ReleaseDate = movie.getmReleaseData();

        //Movie Title
        mtvTitle.setText(Title);

        //Movie Poster Path
        URL posterPathUrl;
        posterPathUrl = NetworkUtils.buildIMGUrl(Thumbnail);
        PopularMoviesUtils.fillPicaso(mtvThumbnail, posterPathUrl.toString());

        //Movie Overview
        mtvPlotSynopsis.setText(PlotSynopsis);

        //Movie rating
        int rating = UserRating;
        String ratingValue = Integer.toString(rating) + "/10";
        mtvrating.setText(ratingValue);

        //Movie release Date
        mtvReleaseDate.setText(ReleaseDate);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //Mark/UnMark Movie Favourite
                count = mDb.moviesDao().isFavouriteMovie(Id);
                //Toast.makeText(getContext(), "Count  " +count +" Movie .", Toast.LENGTH_LONG).show();
                if( count <= 0 )
                    mivfavorite.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_star_border_black_24dp));
                else
                    mivfavorite.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_star_yellow_24dp));

            }
        });

        if( movie.getmTrailer() != null )
            showTrailers(movie.getmTrailer());
        if( movie.getmReview() != null )
             showReviews(movie.getmReview());
    }

    //diaplay Trailers
    public void showTrailers(ArrayList<String> trailerURL)
    {
            mTrailer.removeAllViews();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            int i = 1;

            for (final String trailer : trailerURL)
            {
                mtvTrailerLabel.setVisibility(View.VISIBLE);
                mTrailer.setVisibility(View.VISIBLE);

                ViewGroup trailerContainer = (ViewGroup) inflater.inflate(R.layout.trailer_display, mTrailer, false);
                TextView tvTrailer = (TextView) trailerContainer.findViewById(R.id.trailer);

                tvTrailer.setText(getContext().getString(R.string.Trailer) + " " + String.valueOf(i));
                i++;
                tvTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri webpage = Uri.parse(trailer);
                        Intent intent = new Intent(Intent.ACTION_VIEW,webpage);

                        if(intent.resolveActivity(getActivity().getPackageManager())!=null){
                            startActivity(intent);
                        }
                    }
                });
                mTrailer.addView(trailerContainer);
            }
    }

    //Display Reviews
    public void showReviews(List<MovieReview> reviews)
    {
            mReview.removeAllViews();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            for (MovieReview review : reviews)
            {
                mtvReviewLabel.setVisibility(View.VISIBLE);
                mReview.setVisibility(View.VISIBLE);

                ViewGroup reviewContainer = (ViewGroup) inflater.inflate(R.layout.review_display, mReview, false);
                TextView reviewAuthor = (TextView) reviewContainer.findViewById(R.id.review_author);
                TextView reviewContent = (TextView) reviewContainer.findViewById(R.id.review_content);
                reviewAuthor.setText(review.getAuthor());
                reviewContent.setText(review.getContent());
                mReview.addView(reviewContainer);
            }
    }


    @Override
    public void onClick(View v) {
              Drawable resId;

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mivfavorite.getBackground().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.ic_star_border_black_24dp).getConstantState())) {
                    mivfavorite.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_star_yellow_24dp));
                    Movies movie = new Movies(Id,Title,Thumbnail,PlotSynopsis,UserRating,ReleaseDate);
                    // insert new movie
                    //Toast.makeText(getContext(), "Adding " +Title +" Movie to your Favourite List.", Toast.LENGTH_LONG).show();
                    Log.i(TAG,"Adding " +Title +" Movie to your Favourite List.");
                    mDb.moviesDao().insertMovie(movie);

                }else{
                    mivfavorite.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_star_border_black_24dp));
                    // delete elected movie
                    //Toast.makeText(getContext(), "Deleting " +Title +" Movie from your Favourite List.", Toast.LENGTH_LONG).show();
                    Log.i(TAG,"Deleting " +Title +" Movie from your Favourite List.");
                    mDb.moviesDao().deleteMovie(movie);
                    int count = mDb.moviesDao().isMovieAvailable();
                    if ( count == 0 )
                    getActivity().onBackPressed();
                }


            }
        });
    }
    public void LogMovie(Movies movie) {
        Log.i(TAG,
                +movie.getmId() + "\n"
                        + movie.getmTitle() + "\n"
                        + movie.getmThumbnail() + "\n"
                        + movie.getmPlotSynopsis() + "\n"
                        + movie.getmUserRating() + "\n"
                        + movie.getmReleaseData());
    }
}

