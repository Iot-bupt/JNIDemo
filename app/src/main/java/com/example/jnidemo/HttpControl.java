package com.example.jnidemo;


import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zy on 2018/5/15.
 */

public class HttpControl {

    static private Cookie ck;
    private String host = "10.112.233.200";
    static private String session ;
    public String id;
    public String deviceToken;

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static final MediaType js = MediaType.parse("application/json; charset=utf-8");

    ///创建okHttpClient对象
    private OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();


    /*
    登录时的post请求
     */
    protected void httplogin(){

        //请求体
        RequestBody bodyLogin = RequestBody.create(js, "{\"username\":\"1111test@qq.com\",\"password\":\"123456\"}");

        //创建一个Request Request是OkHttp中访问的请求，Builder是辅助类。Response即OkHttp中的响应。
         final Request requestLogin = new Request.Builder()
                 .url("http://10.112.233.200/api/user/login")
                 .header("Accept","text/plain, */*; q=0.01")
                 .addHeader("Connection","keep-alive")
                 .addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2)AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")
                 .addHeader("Content-Type","application/json; charset=UTF-8")
                 .post(bodyLogin)
                 .build();
        //得到一个call对象
        Call call = mOkHttpClient.newCall(requestLogin);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("http", "login请求失败 " );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                Headers headers = response.headers();
                Log.e("http", "login header " + headers);
//                System.out.println("header-login:" + headers);

                ck = cookieStore.get(host).get(0);

                String sessionStr = ck.toString();
                session = sessionStr.substring(0,sessionStr.indexOf(";"));

                Log.e("http", "login cookie is : " + ck);
                Log.e("http", "login session is  :" + session);
//
//                System.out.println("cookie is  :" + ck);
//                System.out.println("session is  :" + session);
            }
        });

    }

    /*
    创建新设备的post请求
     */

    protected void httpcreate(final String devicename){

        //请求体
        RequestBody bodyCreate = RequestBody.create(js, "{\"name\":\""+devicename.toString()+"\"}");

        //创建一个Request Request是OkHttp中访问的请求，Builder是辅助类。Response即OkHttp中的响应。
        Request requestCreate = new Request.Builder()
                .url("http://10.112.233.200/api/device/create")
                .post(bodyCreate)
                .addHeader("Accept","application/json, text/plain, */*")
                .addHeader("Connection","keep-alive")
                .addHeader("Content-Type","application/json;charset=UTF-8")
                .addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")
                .addHeader("Cookie",session.toString())
                .build();
        //得到一个call对象
        Call call = mOkHttpClient.newCall(requestCreate);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("http", "create请求失败 " );
                httplogin();
                httpcreate(devicename);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = response.body().string();
//                System.out.println("response—create:"+result);
                Log.e("http", "create_Response :"+result );

                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                id = jsonObject.get("id").getAsString();
//                System.out.println("id :"+id);
                Log.e("http", "create_id: "+id );

            }
        });

    }

    /*
    查找令牌的get请求
     */

    protected void httpfind(String id){

        //创建一个Request Request是OkHttp中访问的请求，Builder是辅助类。Response即OkHttp中的响应。
        Request requestCreate = new Request.Builder()
                .url("http://10.112.233.200/api/device/token/"+id.toString())
                .get()
                .addHeader("Accept","application/json, text/plain, */*")
                .addHeader("Connection","keep-alive")
                .addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")
                .addHeader("Cookie",session.toString())
                .build();
        //得到一个call对象
        Call call = mOkHttpClient.newCall(requestCreate);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("http", "findtoken请求失败 " );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Headers headers = response.headers();
//                System.out.println("header-create:" + headers);
                Log.e("http", "find_headers : "+headers);

                String result = response.body().string();
//                System.out.println("response—find:"+result);
                Log.e("http", "find_response : "+result );
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                deviceToken = jsonObject.get("deviceToken").getAsString();
//                System.out.println("Token :"+deviceToken);
                Log.e("http", "find_token : "+deviceToken);
            }
        });

    }

}



