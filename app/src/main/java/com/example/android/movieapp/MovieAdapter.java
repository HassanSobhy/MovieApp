package com.example.android.movieapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.movieapp.data.MovieContract;
import com.example.android.movieapp.utilities.MovieJsonModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hassa on 2/1/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    final private MovieItemClickListener mClickHandler ;

    private List<MovieJsonModel> movieData;

    Context context;

    private Cursor mCursor;

    MovieAdapter (Context context,MovieItemClickListener movieItemClickListener)
    {
        this.context = context;
        mClickHandler = movieItemClickListener ;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (movieData!=null)
        {
            MovieJsonModel mMovie = movieData.get(position);

            Uri uri = Uri.parse("http://image.tmdb.org/t/p/w780/");
            Uri uri1 = Uri.withAppendedPath(uri,mMovie.getPoster_path()).buildUpon().build();

            Picasso.with(context)
                    .load(uri1)
                    .into(holder.posterImageView);
        } else
        {
            mCursor.moveToPosition(position);
            String postrePath = mCursor.getString( mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
            Uri myUri = Uri.parse(postrePath);
            holder.posterImageView.setImageURI(myUri);

        }

    }

    @Override
    public int getItemCount() {

        if (movieData!=null)return movieData.size();
        else if (mCursor!=null)
        {
            if (mCursor.getCount()!=0) return mCursor.getCount();
            else return 0;
        }
        else
            return 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView posterImageView ;
        public MovieViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (movieData!=null)
            {
                MovieJsonModel movieJsonModel = movieData.get(getAdapterPosition());
                mClickHandler.onClick(movieJsonModel);
            }
            else
            {
                mCursor.moveToPosition(getAdapterPosition());
                mClickHandler.onClick(mCursor,getAdapterPosition()+1);
            }

        }
    }

    public void setMovieData(List<MovieJsonModel> movieData) {
        this.movieData = movieData;
        notifyDataSetChanged();
    }

    public interface MovieItemClickListener {
         void onClick(MovieJsonModel movieJsonModel );
         void onClick(Cursor cursor,int id );
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
