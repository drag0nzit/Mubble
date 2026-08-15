package com.example.mubble.models;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private String id;
    private String name;
    private List<String> trackIds;

    public Playlist() {
        trackIds = new ArrayList<>();
    }

    public Playlist(
            String id,
            String name,
            List<String> trackIds) {

        this.id = id;
        this.name = name;

        if (trackIds == null) {
            this.trackIds = new ArrayList<>();
        } else {
            this.trackIds = trackIds;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTrackIds() {
        if (trackIds == null) {
            trackIds = new ArrayList<>();
        }

        return trackIds;
    }

    public void setTrackIds(
            List<String> trackIds) {

        if (trackIds == null) {
            this.trackIds = new ArrayList<>();
        } else {
            this.trackIds = trackIds;
        }
    }
}