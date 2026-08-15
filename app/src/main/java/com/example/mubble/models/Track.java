package com.example.mubble.models;

public class Track {

    private String id;
    private String title;
    private String artist;
    private String album;
    private String audioUrl;
    private String coverUrl;
    private int duration;

    public Track() {
        // Нужен Firebase Firestore
    }

    public Track(
            String id,
            String title,
            String artist,
            String album,
            String audioUrl,
            String coverUrl,
            int duration) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.audioUrl = audioUrl;
        this.coverUrl = coverUrl;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}