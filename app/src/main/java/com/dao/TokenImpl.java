package com.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jnidemo.DataBaseHelper;
import com.example.jnidemo.HttpControl;


/**
 * Created by zyf on 2018/5/16.
 */

public class TokenImpl implements Token{

    private MyTabOpen mytabopen = null;
    private static SQLiteDatabase db = null;
    private static String sql = null;
    private static final String tab_name = "Iot";

    public TokenImpl(Context context) {
        this.mytabopen = new MyTabOpen(context);//获得数据库操作实例
    }



    //添加数据
    public void insert(String uid,String token){
        db = mytabopen.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", uid);
        values.put("token", token);
        db.insert(tab_name, null, values);
        db.close();
    }

    //删除数据
    public boolean delete(String uid){
        db = mytabopen.getWritableDatabase();
        sql = "delete from "+tab_name+" where id = ?";
        db.execSQL(sql, new Object[]{uid});
        db.close();
        return true;
    }

    //更新数据
    public boolean update(String uid,String token) {
        db = mytabopen.getWritableDatabase();
        sql = "update " + tab_name + " set uid=?,token=?";
        db.execSQL(sql, new Object[]{uid, token});
        db.close();
        return true;
    }


    //查询数据
    public String get(String uid){
        db = mytabopen.getReadableDatabase();
        sql = "select * from "+tab_name+ " where uid=?";
        Cursor cur = db.rawQuery(sql, new String[]{uid});
        if(cur.moveToFirst()){
            String token = cur.getString(cur.getColumnIndex("token"));
            return token;
        }
        cur.close();
        db.close();
        return null;
    }


}
