package com.vl.audioplayer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vl.audioplayer.R;

import java.util.Timer;


public class Splash extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 3000;            //set your time here......
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

       pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setMax(SPLASH_DISPLAY_LENGHT-1000);
        MyTimer timer = new MyTimer(SPLASH_DISPLAY_LENGHT,500);
        timer.start();
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

            Intent mainIntent = new Intent(Splash.this,MainActivity.class);
            Splash.this.startActivity(mainIntent);
            Splash.this.finish();
        }

        public void onTick(long millisUntilFinished)
        {
            pb.setProgress(SPLASH_DISPLAY_LENGHT-(int)millisUntilFinished);
            pb.refreshDrawableState();
        }

    }
}
