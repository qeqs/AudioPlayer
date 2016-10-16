package com.vl.audioplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static int index = 0;
    MusicPlayer player;
    ArrayList tracks;
    FloatingActionButton playButton;
    FloatingActionButton nextButton;
    FloatingActionButton prevButton;
    FloatingActionButton menuButton;
    SeekBar bar;
    TextView name;
    MyTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuButton = (FloatingActionButton) findViewById(R.id.menuButton);
        playButton = (FloatingActionButton) findViewById(R.id.playButton);
        nextButton = (FloatingActionButton) findViewById(R.id.nextButton);
        prevButton = (FloatingActionButton) findViewById(R.id.prevButton);
        bar = ((SeekBar)findViewById(R.id.seekBar));
        name = (TextView)findViewById(R.id.trackName);

        MusicSearcher searcher = new MusicSearcher();
        try {
            tracks =(ArrayList)searcher.find(MusicSearcher.getExternalSdCardPath(),"mp3");
            name.setText(index+": "+((java.io.File)tracks.get(index)).getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        player = new MusicPlayer(getSystemService(Context.AUDIO_SERVICE),tracks);
        menuButton.setOnClickListener(viewMenuClickListener);
        playButton.setOnClickListener(viewPlayerClickListener);
        nextButton.setOnClickListener(viewPlayerClickListener);
        prevButton.setOnClickListener(viewPlayerClickListener);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    player.setCurrentPosition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        timer = new MyTimer(Integer.MAX_VALUE,500);
        timer.start();
    }


    View.OnClickListener viewPlayerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.playButton:
                    if (player.isPlaying()) {
                        playButton.setImageResource(android.R.drawable.ic_media_play);
                        player.pause();
                    } else {

                        playButton.setImageResource(android.R.drawable.ic_media_pause);
                        try {

                            player.play(index, player.getReady());
                            if (!player.getReady())
                                player.setReady();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.nextButton:
                    try {
                        player.play(++index,false);
                        playButton.setImageResource(android.R.drawable.ic_media_pause);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.prevButton:
                    try {
                        player.play(--index,false);
                        playButton.setImageResource(android.R.drawable.ic_media_pause);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    };
    View.OnClickListener viewMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };


    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popupmenu);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.menu1:
                                Toast.makeText(getApplicationContext(),
                                        "Вы выбрали PopupMenu 1",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menuAbout:
                                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                                startActivity(intent);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
        popupMenu.show();
    }
    private class MyTimer extends CountDownTimer
    {

        public MyTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish()
        {

            timer = new MyTimer(Integer.MAX_VALUE,1000);
            timer.start();
        }

        public void onTick(long millisUntilFinished)
        {
            name.setText(index+": "+player.getCurTrackName());

            bar.setMax(player.getDuration());
            bar.setProgress(player.getCurrentPosition());
            ((TextView)findViewById(R.id.trackInfo)).setText("Time " + player.getCurrentPosition()/60+":"+player.getCurrentPosition()%60 + " / "
                    + player.getDuration()/60+":"+player.getDuration()%60);
        }

    }
}