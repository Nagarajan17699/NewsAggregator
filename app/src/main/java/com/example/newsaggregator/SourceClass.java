package com.example.newsaggregator;

public class SourceClass {

    private String id;
    private String name;
    private String category;

    public SourceClass(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
}
