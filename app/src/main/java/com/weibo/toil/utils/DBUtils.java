package com.weibo.toil.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.String;

public class DBUtils {
    public static final String CREATE_TABLE_IF_NOT_EXISTS = "create table if not exists %s " +
            "(id integer  primary key autoincrement,key text unique,is_read integer)";

    private SQLiteDatabase mSQLiteDatabase;

    private DBUtils(Context context) {
        mSQLiteDatabase = new DBHelper(context, Constant.DB__IS_READ_NAME + ".db").getWritableDatabase();
    }

    public static DBUtils getDB() {
        return DBUtilshelper.d;
    }

    private static class DBUtilshelper{
        private static final DBUtils d = new DBUtils(MainApplication.getContext());
    }

    public void insertHasRead(final String table, final String key, final int value) {
        MainApplication.getService().submit(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = mSQLiteDatabase.query(table, null, null, null, null, null, "id asc");
                if (cursor.getCount() > 200 && cursor.moveToNext()) {
                    mSQLiteDatabase.delete(table, "id=?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex("id")))});
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put("key", key);
                contentValues.put("is_read", value);
                mSQLiteDatabase.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        });

    }

    public boolean isRead(String table, String key,int value) {
        boolean isRead = false;
        Cursor cursor = mSQLiteDatabase.query(table, null, "key=?", new String[]{key}, null, null, null);
        if (cursor.moveToNext() && (cursor.getInt(cursor.getColumnIndex("is_read")) == value)) {
            isRead = true;
        }
        cursor.close();
        return isRead;
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name) {
            super(context, name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, Constant.GUOKR));
            db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, Constant.IT));
            db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, Constant.VIDEO));
            db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, Constant.ZHIHU));
            db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, Constant.WEIXIN));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
