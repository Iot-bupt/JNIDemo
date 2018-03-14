package com.example.jnidemo;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import mqtt.MqttWrapper;

import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.Serial;
import com.google.gson.Gson;

import config.Config;


public  class SecondActivity extends ActionBarActivity {
	
	public static Serial serial;
	private SmsBroadCastReceiver smsBroadCastReceiver;
	public static Map<String, DeviceInfo> devices = new HashMap<String, DeviceInfo>();
	public static MqttWrapper  wrapper = new MqttWrapper();
	
	private TextView tv1;
	private TextView tv2;
	private EditText et1;
	private EditText et2;
	
	
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
			Config.ISFIRST = false;
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
        
//        Button btn_init = (Button) findViewById(R.id.btn_init);
//        btn_init.setOnClickListener(initonclick);
        
        Button btn_link = (Button) findViewById(R.id.btn_link);
        btn_link.setOnClickListener(linkonclick);
        
//        Button btn_device = (Button) findViewById(R.id.btn_device);
//        btn_device.setOnClickListener(deviceonclick);
        
        smsBroadCastReceiver = new SmsBroadCastReceiver();
        registerReceiver(smsBroadCastReceiver,new IntentFilter("com.feibi.callback"));
        serial.setmContext(getApplicationContext());
        		
        }
    
//    private View.OnClickListener initonclick = new View.OnClickListener() {
//
//		@Override
//		public void onClick(View arg0) {
//			wrapper.init();
//		}
//	};
	
    
    private View.OnClickListener linkonclick = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			 wrapper.init();
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
	
//    private View.OnClickListener deviceonclick = new View.OnClickListener() {
//
//		@Override
//		public void onClick(View arg0) {
//			// TODO Auto-generated method stub
//			 serial.getDevices();
//			 timer.schedule(task, 3000, 5000);
//		}
//	};
	
	public  class SmsBroadCastReceiver extends BroadcastReceiver    
	{   
	  
	    @Override  
	    public void onReceive(Context context, Intent intent)   
	    { 
	    	if(intent.getBooleanExtra("isAttribute", false)){
		    	DeviceInfo deviceInfo = (DeviceInfo) intent.getSerializableExtra("data");
		    	
		    	try {
					postDeviceData(deviceInfo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}else{
	    		String data = intent.getStringExtra("data");
	    		postDeviceTele(data);
	    	}
	    }
    }
	
	private void postDeviceData(DeviceInfo deviceInfo) {
		// TODO Auto-generated method stub
		try{
	    	DeviceData deviceData = convert(deviceInfo);
	    	Gson gson = new Gson();
	    	 JSONObject json = new JSONObject();
	    	 json.put("uId", deviceInfo.getUId());
	    	 json.put("dataType", "attribute");
	    	 json.put("deviceName", deviceInfo.getDeviceName());
	    	String deviceDataStr = gson.toJson(deviceData);
	    	json.put("info",deviceDataStr);	
	    	wrapper.publish(Config.DATA_TOPIC, json.toString());
	    	devices.put(deviceInfo.getUId()+"", deviceInfo);
		}catch(Exception e){
			e.printStackTrace();
		}
	}   
	
	private void postDeviceTele(String data){
		try{
			JSONObject json = new JSONObject(data);
			String uId = json.getString("uId");
			if(devices.containsKey(uId)){
		    	wrapper.publish(Config.DATA_TOPIC, json.toString());	
			}else{
				return;
			}
		}catch(Exception e){
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
    	deviceData.setSensordata(String.valueOf(deviceInfo.getSensordata()));
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