package com.vl.audioplayer.service;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.vl.audioplayer.entities.Track;
import com.vl.audioplayer.entities.TrackList;

import java.io.IOException;


public class TestPlayer {

    public TestPlayer(){
        player = new MediaPlayer();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mp==null)return;
                mp.release();
                player = new MediaPlayer();
                play(++indexCurrentTrack);
            }
        });

    }


    private Integer indexCurrentTrack = 0;
    private MediaPlayer player;
    private TrackList currentTrackList;
    private boolean isPaused = true;
    public void pause()
    {
        setPaused(true);
        player.pause();
    }
    public void play() {
        if(!isPaused)
        player.start();
    }
    public void play(Integer i) {
        if (currentTrackList.getTracks().size() == 0) return;
        if (currentTrackList.getTracks().size() <= i) i = 0;

        try {
            player.setDataSource(currentTrackList.getTracks().get(i).toString());
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.prepare();
            player.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentPosition(){
        return player.getCurrentPosition()/1000;
    }
    public void setCurrentPosition(int pos){
        player.seekTo(pos*1000);
    }
    public int getDuration(){
        return  player.getDuration()/1000;
    }

    public TrackList getCurrentTrackList() {
        return currentTrackList;
    }

    public void setCurrentTrackList(TrackList currentTrackList) {
        this.currentTrackList = currentTrackList;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void setPaused(boolean paused) {
        isPaused = paused;
    }
    public Track getCurrentTrack(){
        return currentTrackList.getTracks().get(indexCurrentTrack);
    }
}
