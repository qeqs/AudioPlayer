package com.vl.audioplayer;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MusicPlayer player;
    ArrayList tracks;
    boolean is_playing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton menuButton = (FloatingActionButton) findViewById(R.id.menuButton);
        final FloatingActionButton playButton = (FloatingActionButton) findViewById(R.id.playButton);

        player = new MusicPlayer(getSystemService(Context.AUDIO_SERVICE));

        MusicSearcher searcher = new MusicSearcher();
        try {
            tracks =(ArrayList)searcher.find(MusicSearcher.getExternalSdCardPath(),"mp3");

        } catch (Exception e) {
            e.printStackTrace();
        }
        menuButton.setOnClickListener(viewClickListener);
        ///TODO: вынести отдельно
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.playButton:
                        if (player.isPlaying()) {
                            playButton.setImageResource(android.R.drawable.ic_media_play);
                            player.pause();
                        }
                        else {
                            playButton.setImageResource(android.R.drawable.ic_media_pause);
                            try {
                                if(!player.continuePlay())
                                        player.play(tracks.get(0).toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;


                }
                ((TextView)findViewById(R.id.trackInfo)).setText("Time " + player.getCurrentPosition() + " / "
                        + player.getDuration());
            }
        });

    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
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
}