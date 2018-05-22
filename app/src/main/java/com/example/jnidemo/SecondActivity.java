package com.example.jnidemo;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.DataBaseHelper;
import com.dao.TokenImpl;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.Serial;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mqtt.DataMqttClient;
import mqtt.RpcMqttClient;

public  class SecondActivity extends ActionBarActivity {
	
	public static Serial serial;
	private SmsBroadCastReceiver smsBroadCastReceiver;
	public static Map<String, DeviceInfo> devices = new HashMap<String, DeviceInfo>();

	private TextView tv1;
	private TextView tv2;
	private EditText et1;
	private EditText et2;

	private HttpControl hc= new HttpControl();
	private DataMqttClient dataMqttClient = new DataMqttClient();
	private TokenImpl database = new TokenImpl(this);
	
	private final Timer timer = new Timer();
	
	Handler handler = new Handler() {
		@Override  
	    public void handleMessage(Message msg) {  
	        // TODO Auto-generated method stub  
	        // 要做的事情  
			serial.getDevices();
	        super.handleMessage(msg);  
	    }
	};
	
	private TimerTask task = new TimerTask() {
		@Override  
	    public void run() {  
	        // TODO Auto-generated method stub  
//			Config.ISFIRST = false;
	        Message message = new Message();  
	        message.what = 1;  
	        handler.sendMessage(message);  
	    } 
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		serial = new Serial();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //初始化组件
        Button btn_link = (Button) findViewById(R.id.btn_link);
        btn_link.setOnClickListener(linkonclick);

		//创建广播接收器
        smsBroadCastReceiver = new SmsBroadCastReceiver();
        registerReceiver(smsBroadCastReceiver,new IntentFilter("com.feibi.callback"));
        serial.setmContext(getApplicationContext());

		//创建SQlLite数据库
		SQLiteOpenHelper helper = new DataBaseHelper(this);
		helper.getWritableDatabase();

//        初始化的时候进行login
		hc.httplogin();
		RpcMqttClient.init();
	}



    private View.OnClickListener linkonclick = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// wrapper.init();
			 int ret = serial.connectLANZll();
	    	 if(ret > 0){
		    	 Toast.makeText(getApplicationContext(), ret+"", Toast.LENGTH_SHORT).show();
	    	 }
	    	 else
	    	 {
	    		 Toast.makeText(getApplicationContext(), ret+"", Toast.LENGTH_SHORT).show();
	    	 }
			serial.getDevices();
			timer.schedule(task, 3000, 5000);

		}
	};
	

	
	public  class SmsBroadCastReceiver extends BroadcastReceiver    
	{   
	  
	    @Override  
	    public void onReceive(Context context, Intent intent)   
	    {

			final DeviceInfo deviceInfo = (DeviceInfo) intent.getSerializableExtra("data");
//			Log.e("SecondActivity", "deviceInfo = "+deviceInfo );
			devices.put(deviceInfo.getUId()+"", deviceInfo);

//            if (!deviceInfo.getDeviceName().equals("switch_1")){
//            	return;
//			}
			final int uid = deviceInfo.getUId();
			if(database.get(uid) == null){
			    //SQLite里没有token
				new Thread(new Runnable() {
					@Override
					public void run() {
						String id=null,token=null;
						try{
							id = hc.httpcreate(deviceInfo.getDeviceName());
							token = hc.httpfind(id);
						}catch (Exception e){
							e.printStackTrace();
						}
						if(id==null||token==null){
							Log.e("12345", "onReceive: 创建设备失败");
							//hc.httplogin();
							return;
						}
						//存入DB
						database.insert(uid,token);
						//摘除sensordata发送属性
						postDeviceAttribute(deviceInfo, token);
						//只发送sensordata
						postDeviceData(deviceInfo, token);
					}
				}).start();

            }else{
			    //SQLite里有token，从表中拿token
                String token = database.get(uid);
                //摘除sensordata发送属性
                postDeviceAttribute(deviceInfo, token);
                //只发送sensordata
                postDeviceData(deviceInfo, token);

            }

	    }
    }

	private void postDeviceAttribute(DeviceInfo deviceInfo, String token) {
		// TODO Auto-generated method stub
		try{
			DeviceData deviceData = convert(deviceInfo);
			Gson gson = new Gson();
			String deviceDataStr = gson.toJson(deviceData);
			//进行发送
			dataMqttClient.publishAttribute(token,deviceDataStr);

		}catch(Exception e) {
			e.printStackTrace();
		}
	}   
	
	private void postDeviceData(DeviceInfo deviceInfo, String token){
		try {
			int sensordata = deviceInfo.getSensordata();
			JSONObject info = new JSONObject();
			if(deviceInfo.getDeviceId()==0x0302){
				if(deviceInfo.getAttribID() == 0x00){
					info.put("temperature", sensordata);
				}else{
					info.put("humidity", sensordata);
				}
			}else {
				info.put("telemetry",sensordata);
			}

			String data = info.toString();
			dataMqttClient.publishData(token,data);
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
    private  DeviceData convert(DeviceInfo deviceInfo) {
    	DeviceData deviceData = new DeviceData();
    	deviceData.setDevicesnid(deviceInfo.getDeviceSnid());
    	deviceData.setDevicename(deviceInfo.getDeviceName());
    	deviceData.setDevicestatus(Integer.valueOf(deviceInfo.getDeviceStatus()));
    	deviceData.setDevicestate(Integer.valueOf(deviceInfo.getDeviceState()));
    	deviceData.setUid(String.valueOf(deviceInfo.getUId()));
    	deviceData.setDeviceid(String.valueOf(deviceInfo.getDeviceId()));
    	deviceData.setProfileid(String.valueOf(deviceInfo.getProfileId()));
    	deviceData.setType(deviceInfo.type);
    	deviceData.setClusterid(String.valueOf(deviceInfo.getClusterId()));
    	deviceData.setAttribid(String.valueOf(deviceInfo.getAttribID()));
    	deviceData.setHascolourable(String.valueOf(deviceInfo.hasColourable));
    	deviceData.setHasdimmable(String.valueOf(deviceInfo.hasDimmable));
    	deviceData.setHasswitchable(String.valueOf(deviceInfo.hasSwitchable));
    	deviceData.setHasthermometer(String.valueOf(deviceInfo.hasThermometer));
    	deviceData.setHaspowerusage(String.valueOf(deviceInfo.hasPowerUsage));
    	deviceData.setHasoutswitch(String.valueOf(deviceInfo.hasOutSwitch));
    	deviceData.setHasoutlevel(String.valueOf(deviceInfo.hasOutLeveL));
    	deviceData.setHasoutcolor(String.valueOf(deviceInfo.hasOutColor));
    	deviceData.setHasoutscene(String.valueOf(deviceInfo.hasOutScene));
    	deviceData.setHasoutgroup(String.valueOf(deviceInfo.hasOutGroup));
    	deviceData.setHassensor(String.valueOf(deviceInfo.hasSensor));
    	deviceData.setIssmartplug(String.valueOf(deviceInfo.issmartplug));
    	deviceData.setZonetype(String.valueOf(deviceInfo.zoneType));
    	
    	return deviceData;
    }
}