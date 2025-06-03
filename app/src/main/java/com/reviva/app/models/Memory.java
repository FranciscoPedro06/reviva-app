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

    // Constructor
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

    // Getters
    public String getMemory_id() {
        return memory_id;
    }

    public String getUser_id(){
        return user_id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }
    public String getMedia_url(){
        return media_url;
    }

    public String getMedia_type(){
        return media_type;
    }

    public long getCreatedAt(){
        return createdAt;
    }

    public long getUnlockAt(){
        return unlockAt;
    }

    public boolean getIsUnlocked(){
        return isUnlocked;
    }

    // Setters
    public String setTitle(String title){
        return title;
    }
    public String setDescription(String description){
        return description;
    }
    public String setMedia_url(String media_url){
        return media_url;
    }
    public String setMedia_type(String media_type) {
        return media_type;
    }
    public long setCreatedAt(long createdAt){
        return createdAt;
    }
    public long setUnlockAt(long unlockAt){
        return unlockAt;
        }
    public boolean setIsUnlocked(boolean isUnlocked){
        return isUnlocked;
    }
}


