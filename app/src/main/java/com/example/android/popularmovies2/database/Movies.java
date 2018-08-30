package com.example.android.popularmovies2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navas on 6/23/2018.
 * Movies Class with Movie related Information.
 */

@Entity
public class Movies implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int mId;
    public String mTitle;
    public String mThumbnail;
    public String mPlotSynopsis;
    public int mUserRating;
    public String mReleaseDate;
    @Ignore
    private ArrayList<String> mTrailer;
    @Ignore
    private ArrayList<MovieReview> mReview = new ArrayList<MovieReview>();

    // Constructor for loading values into Database
    public Movies(int mId, String mTitle, String mThumbnail, String mPlotSynopsis, int mUserRating, String mReleaseDate) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mThumbnail = mThumbnail;
        this.mPlotSynopsis = mPlotSynopsis;
        this.mUserRating = mUserRating;
        this.mReleaseDate = mReleaseDate;
        this.mTrailer = null;
        this.mReview = null;
    }

    @Ignore
    public Movies(int mId, String mTitle, String mThumbnail, String mPlotSynopsis, int mUserRating, String mReleaseDate,ArrayList<String> videos, ArrayList<MovieReview> review){
        this.mId = mId;
        this.mTitle = mTitle;
        this.mThumbnail = mThumbnail;
        this.mPlotSynopsis = mPlotSynopsis;
        this.mUserRating = mUserRating;
        this.mReleaseDate = mReleaseDate;
        mTrailer = videos;
        mReview = review;
    }

    @Ignore
    public Movies(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mThumbnail = in.readString();
        mPlotSynopsis = in.readString();
        mUserRating = in.readInt();
        mReleaseDate = in.readString();
        mTrailer = in.createStringArrayList();
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        in.readTypedList(this.mReview, MovieReview.CREATOR); // Should work now
    }
    public int getmId() {return mId; }

    public String getmTitle() {
        return mTitle;
    }

    public String getmThumbnail() {
        return mThumbnail;
    }

    public int getmUserRating() {
        return mUserRating;
    }

    public String getmPlotSynopsis() {
        return mPlotSynopsis;
    }

    public String getmReleaseData() {
        return mReleaseDate;
    }

    public ArrayList<String> getmTrailer() {
        return mTrailer;
    }

    public List<MovieReview> getmReview() {
        return mReview;
    }

    public void setmVideos(ArrayList<String> video) {
        mTrailer = video;
    }

    public void setmReview(ArrayList<MovieReview> review) {
        mReview = review;
    }

    @Override
    public String toString() {
        return "Movies{" +
                "mTitle='" + mTitle + '\'' +
                ", mThumbnail='" + mThumbnail + '\'' +
                ", mPlotSynopsis=" + mPlotSynopsis +
                ", mUserRating=" + Integer.toString(mUserRating) +
                ", mReleaseDate=" + mReleaseDate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mThumbnail);
        parcel.writeString(mPlotSynopsis);
        parcel.writeInt(mUserRating);
        parcel.writeString(mReleaseDate);
        parcel.writeStringList(mTrailer);
        parcel.writeTypedList(mReview);
    }

    // This is to de-serialize the object
    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}