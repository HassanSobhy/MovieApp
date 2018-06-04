package com.example.android.movieapp.utilities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hassa on 2/18/2018.
 */

public class VideosResult {
    public List<VideosModel> getResults() {
        return results;
    }
    private List<VideosModel> results;

}
