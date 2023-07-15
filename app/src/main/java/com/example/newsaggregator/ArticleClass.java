package com.example.newsaggregator;

public class ArticleClass {

    private String author;
    private String title;
    private String description;
    private String url;
    private String utlToImage;
    private String publishedAt;

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUtlToImage() {
        return utlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public ArticleClass(String author, String title, String description, String url, String utlToImage, String publishedAt) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.utlToImage = utlToImage;
        this.publishedAt = publishedAt;
    }
}
