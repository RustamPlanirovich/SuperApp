package com.example.superapp;


import java.util.Map;

public class MusicResponse {
    private String title;
    private String duration;
    private String comments;



    public MusicResponse() {
    }

    public MusicResponse(String title, String duration, String comments) {
        this.title = title;
        this.duration = duration;
        this.comments = comments;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
