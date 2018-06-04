package com.example.android.movieapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.MainActivity.MovieApi;

import com.example.android.movieapp.data.MovieContract;
import com.example.android.movieapp.utilities.JsonResultModel;
import com.example.android.movieapp.utilities.MovieJsonModel;
import com.example.android.movieapp.utilities.VideosModel;
import com.example.android.movieapp.utilities.VideosResult;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.android.movieapp.data.MovieContract.MovieEntry;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_MOVIE_LOADER_DETAIL = 35;

    static final String TITLE_EXTRA = "title";
    static final String RELEASE_DATE_EXTRA = "release_date";
    static final String POSTE_PATH_EXTRA = "poster_path";
    static final String VOTE_AVERAGE_EXTRA = "vote_average";
    static final String OVERVIEW_EXTRA = "over_view";
    static final String ID_EXTRA = "id";
    static final String INTENT_TYPE_EXTRA = "intent_type";
    static final String AUTHOR_EXTRA = "author";
    static final String CONTENT_EXTRA = "content";
    //  two static variable,
    public static int scrollX = 0;
    public static int scrollY = -1;



    String title;
    String releaseDate;
    String posterPath;
    String voteAverage;
    String overView;
    int id;
    String author;
    String content;
    String intentExtra;


    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAvgTextView;
    private TextView mOverViewTextView;
    private ImageView mPosterImageView;
    private TextView mAuthorTextView;
    private TextView mContentTextView;
    private Button mAddFavButton;
    private ScrollView scrollView;


    MovieApi movieApi;
    Call<VideosResult> connection;
    Call<VideosResult> connection2;


    TrailerAdapter trailerAdapter;
    ListView mVideoslListView;
    ArrayList<TrailerModel> arrayList;

    ArrayList<MovieJsonModel> arrayList1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = findViewById(R.id.tv_title);
        mReleaseDateTextView = findViewById(R.id.tv_release_date);
        mVoteAvgTextView = findViewById(R.id.tv_vote_avg);
        mOverViewTextView = findViewById(R.id.tv_over_view);
        mPosterImageView = findViewById(R.id.iv_detail_poster);
        mAuthorTextView = findViewById(R.id.author);
        mContentTextView = findViewById(R.id.content);
        mAddFavButton = findViewById(R.id.addFav);

        Intent intent = getIntent();
        intentExtra = intent.getStringExtra(INTENT_TYPE_EXTRA);

        if (intent != null && intentExtra.equals("Json")) {


            if (intent.hasExtra(TITLE_EXTRA)) {
                title = intent.getStringExtra(TITLE_EXTRA);
            }
            if (intent.hasExtra(RELEASE_DATE_EXTRA)) {
                releaseDate = intent.getStringExtra(RELEASE_DATE_EXTRA);
            }
            if (intent.hasExtra(POSTE_PATH_EXTRA)) {
                posterPath = intent.getStringExtra(POSTE_PATH_EXTRA);
            }
            if (intent.hasExtra(VOTE_AVERAGE_EXTRA)) {
                voteAverage = intent.getStringExtra(VOTE_AVERAGE_EXTRA);
            }
            if (intent.hasExtra(OVERVIEW_EXTRA)) {
                overView = intent.getStringExtra(OVERVIEW_EXTRA);
            }
            if (intent.hasExtra(ID_EXTRA)) {
                id = intent.getIntExtra(ID_EXTRA, 0);
            }


            String baseUrl = "https://api.themoviedb.org/3/movie/" + String.valueOf(id) + "/";
            Retrofit retrofita = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            movieApi = retrofita.create(MovieApi.class);
            connection = movieApi.getVideos();
            connection2 = movieApi.getReviews();

            arrayList = new ArrayList<>();
            connection.enqueue(new Callback<VideosResult>() {
                @Override
                public void onResponse(Call<VideosResult> call, Response<VideosResult> response) {
                    final List<VideosModel> videos = response.body().getResults();
                    for (int i = 0; i < videos.size(); i++) {
                        arrayList.add(new TrailerModel(videos.get(i).getKey()));
                    }
                    trailerAdapter = new TrailerAdapter(DetailActivity.this, arrayList);
                    mVideoslListView = findViewById(R.id.list_view);
                    mVideoslListView.setAdapter(trailerAdapter);
                    fixListView(mVideoslListView);
                    mVideoslListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String url = "https://www.youtube.com/watch?v=" + arrayList.get(i).key;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onFailure(Call<VideosResult> call, Throwable t) {

                }
            });

            connection2.enqueue(new Callback<VideosResult>() {
                @Override
                public void onResponse(Call<VideosResult> call, Response<VideosResult> response) {
                    List<VideosModel> videos = response.body().getResults();

                    if (videos != null && videos.size() != 0) {
                        mAuthorTextView.setText(videos.get(0).getAuthor());
                        mContentTextView.setText(videos.get(0).getContent());
                    }
                }

                @Override
                public void onFailure(Call<VideosResult> call, Throwable t) {

                }
            });



        } else {
            mAddFavButton.setText(getResources().getString(R.string.unfavorites));
            if (intent.hasExtra(TITLE_EXTRA)) {
                title = intent.getStringExtra(TITLE_EXTRA);
            }
            if (intent.hasExtra(RELEASE_DATE_EXTRA)) {
                releaseDate = intent.getStringExtra(RELEASE_DATE_EXTRA);
            }
            if (intent.hasExtra(POSTE_PATH_EXTRA)) {
                posterPath = intent.getStringExtra(POSTE_PATH_EXTRA);
            }
            if (intent.hasExtra(VOTE_AVERAGE_EXTRA)) {
                voteAverage = intent.getStringExtra(VOTE_AVERAGE_EXTRA);
            }
            if (intent.hasExtra(OVERVIEW_EXTRA)) {
                overView = intent.getStringExtra(OVERVIEW_EXTRA);
            }
            if (intent.hasExtra(ID_EXTRA)) {
                id = intent.getIntExtra(ID_EXTRA, 0);
            }
            if (intent.hasExtra(AUTHOR_EXTRA)) {
                author = intent.getStringExtra(AUTHOR_EXTRA);
            }
            if (intent.hasExtra(CONTENT_EXTRA)) {
                content = intent.getStringExtra(CONTENT_EXTRA);
            }
            final Uri uri = intent.getData();
            mAddFavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] a = new String[]{title};
                    getContentResolver().delete(uri, null, a);
                    finish();
                }
            });
        }

        displayData();
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER_DETAIL, null, DetailActivity.this);
        scrollView = findViewById(R.id.scroll_view);
        if (savedInstanceState!=null)
        {
            final int[] position = savedInstanceState.getIntArray("position");
            if(position != null)
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.scrollTo(position[0], position[1]);
                    }
                });
        }



    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putIntArray("position",
                new int[]{ scrollView.getScrollX(), scrollView.getScrollY()});


    }

    public static void fixListView(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) return;

        int width = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int height = 0;
        View view = null;
        int mCount = adapter.getCount();
        for (int i = 0; i < mCount; i++) {
            view = adapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(width, View.MeasureSpec.UNSPECIFIED);
            height += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = height + (listView.getDividerHeight() * (mCount - 1));
        listView.setLayoutParams(params);
    }


    public void displayData() {
        mTitleTextView.setText(title);
        mReleaseDateTextView.setText(releaseDate);
        mVoteAvgTextView.setText(voteAverage);
        mOverViewTextView.setText(overView);

        if (intentExtra.equals("Json")) {
            Uri uri = Uri.parse("http://image.tmdb.org/t/p/w780/");
            Uri uri1 = Uri.withAppendedPath(uri, posterPath).buildUpon().build();

            Picasso.with(this)
                    .load(uri1)
                    .into(mPosterImageView);
        } else {
            Uri uri = Uri.parse(posterPath);
            mPosterImageView.setImageURI(uri);
            mAuthorTextView.setText(author);
            mContentTextView.setText(content);

        }



    }



    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
        String[] a = new String[]{title};
        return new android.support.v4.content.CursorLoader(this, ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,1), projection, null, a,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount()!= 0) {
            mAddFavButton.setText(getResources().getString(R.string.unfavorites));
            mAddFavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] a = new String[]{title};
                    getContentResolver().delete(ContentUris.withAppendedId(MovieEntry.CONTENT_URI, 1), null, a);
                    finish();
                }
            });
        } else {

            mAddFavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieEntry.COLUMN_TITLE, mTitleTextView.getText().toString());
                    contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, mReleaseDateTextView.getText().toString());
                    contentValues.put(MovieEntry.COLUMN_VOTE_AVG, mVoteAvgTextView.getText().toString());
                    contentValues.put(MovieEntry.COLUMN_REVIEW_AUTHOR, mAuthorTextView.getText().toString());
                    contentValues.put(MovieEntry.COLUMN_REVIEW_CONTENT, mContentTextView.getText().toString());
                    contentValues.put(MovieEntry.COLUMN_OVER_VIEW, mOverViewTextView.getText().toString());

                    Drawable drawable = mPosterImageView.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                    ContextWrapper contextWrapper = new ContextWrapper(DetailActivity.this);
                    File mFile = contextWrapper.getDir("Images", MODE_PRIVATE);
                    mFile = new File(mFile, mTitleTextView.getText().toString() + ".jpg");

                    try {
                        OutputStream stream = new FileOutputStream(mFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        stream.flush();
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri savedImageURI = Uri.parse(mFile.getAbsolutePath());
                    contentValues.put(MovieEntry.COLUMN_POSTER, savedImageURI.toString());
                    getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
