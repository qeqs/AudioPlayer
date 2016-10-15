package com.vl.audioplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.content.ContentUris;
import java.io.IOException;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by kvakin on 13.10.2016.
 */

public class MusicPlayer {
  private MediaPlayer mediaPlayer;
    private AudioManager am;

    public MusicPlayer(Object player){
        am = (AudioManager) player;

    }

    public void play(String source) throws IOException {
        releaseMP();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(source);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }
    public void pause(){
        if(mediaPlayer == null)return;
        mediaPlayer.stop();
    }
    public boolean isPlaying(){
        if(mediaPlayer==null)return false;
        return mediaPlayer.isPlaying();

    }
    public boolean continuePlay(){

        try {
            if(mediaPlayer==null)return false;
            mediaPlayer.start();
        }
        catch (IllegalStateException e){
            return false;
        }
        return true;
    }
    public int getCurrentPosition(){
        if(mediaPlayer==null)return 0;
        return mediaPlayer.getCurrentPosition()/1000;
    }
    public int getDuration(){
        if(mediaPlayer==null)return 0;
        return  mediaPlayer.getDuration()/1000;
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
