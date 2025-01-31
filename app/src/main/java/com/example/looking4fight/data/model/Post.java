package com.example.looking4fight.data.model;

public class Post {
    private String imageUrl;
    private String caption;
    private String username;
    private int likeCount;

    public Post(String imageUrl, String caption, String username, int likeCount) {
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.username = username;
        this.likeCount = likeCount;
    }

    // Default constructor for Firebase
    public Post() {
        this.imageUrl = "";
        this.caption = "";
        this.username = "";
        this.likeCount = 0;
    }

    // Getter methods
    public String getImageUrl() { return imageUrl; }
    public String getCaption() { return caption; }
    public String getUsername() { return username; }
    public int getLikeCount() { return likeCount; }

    // Alias method for getText() (since caption is the post text)
    public String getText() { return caption; }
}
