package com.example.android.movieapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by hassa on 2/23/2018.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.movieapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_OVER_VIEW = "over_view";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "content";

    }
}
