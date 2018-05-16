package com.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zyf on 2018/5/16.
 */

public class MyTabOpen extends SQLiteOpenHelper {

    private static final String db_sql = "SQLiter.db";
    private static int NUMBER = 1;
    private static final String tab_name = "Iot";
    private static String sql = null;

    public MyTabOpen(Context context){
        super(context, db_sql, null, NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        sql = "CREATE TABLE " + tab_name + " (" +
                "uid       VARCHAR(20)        NOT NULL ," +
                "token  VARCHAR(20)     NOT NULL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("ALTER TABLE students ADD school VARCHAR(10) NULL");
    }
}
