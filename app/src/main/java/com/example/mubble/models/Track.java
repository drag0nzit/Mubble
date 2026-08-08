package com.example.mubble.models;

public class Track {

    private String id;
    private String title;
    private String artist;
    private String albumId;
    private int duration;
    private String coverUrl;
    private String audioUrl;

    public Track() {
    }

    public Track(String id,
                 String title,
                 String artist,
                 String albumId,
                 int duration,
                 String coverUrl,
                 String audioUrl) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
        this.duration = duration;
        this.coverUrl = coverUrl;
        this.audioUrl = audioUrl;
    }

    public String getId() { return id; }

    public String getTitle() { return title; }

    public String getArtist() { return artist; }

    public String getAlbumId() { return albumId; }

    public int getDuration() { return duration; }

    public String getCoverUrl() { return coverUrl; }

    public String getAudioUrl() { return audioUrl; }
}