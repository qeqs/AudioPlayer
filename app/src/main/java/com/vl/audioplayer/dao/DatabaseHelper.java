package com.vl.audioplayer.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.vl.audioplayer.activities.MainActivity;
import com.vl.audioplayer.entities.Track;
import com.vl.audioplayer.entities.PlayList;
import com.vl.audioplayer.entities.TrackPlayList;

import java.sql.SQLException;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "MusicBase.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    private ConnectionSource connectionSource;
    private SQLiteDatabase database;
    private Dao<Track, Integer> trackDao = null;
    private Dao<PlayList, Integer> playListDao = null;
    private Dao<TrackPlayList, Integer> trackPlayListsDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            database = db;
            this.connectionSource = connectionSource;
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource,TrackPlayList.class);
            TableUtils.createTable(connectionSource, Track.class);
            TableUtils.createTable(connectionSource, PlayList.class);

            PlayList playList = new PlayList();

            playList.setName("all");

            getPlayListDao().create(playList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void clearAll() throws SQLException {

        TableUtils.dropTable(connectionSource,TrackPlayList.class,true);
        TableUtils.dropTable(connectionSource, Track.class,true);
        TableUtils.dropTable(connectionSource, PlayList.class,true);
        onCreate(database,connectionSource);
    }
        /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Track.class, true);
            TableUtils.dropTable(connectionSource, PlayList.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Track, Integer> getTrackDao() throws SQLException {
        if (trackDao == null) {
            trackDao = getDao(Track.class);
        }
        return trackDao;
    }
    public Dao<PlayList, Integer> getPlayListDao() throws SQLException {
        if (playListDao == null) {
            playListDao = getDao(PlayList.class);
        }
        return playListDao;
    }
    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        trackDao = null;
    }

    public Dao<TrackPlayList, Integer> getTrackPlayListsDao() throws SQLException {
        if(trackPlayListsDao==null) trackPlayListsDao = getDao(TrackPlayList.class);
        return trackPlayListsDao;
    }
}

