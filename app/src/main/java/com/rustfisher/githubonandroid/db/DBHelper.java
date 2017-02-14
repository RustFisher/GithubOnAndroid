package com.rustfisher.githubonandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "github.db";
    public static final String TABLE_NAME = "github_table";

    public static final String _ID = "id";
    public static final String OWNER_NAME = "owner_name";
    public static final String RECORD_TIME = "record_time";// ms

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cmd = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + OWNER_NAME + " VARCHAR, "
                + RECORD_TIME + " INTEGER);";
        db.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
