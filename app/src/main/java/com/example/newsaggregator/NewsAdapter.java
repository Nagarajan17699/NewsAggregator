package com.example.newsaggregator;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    public final MainActivity mainActivity;
    public final ArrayList<ArticleClass> articleClasses;
    private static final String TAG = "NewsAdapter";

    public NewsAdapter(MainActivity mainActivity, ArrayList<ArticleClass> articleClasses) {
        this.articleClasses = articleClasses;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        ArticleClass articleClass = articleClasses.get(position);

        if(articleClass.getTitle().equalsIgnoreCase("") || articleClass.getTitle().equalsIgnoreCase("null"))
            holder.news_title.setVisibility(View.GONE);
        else
            holder.news_title.setText(articleClass.getTitle());

        if(articleClass.getAuthor().equalsIgnoreCase("") || articleClass.getAuthor().equalsIgnoreCase("null"))
            holder.news_source.setVisibility(View.GONE);
        else
            holder.news_source.setText(articleClass.getAuthor());

        if(articleClass.getPublishedAt().equalsIgnoreCase("") || articleClass.getPublishedAt().equalsIgnoreCase("null"))
            holder.date_time.setVisibility(View.GONE);
        else
            holder.date_time.setText(getDateTime(articleClass.getPublishedAt()));

        if(articleClass.getDescription().equalsIgnoreCase("") || articleClass.getDescription().equalsIgnoreCase("null"))
            holder.news_content.setVisibility(View.GONE);
        else
            holder.news_content.setText(articleClass.getDescription());

        holder.page_count.setText(String.format("%d out of %d", position+1, articleClasses.size()));
        loadImage(holder.news_img, articleClass.getUtlToImage());

    }

    @Override
    public int getItemCount() {
        return articleClasses.size();
    }

    private void loadImage(ImageView imageView, String urlString){
        try {
            long start = System.currentTimeMillis();

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.brokenimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);


            Glide.with(imageView)
                    .load((!urlString.equals("")) ? urlString : R.drawable.noimage)
                    .apply(options)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Log.d(TAG, "onLoadFailed: " + e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            long time = System.currentTimeMillis() - start;
                            //Log.d(TAG, "onResourceReady: " + time);
                            return false;
                        }
                    })
                    .into(imageView);
        } catch(Exception e){
            Log.d(TAG, "loadImage: Image Failed to Load");
        }
    }

    public String getDateTime(String date_time){
        try {
            String dateTime = date_time.substring(0, 19);
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            String formattedDate = dateTimeFormatter.format(localDateTime);
            return formattedDate;
        } catch (Exception e){
           // e.printStackTrace();
        }
        return "";
    }
}
