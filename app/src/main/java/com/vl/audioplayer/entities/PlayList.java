package com.vl.audioplayer.entities;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;

@DatabaseTable
public class PlayList {
    @DatabaseField(generatedId = true)
    private int id;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<TrackPlayList> tracksForeign;
    private ArrayList<Track> tracks = new ArrayList<>();
    @DatabaseField
    private String name;


    public ArrayList<Track> getTracks() {
        convertToList();
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {

        this.tracks = tracks;
        convertToForeign();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void convertToList(){
        tracks.clear();
        if(tracksForeign!=null)
        for(TrackPlayList track:tracksForeign){
            tracks.add(track.getTrack());
        }
    }
    private void convertToForeign(){
        for(Track track:tracks){
            TrackPlayList trackPlayList = new TrackPlayList();
            trackPlayList.setTrack(track);
            trackPlayList.setPlayList(PlayList.this);
            tracksForeign.add(trackPlayList);
        }

    }
}
