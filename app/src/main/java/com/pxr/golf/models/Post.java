package com.pxr.golf.models;

public class Post {
    String url;
    String image;

    public Post(String url, String image) {
        this.url = url;
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }
}
