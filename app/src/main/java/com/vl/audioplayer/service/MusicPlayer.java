package com.vl.audioplayer.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.vl.audioplayer.entities.Track;
import com.vl.audioplayer.entities.PlayList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MusicPlayer {
    private Context context;
    private ArrayList<PlayerListener> listeners = new ArrayList<>();
    private Integer indexCurrentTrack = 0;
    private MediaPlayer player;
    private PlayList currentPlayList;
    private boolean isPaused = true;

    public MusicPlayer(Context context){
        this.context = context;
    }
    public void addListener(PlayerListener listener){
        listeners.add(listener);
    }

    private void createPlayer(Context context, String path) throws IOException {

        if(player!=null)player.release();
        player = MediaPlayer.create(context, Uri.fromFile(new File(path)));

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mp==null)return;
                mp.release();
                player = new MediaPlayer();
                play(++indexCurrentTrack);
                for (PlayerListener listener :
                        listeners) {
                    listener.onEoF(indexCurrentTrack);
                }
            }
        });
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }


    public void pause()
    {
        setPaused(true);
        player.pause();
    }
    public void play() {
        if (getDuration() <= 0)
            play(indexCurrentTrack);
        if (isPaused) {
            player.start();
            setPaused(false);
        }
    }
    public void play(Integer i) {

        indexCurrentTrack = i;
        if (currentPlayList.getTracks().size() == 0) return;
        if (currentPlayList.getTracks().size() <= i) i = 0;
        if (i < 0) i = currentPlayList.getTracks().size() - 1;

        try {
            createPlayer(context,currentPlayList.getTracks().get(i).getPath());
            player.start();
            setPaused(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void next(){
        play(++indexCurrentTrack);
        for (PlayerListener listener :
                listeners) {
            listener.onEoF(indexCurrentTrack);
        }
    }
    public void prev(){
        play(--indexCurrentTrack);
        for (PlayerListener listener :
                listeners) {
            listener.onEoF(indexCurrentTrack);
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

    public PlayList getCurrentPlayList() {
        return currentPlayList;
    }

    public void setCurrentPlayList(PlayList currentPlayList) {
        this.currentPlayList = currentPlayList;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void setPaused(boolean paused) {
        isPaused = paused;
        for (PlayerListener listener :
                listeners) {
            listener.onIsPausedChanged(paused);
        }
    }
    public Track getCurrentTrack(){
        if(currentPlayList!=null)
        return currentPlayList.getTracks().get(indexCurrentTrack);
        else return new Track();
    }

    public static abstract class PlayerListener {
        abstract public void onEoF(int position);
        abstract public void onIsPausedChanged(boolean isPaused);
    }
}
