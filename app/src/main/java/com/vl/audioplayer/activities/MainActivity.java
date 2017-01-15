package com.vl.audioplayer.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.vl.audioplayer.dao.DatabaseHelper;
import com.vl.audioplayer.entities.Track;
import com.vl.audioplayer.entities.PlayList;
import com.vl.audioplayer.service.MusicSearcher;
import com.vl.audioplayer.R;
import com.vl.audioplayer.service.MusicPlayer;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 255;
    public static int index = 0;
    MusicPlayer player;
    ArrayList tracks;
    PlayList currentPlayList;
    public ProgressDialog dialog;
    DatabaseHelper database;
    FloatingActionButton playButton;
    FloatingActionButton nextButton;
    FloatingActionButton prevButton;
    FloatingActionButton menuButton;
    SeekBar bar;
    MyTimer timer;
    ListView listView;
    MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuButton = (FloatingActionButton) findViewById(R.id.menuButton);
        playButton = (FloatingActionButton) findViewById(R.id.playButton);
        nextButton = (FloatingActionButton) findViewById(R.id.nextButton);
        prevButton = (FloatingActionButton) findViewById(R.id.prevButton);
        bar = ((SeekBar) findViewById(R.id.seekBar));
        listView = (ListView) findViewById(R.id.list_tracks);
        listView.setSelector(R.drawable.track_list_drawable);

        player = new MusicPlayer(MainActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForRead();
        }
        database = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        try {

            List<PlayList> playList = database.getPlayListDao().queryForAll();
            if (playList.size() > 0)
                currentPlayList = playList.get(0);
            else {
                currentPlayList = new PlayList();
                currentPlayList.setName("all");
            }
        } catch (SQLException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(),
                    "Ошибка инициализации",
                    Toast.LENGTH_SHORT).show();
        }

        dialog = new ProgressDialog(MainActivity.this);
        new SearcherTask().execute(false);

        menuButton.setOnClickListener(viewMenuClickListener);
        playButton.setOnClickListener(viewPlayerClickListener);
        nextButton.setOnClickListener(viewPlayerClickListener);
        prevButton.setOnClickListener(viewPlayerClickListener);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                for (int j = 0; j < parent.getChildCount(); j++)
//                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
//                index = position;
                // change the background color of the selected element
                //view.setSelected(true);
               // selectedView = view;
                adapter.setSelectedIndex(position);
              // view.setBackgroundColor(Color.LTGRAY);
                player.play(position);
            }
        });

        player.addListener(new MusicPlayer.PlayerListener() {
            @Override
            public void onEoF(int position) {

                // change the background color of the selected element
                listView.smoothScrollToPosition(position);
                adapter.setSelectedIndex(position);
            }

            @Override
            public void onIsPausedChanged(boolean isPaused) {
                setPlayIcon(isPaused);
            }
        });


        timer = new MyTimer(Integer.MAX_VALUE, 500);
        timer.start();
    }


    View.OnClickListener viewPlayerClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.playButton:
                    if (!player.isPaused()) {
                        player.pause();
                    } else {
                        player.play();
                    }
                    break;
                case R.id.nextButton:
                    player.next();
                    break;
                case R.id.prevButton:
                    player.prev();
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

                            case R.id.menuTrackList:
                                Toast.makeText(getApplicationContext(),
                                        "not implemented yet",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menuAbout:
                                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.menuRefresh:
                                dialog = new ProgressDialog(MainActivity.this);
                                new SearcherTask().execute(true);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
        popupMenu.show();
    }
    public void setPlayIcon(boolean isPaused){
        if(!isPaused)
            playButton.setImageResource(android.R.drawable.ic_media_pause);
        else
            playButton.setImageResource(android.R.drawable.ic_media_play);
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
            if(player.isPaused())return;

            bar.setMax(player.getDuration());
            bar.setProgress(player.getCurrentPosition());
            ((TextView)findViewById(R.id.trackInfo)).setText("Time " + player.getCurrentPosition()/60+":"+player.getCurrentPosition()%60 + " / "
                    + player.getDuration()/60+":"+player.getDuration()%60);
        }

    }

    @Override
    public void onDestroy() {
        moveTaskToBack(true);

        super.onDestroy();
        System.exit(0);
    }

    public void setTrackList(PlayList list){
       // ArrayAdapter<Track> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list.getTracks());
        adapter = new MyAdapter(this,list.getTracks());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askForRead(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
    }

    public class SearcherTask extends AsyncTask<Boolean, Void, Boolean> {
        private Context context;
        private MusicSearcher musicSearcher;

        public SearcherTask() {
            musicSearcher = new MusicSearcher();

        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("wait for a moment...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if(params[0]) try {
                database.clearAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(currentPlayList.getTracks().size()>0)return true;

            Boolean res = true;
            Boolean res2 = true;
            tracks = new ArrayList();
            try {
                for(String dir:MusicSearcher.getStorageDirectories())
                tracks.addAll(musicSearcher.find(dir, "mp3"));

            } catch (Exception e) {
                e.printStackTrace();
                if(tracks.size() == 0)
                res=false;
            }
            if(tracks!=null) {
                for (Object tr : tracks) {

                    File track = (File) tr;

                    Track temp = new Track();
                    temp.setName(track.getName());
                    temp.setPath(track.getAbsolutePath());
                    try {
                        database.getTrackDao().create(temp);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                try {
                   currentPlayList.setTracks((ArrayList<Track>) database.getTrackDao().queryForAll());
                   database.getPlayListDao().create(currentPlayList);
               } catch (SQLException e) {
                    e.printStackTrace();
                    res = false;
                }
            }
            return res;
        }


        @Override
        protected void onPostExecute(Boolean result) {


            if (dialog.isShowing()) {
                dialog.dismiss();

                if(!result){
                    Toast.makeText(getApplicationContext(),
                            "Ошибка поиска треков",
                            Toast.LENGTH_SHORT).show();
                }
                player.setCurrentPlayList(currentPlayList);
                setTrackList(currentPlayList);
            }
        }
    }

    public class MyAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<Track> testList;
        private int selectedIndex;
        private int selectedColor = Color.parseColor("#FFFFFF");

        public MyAdapter(Context ctx, ArrayList<Track> testList)
        {
            this.context = ctx;
            this.testList = testList;
            selectedIndex = -1;
        }

        public void setSelectedIndex(int ind)
        {
            selectedIndex = ind;
            notifyDataSetChanged();
        }
        @Override
        public int getCount()
        {
            return testList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return testList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }
        private class ViewHolder
        {
            TextView tv;
        }
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View vi = convertView;
                ViewHolder holder;
                if(convertView == null)
                {
                    vi = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
                    holder = new ViewHolder();

                    holder.tv = (TextView) vi;

                    vi.setTag(holder);
                }
                else
                {
                    holder = (ViewHolder) vi.getTag();
                }

                if(selectedIndex!= -1 && position == selectedIndex)
                {
                    holder.tv.setBackgroundColor(Color.LTGRAY);
                }
                else
                {
                    holder.tv.setBackgroundColor(selectedColor);
                }
                holder.tv.setText("" + (position + 1) + " " + testList.get(position).getName());

                return vi;
            }
        }


}