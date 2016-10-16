package com.vl.audioplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kvakin on 13.10.2016.
 */

public class MusicPlayer {
  private MediaPlayer mediaPlayer;
    private AudioManager am;
    private ArrayList playList;
    private String curTrackName;
    private boolean ready = false;
    public MusicPlayer(Object player,ArrayList tracks){
        am = (AudioManager) player;

        this.playList = tracks;
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(playList.get(MainActivity.index).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(onStop);

    }
    MediaPlayer.OnCompletionListener onStop =  new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(!ready){return;}
            try {
                    play(++MainActivity.index,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    public void setPlayList(ArrayList tracks){

        this.playList = tracks;
    }
    public void setReady(){
        ready = !ready;
    }
    public boolean getReady(){
        return ready;
    }
    public void play(int i,boolean is_continue) throws IOException {

        if (!is_continue) {
            releaseMP();

            if (i >= playList.size()) i = (MainActivity.index = 0);
            if (i < 0) i = (MainActivity.index = playList.size() - 1);
            if (playList.size() > 0) {
                mediaPlayer.setDataSource(playList.get(i).toString());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
                curTrackName = ((File) playList.get(i)).getName();
            }
        } else mediaPlayer.start();
    }
    public void pause(){
        mediaPlayer.pause();
    }
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();

    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition()/1000;
    }
    public void setCurrentPosition(int pos){
        mediaPlayer.seekTo(pos*1000);
    }
    public int getDuration(){
        return  mediaPlayer.getDuration()/1000;
    }
    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnCompletionListener(onStop);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getCurTrackName() {
        return curTrackName==null?" ":curTrackName;
    }
    @Override
    protected void finalize() throws Throwable {
        mediaPlayer.release();
        super.finalize();

    }
}
