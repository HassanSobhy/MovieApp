package com.example.android.movieapp.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hassa on 2/2/2018.
 */

// model for json

public class MovieJsonModel implements Parcelable{

    private String title;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("vote_average")
    @Expose
    private String voteAverage;
    @SerializedName("overview")
    @Expose
    private String overView;

    private int id;



    public MovieJsonModel(String title, String release_date, String poster_path, String vote_average, String overview, int id, String key) {
        this.title = title;
        this.releaseDate = release_date;
        this.posterPath = poster_path;
        this.voteAverage = vote_average;
        this.overView = overview;
        this.id = id;




    }


    protected MovieJsonModel(Parcel in) {
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readString();
        overView = in.readString();
        id = in.readInt();
    }



    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease_date() {
        return releaseDate;
    }

    public String getPoster_path() {
        return posterPath;
    }

    public String getVote_average() {
        return voteAverage;
    }

    public String getOverview() {
        return overView;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
        parcel.writeString(voteAverage);
        parcel.writeString(overView);
        parcel.writeInt(id);
    }
    public static final Creator<MovieJsonModel> CREATOR = new Creator<MovieJsonModel>() {
        @Override
        public MovieJsonModel createFromParcel(Parcel in) {
            return new MovieJsonModel(in);
        }

        @Override
        public MovieJsonModel[] newArray(int size) {
            return new MovieJsonModel[size];
        }
    };
}
