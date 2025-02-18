package com.example.looking4fight.data.model;

public class Post {
    private String imageUrl;
    private String caption;
    private String username;
    private int likeCount;

    public Post(String imageUrl, String caption, String username, int likeCount)
    {
        this.imageUrl = imageUrl != null ? imageUrl : "";
        this.caption = caption != null ? caption : "";
        this.username = username != null ? username : "";
        this.likeCount = Math.max(likeCount, 0); // Ensures likes are not negative
    }

    // Default constructor for Firebase
    public Post()
    {
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

    // Setter methods in case you need to modify values later
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl != null ? imageUrl : ""; }
    public void setCaption(String caption) { this.caption = caption != null ? caption : ""; }
    public void setUsername(String username) { this.username = username != null ? username : ""; }
    public void setLikeCount(int likeCount) { this.likeCount = Math.max(likeCount, 0); } // Prevents negative likes

}
