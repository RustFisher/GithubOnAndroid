package com.rustfisher.githubonandroid.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rustfisher.githubonandroid.GApp;

import java.util.ArrayList;
import java.util.Locale;

public final class DBManager {

    private static volatile DBManager manager;
    private SQLiteDatabase db;

    private DBManager() {
        DBHelper dbHelper = new DBHelper(GApp.getApp().getApplicationContext(), DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
        db = dbHelper.getReadableDatabase();
    }

    public static DBManager getManager() {
        if (null == manager) {
            synchronized (DBManager.class) {
                if (null == manager) {
                    manager = new DBManager();
                }
            }
        }
        return manager;
    }

    public void insertOneRecord(String name) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.OWNER_NAME, name);
        cv.put(DBHelper.RECORD_TIME, String.valueOf(System.currentTimeMillis()));
        db.insert(DBHelper.TABLE_NAME, null, cv);
    }

    public ArrayList<OwnerEntity> queryAllHistory() {
        Cursor cursor = db.query(DBHelper.TABLE_NAME, new String[]{
                DBHelper._ID,
                DBHelper.OWNER_NAME,
                DBHelper.RECORD_TIME}, null, null, null, null, null);
        ArrayList<OwnerEntity> list = new ArrayList<>();
        if (cursor.getCount() == 0) {
            cursor.close();
            return list;
        }
        while (cursor.moveToNext()) {
            list.add(new OwnerEntity(cursor.getInt(0), cursor.getString(1), cursor.getLong(2)));
        }
        cursor.close();
        return list;
    }

    public ArrayList<String> queryHistoryStr() {
        Cursor cursor = db.query(DBHelper.TABLE_NAME, new String[]{
                DBHelper.OWNER_NAME}, null, null, null, null, null);
        ArrayList<String> list = new ArrayList<>();
        if (cursor.getCount() == 0) {
            cursor.close();
            return list;
        }
        while (cursor.moveToNext()) {
            list.add((cursor.getString(0)));
        }
        cursor.close();
        return list;
    }

    public final static class OwnerEntity {
        int _id;
        String ownerName;
        long recordTime;

        public OwnerEntity(int id, String name, long time) {
            _id = id;
            ownerName = name;
            recordTime = time;
        }

        @Override
        public String toString() {
            return String.format(Locale.ENGLISH, "id:%d name:%s %d", _id, ownerName, recordTime);
        }
    }

}
