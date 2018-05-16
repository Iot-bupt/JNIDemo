package com.dao;

/**
 * Created by zy on 2018/5/16.
 */

public interface Token {

    public void insert(String uid,String token);
    public boolean delete(String uid);
    public boolean update(String uid,String token);
    public String get(String uid);
}
