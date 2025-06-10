package com.reviva.app.models;

public class Memory {
    private String memoryId;
    private String userId;
    private String title;
    private String description;
    private String mediaUrl;
    private String mediaType;
    private long createdAt;
    private long unlockAt;
    private boolean isUnlocked;

    // Constructs
    public Memory() {}


    public Memory(String memoryId, String userId, String title, String description,
                  String mediaUrl, String mediaType, long createdAt, long unlockAt, boolean isUnlocked) {
        this.memoryId = memoryId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.createdAt = createdAt;
        this.unlockAt = unlockAt;
        this.isUnlocked = isUnlocked;
    }

    // Getters
    public String getMemoryId() {
        return memoryId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUnlockAt() {
        return unlockAt;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    // Setters
    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUnlockAt(long unlockAt) {
        this.unlockAt = unlockAt;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}
