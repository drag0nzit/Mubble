package com.example.mubble.models;

public class Album {

    private String id;
    private String title;
    private String artist;
    private String coverUrl;
    private int year;

    public Album() {
    }

    public Album(String id,
                 String title,
                 String artist,
                 String coverUrl,
                 int year) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.coverUrl = coverUrl;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public int getYear() {
        return year;
    }
}