package com.example.android.movieapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.movieapp.data.MovieContract;
import com.example.android.movieapp.data.MovieDbHelper;
import com.example.android.movieapp.utilities.MovieJsonModel;
import com.example.android.movieapp.utilities.JsonResultModel;
import com.example.android.movieapp.utilities.VideosResult;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Loader;
import android.content.CursorLoader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the movie data loader
     */
    private static final int ID_MOVIE_LOADER = 44;
    private RecyclerView recyclerView;

    private MovieAdapter movieAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    GridLayoutManager layoutManager;



    MovieApi movieApi = null;
    Call<JsonResultModel> connection;

    List<MovieJsonModel> movies;
    ArrayList<MovieJsonModel> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_movie);
        layoutManager = new GridLayoutManager(this, numberOfColumns());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this, this);
        recyclerView.setAdapter(movieAdapter);




        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        movieApi = retrofit.create(MovieApi.class);
        connection = movieApi.getTopRatedMovies();


        getConnection();





        if(savedInstanceState!=null)
        {
            List<MovieJsonModel> a =savedInstanceState.getParcelableArrayList("movies");
            movieAdapter.setMovieData((List<MovieJsonModel>)a);
            recyclerView.scrollToPosition(savedInstanceState.getInt("Position"));
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        int positionView = ((GridLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        state.putParcelableArrayList("movies",arrayList);
        state.putInt("Position", positionView);
    }



    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    @Override
    public void onClick(MovieJsonModel movieJsonModel) {

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.INTENT_TYPE_EXTRA, "Json");
        intent.putExtra(DetailActivity.TITLE_EXTRA, movieJsonModel.getTitle());
        intent.putExtra(DetailActivity.POSTE_PATH_EXTRA, movieJsonModel.getPoster_path());
        intent.putExtra(DetailActivity.RELEASE_DATE_EXTRA, movieJsonModel.getRelease_date());
        intent.putExtra(DetailActivity.VOTE_AVERAGE_EXTRA, movieJsonModel.getVote_average());
        intent.putExtra(DetailActivity.OVERVIEW_EXTRA, movieJsonModel.getOverview());
        intent.putExtra(DetailActivity.ID_EXTRA, movieJsonModel.getId());
        startActivity(intent);
    }

    @Override
    public void onClick(Cursor cursor, int id) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.INTENT_TYPE_EXTRA, "Cursor");
        intent.putExtra(DetailActivity.TITLE_EXTRA, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        intent.putExtra(DetailActivity.POSTE_PATH_EXTRA, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)));
        intent.putExtra(DetailActivity.RELEASE_DATE_EXTRA, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        intent.putExtra(DetailActivity.VOTE_AVERAGE_EXTRA, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVG)));
        intent.putExtra(DetailActivity.OVERVIEW_EXTRA, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVER_VIEW)));
        intent.putExtra(DetailActivity.AUTHOR_EXTRA, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR)));
        intent.putExtra(DetailActivity.CONTENT_EXTRA, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT)));
        Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
        intent.setData(uri);
        startActivity(intent);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, MainActivity.this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // read data from database

        String[] projection = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_VOTE_AVG,
                MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR,
                MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT,
                MovieContract.MovieEntry.COLUMN_OVER_VIEW,
                MovieContract.MovieEntry.COLUMN_POSTER
        };

        return new android.support.v4.content.CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI, projection, null, null, MovieContract.MovieEntry._ID);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        recyclerView.smoothScrollToPosition(mPosition);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);

    }

    public interface MovieApi {
        @GET("top_rated?api_key=f7186053daaec4b2c3591d5726d3a328")
        Call<JsonResultModel> getTopRatedMovies();

        @GET("popular?api_key=f7186053daaec4b2c3591d5726d3a328")
        Call<JsonResultModel> getPopularMovies();

        // stage 2

        @GET("videos?api_key=f7186053daaec4b2c3591d5726d3a328&language=en-US")
        Call<VideosResult> getVideos();

        // stage 2
        @GET("reviews?api_key=f7186053daaec4b2c3591d5726d3a328&language=en-US")
        Call<VideosResult> getReviews();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.top_rated_action:
                connection = movieApi.getTopRatedMovies();
                getConnection();
                return true;
            case R.id.most_popular_action:
                connection = movieApi.getPopularMovies();
                getConnection();
                return true;
            case R.id.favourite_action:
                getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, MainActivity.this);
                movieAdapter.setMovieData(null);
                recyclerView.setAdapter(movieAdapter);
                return true;
            default:
                return true;

        }
    }

    public void getConnection() {
        connection.enqueue(new Callback<JsonResultModel>() {
            @Override
            public void onResponse(Call<JsonResultModel> call, Response<JsonResultModel> response) {
                movies = response.body().getResults();
                arrayList = new ArrayList<MovieJsonModel>(movies);
                if (movies == null)
                    Toast.makeText(MainActivity.this, "Error !", Toast.LENGTH_LONG).show();
                else {
                    movieAdapter.setMovieData(movies);

                }

            }

            @Override
            public void onFailure(Call<JsonResultModel> call, Throwable t) {
                getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, MainActivity.this);
            }

        });
    }

}
