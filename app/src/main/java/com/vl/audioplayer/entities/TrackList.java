package com.vl.audioplayer.entities;

import com.j256.ormlite.field.DatabaseField;

import java.util.List;


public class TrackList {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private List<Track> tracks;
    @DatabaseField
    private String name;


    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
