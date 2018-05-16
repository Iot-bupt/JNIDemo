package com.example.jnidemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by zyf on 2018/5/16.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static String name = "test.db";
    private static String table_name = "Iot.db";
    private static int version = 1;
    private static String TABLE_TEST_SQL = "create table book(id integer primary key autoincrement," +
            "name varchar(32),price real)";
    private static String sql = null;

    public DataBaseHelper(Context context){
        super(context,name,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(TABLE_TEST_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        sql = "ALTER TABLE " + table_name + " ADD sex VARCHAR(2) NULL";
        Log.i("sql", sql);
        db.execSQL(sql);

    }
}
