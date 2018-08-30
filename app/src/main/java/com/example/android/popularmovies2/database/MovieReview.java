package com.example.android.popularmovies2.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class with Movie Review related Information.
 */

public class MovieReview implements Parcelable {

    private String mAuthor;
    private String mContent;

    public MovieReview(String author, String content) {
        this.mAuthor = author;
        this.mContent = content;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public MovieReview(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getAuthor());
        parcel.writeString(getContent());

    }

    // This is to de-serialize the object
    public static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
}
