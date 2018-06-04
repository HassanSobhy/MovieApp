package com.example.android.movieapp.utilities;

/**
 * Created by hassa on 2/18/2018.
 */

public class VideosModel {

    private String key ;
    private String author;
    private String content;


    public String getKey() {
        return key;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public VideosModel(String key, String author, String content) {

        this.key = key;
        this.author = author;
        this.content = content;
    }
}
