package com.reviva.app.models;

public class Memory {
    private String memory_id;
    private String user_id;
    private String title;
    private String description;
    private String media_url;
    private String media_type;
    private long createdAt;
    private long unlockAt;
    private boolean isUnlocked;

    public Memory(String memory_id, String user_id, String title, String description, String media_url, String media_type, long createdAt, long unlockAt, boolean isUnlocked){
        this.memory_id = memory_id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.media_url = media_url;
        this.media_type = media_type;
        this.createdAt = createdAt;
        this.unlockAt = unlockAt;
        this.isUnlocked = isUnlocked;

    }

}


