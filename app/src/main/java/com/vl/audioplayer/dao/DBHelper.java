package com.vl.audioplayer.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    //TODO:create table string
    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS myTable(...)";
    //название базы
    private final static String  DB_NAME = "APDB.db";
    // версия базы данных
    private static final int DATABASE_VERSION = 1;
    //TODO:public strings with table names
    Context mContext;

    public DBHelper(Context context, int dbVer) {
        super(context, DB_NAME, null, dbVer);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS tableName");
        onCreate(db);
    }

}

