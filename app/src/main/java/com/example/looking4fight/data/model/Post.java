package com.example.looking4fight.data.model;

public class Post {
    private String name; // Matches Firestore field
    private String mediaUrl;
    private String title;
    private String description;
    private String userId;
    private long timestamp;
    private long likesCount;
    private String postId;

    // Default constructor required for Firebase
    public Post() {}

    // Constructor for manual post creation
    public Post(String mediaUrl, String title, String description, String userId, long timestamp) {
        this.mediaUrl = mediaUrl != null ? mediaUrl : "";
        this.title = title != null ? title : "";
        this.description = description != null ? description : "";
        this.userId = userId != null ? userId : "";
        this.timestamp = timestamp;
        this.likesCount = likesCount;
    }

    // Getters (Firebase requires these for mapping)
    public String getMediaUrl() { return mediaUrl; }

    public String getTitle() { return title; }
    public String getPostId() {return postId;}
    public String getDescription() { return description; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
    public long getLikesCount() {return likesCount;}


    // Setters (Needed for Firebase deserialization)
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public void setPostId(String postId) { this.postId = postId; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public void setLikeCount(long likeCount) { this.likesCount = likesCount; }
}

