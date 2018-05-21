package com.dao;

/**
 * Created by zy on 2018/5/16.
 */

public interface Token {

    public void insert(int uid,String token);
    public boolean delete(int uid);
    public boolean update(int uid,String token);
    public String get(int uid);
}
