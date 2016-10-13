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
    private final String DATA_STREAM ="https://top-fwz1.mail.ru/tracker?js=13;id=650785;e=RT/beat;sid=fe1d288b;ids=650785;ver=60;_=0.6539773291352935"; //"http://online.radiorecord.ru:8101/rr_128";
    private MediaPlayer mediaPlayer;
    private AudioManager am;

    public MusicPlayer(Object player){
        am = (AudioManager) player;

    }
    public void testPlay() throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(DATA_STREAM);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
        return mediaPlayer.getCurrentPosition();
    }
    public int getDuration(){
        return  mediaPlayer.getDuration();
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
