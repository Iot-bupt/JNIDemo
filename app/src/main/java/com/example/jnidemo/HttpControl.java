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
    private String host = "39.104.84.131";
    static private String session ;
    public static String id;
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
        cookieStore.clear();
        //请求体
        RequestBody bodyLogin = RequestBody.create(js, "{\"username\":\"TenantAdmin@bupt.edu.cn\",\"password\":\"password\"}");

        //创建一个Request Request是OkHttp中访问的请求，Builder是辅助类。Response即OkHttp中的响应。
         final Request requestLogin = new Request.Builder()
                 .url("http://39.104.84.131/api/user/login")
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

                ck = cookieStore.get(host).get(0);

                String sessionStr = ck.toString();
                session = sessionStr.substring(0,sessionStr.indexOf(";"));

                Log.e("http", "login cookie is : " + ck);
                Log.e("http", "login session is  :" + session);
            }
        });

    }

    /*
    创建新设备的post请求
     */

    protected String httpcreate(final String devicename) throws Exception{

        //请求体
        JSONObject obj = new JSONObject();
        obj.put("name",devicename);
        RequestBody bodyCreate = RequestBody.create(js, obj.toString());

        //创建一个Request Request是OkHttp中访问的请求，Builder是辅助类。Response即OkHttp中的响应。
        Request requestCreate = new Request.Builder()
                .url("http://39.104.84.131/api/device/create")
                .post(bodyCreate)
                .addHeader("Accept","application/json, text/plain, */*")
//                .addHeader("Accept","text/plain, */*, q=0.01")
                .addHeader("Connection","keep-alive")
                .addHeader("Content-Type","application/json;charset=UTF-8")
                .addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2)" +
                        " AppleWebKit/604.4.7 (KHTML, like Gecko) Version/11.0.2 Safari/604.4.7")
                .addHeader("Cookie",session.toString())
                .build();
        //得到一个call对象
        Response response = mOkHttpClient.newCall(requestCreate).execute();
        if (response.isSuccessful()){
                String result = response.body().string();
                Log.e("http", "create_Response :"+result );

                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                id = jsonObject.get("id").getAsString();
                Log.e("http", "id: "+id);
                return id;
        }else{
            httplogin();
            return null;
        }

    }

    /*
    查找令牌的get请求
     */

    protected String httpfind(String id)throws Exception {

        //创建一个Request Request是OkHttp中访问的请求，Builder是辅助类。Response即OkHttp中的响应。
        Request requestCreate = new Request.Builder()
                .url("http://39.104.84.131/api/device/token/" + id.toString())
                .get()
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Connection", "keep-alive")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")
                .addHeader("Cookie", session.toString())
                .build();
        //得到一个call对象
        Response response = mOkHttpClient.newCall(requestCreate).execute();
        if (response.isSuccessful()) {

            String result = response.body().string();
            Log.e("http", "find_response : " + result);

            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
            deviceToken = jsonObject.get("deviceToken").getAsString();
            Log.e("http", "find_token : " + deviceToken);
            return deviceToken;
        }
        return null;


    }
}



