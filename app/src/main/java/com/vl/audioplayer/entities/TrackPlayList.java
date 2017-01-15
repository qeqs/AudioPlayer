package com.vl.audioplayer.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class TrackPlayList {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true,foreignAutoCreate = true, foreignAutoRefresh = true)
    private Track track;
    @DatabaseField(foreign = true,foreignAutoCreate = true, foreignAutoRefresh = true)
    private PlayList playList;

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public PlayList getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
