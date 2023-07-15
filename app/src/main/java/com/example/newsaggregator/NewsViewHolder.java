package com.example.newsaggregator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsViewHolder extends RecyclerView.ViewHolder{

    TextView news_title;
    TextView date_time;
    TextView news_source;
    TextView news_content;
    TextView page_count;
    ImageView news_img;


    public NewsViewHolder(@NonNull View itemView) {

        super(itemView);

         news_title = itemView.findViewById(R.id.news_title);
         date_time = itemView.findViewById(R.id.date_time);
         news_source = itemView.findViewById(R.id.news_source);
         news_content = itemView.findViewById(R.id.news_content);
         page_count = itemView.findViewById(R.id.page_count);
         news_img = itemView.findViewById(R.id.news_image);

    }

}
