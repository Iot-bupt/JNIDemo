package com.example.jnidemo;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;



import org.apache.http.client.ClientProtocolException;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.Serial;
import com.fbee.zllctl.TaskDeviceAction;
import com.google.gson.Gson;

@SuppressLint("NewApi") 
public  class MainActivity extends ActionBarActivity {
	
	private static final String TAG = MainActivity.class.getName();

	private static Serial serial;
	private static DeviceInfo deviceInfo;
	private static DeviceInfo lightinfo;
	private static  boolean islight;
//	private AlertDialog multiBoxChoose_dialog;
	// 所有设备Uid和设备对象的Map
	private static Map<String, DeviceInfo> devices = new HashMap<String, DeviceInfo>();
	
	// Server configuration
	private static final String OPENIOT_HOST = "10.108.219.194";
	private static final String DEVICE_ID = "eca553d0-4480-11e7-8355-cd30e8402e61";
	private static final String ACCESS_TOKEN = "FszMjFpvyvacGkPNRQGe";
	private static final String GATEWAY_URL = "http://10.108.219.194:9091/testPost";
	private static final String topic = "testTopic";  // 确保有这个topic

	
	private static MqttAsyncClient mOpenIoTMqttClient;
	
	//****************设置定时器相关***************
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
	        Message message = new Message();  
	        message.what = 1;  
	        handler.sendMessage(message);  
	    } 
	};
	
	//private OnSeekBarChangeListener SeekBarChange;
	
	
	private static SmsBroadCastReceiver smsBroadCastReceiver;
	static {
		System.loadLibrary("zllcmd");
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //连接按钮
        Button LinkButton = (Button)findViewById(R.id.Link_Server);
        LinkButton.setOnClickListener(LinkonClick);
        //开启按钮
        Button ONButton = (Button)findViewById(R.id.ON);
        ONButton.setOnClickListener(LightOnClick);
        //关闭按钮
        Button OFFButton = (Button)findViewById(R.id.OFF);
        OFFButton.setOnClickListener(LightOffClick);
        //调光
        SeekBar seekbar = (SeekBar)findViewById(R.id.BAR);
        seekbar.setOnSeekBarChangeListener(SeekBarChange);
        //调色
        SeekBar seekbar1 = (SeekBar)findViewById(R.id.BAR1);
        seekbar1.setOnSeekBarChangeListener(SeekBarCOLChange);
        //创建场景
        Button CreatButton = (Button)findViewById(R.id.creatsence);
        CreatButton.setOnClickListener(CreatSenceClick);
        
        serial = new Serial();
        serial.setmContext(getApplicationContext());
        //deviceInfo = new DeviceInfo();
        smsBroadCastReceiver = new SmsBroadCastReceiver();
        registerReceiver(smsBroadCastReceiver,new IntentFilter("com.feibi.callback"));
        
        // 创建MQTT客户端
        try {
        	mOpenIoTMqttClient = new MqttAsyncClient("tcp://" + OPENIOT_HOST + ":1883", DEVICE_ID, new MemoryPersistence());
        } catch (MqttException e) {
        	Log.e(TAG, "Unable to create MQTT client", e);
        }
        
        if (Build.VERSION.SDK_INT >= 11) {      
        	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());    
        	StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());  
        }
    }
    

    private View.OnClickListener LinkonClick=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
	    	//注意给予网路权限
			//本地连接
	    	 int ret = serial.connectLANZll();
	    	 if(ret > 0){
//		    	 final String[] boxip = serial.getGatewayIps(ret);
//		    	 final String[] boxsnid = serial.getBoxSnids(ret);
		    	 Toast.makeText(getApplicationContext(), ret+"", Toast.LENGTH_SHORT).show();
		    	 serial.getDevices();
		    	 mqttConnect();
//		    	 new AlertDialog.Builder(MainActivity.this)
//		    	 .setTitle("请选择")  
//		    	 .setIcon(android.R.drawable.ic_dialog_info)                  
//		    	 .setSingleChoiceItems(boxip, 0,   
//		    	   new DialogInterface.OnClickListener() {  
//		    	                               
//		    	      public void onClick(DialogInterface dialog, int which) {  
//		    	    	int conRet = serial.connectLANZllByIp(boxip[which], boxsnid[which]);
//		    	    	 // int conRet =  serial.connectRemoteZll("20167", "a2nd"); 
//		    	    	  String resMessage = "conRet="+String.valueOf(conRet) + ", boxip=" + boxip[which] + ", boxsnid=" + boxsnid[which];
//		    	    	  Toast.makeText(getApplicationContext(), resMessage, Toast.LENGTH_SHORT).show();
//		    	    	  if(conRet > 0 ){
//		    	    		  serial.getDevices();
//		    	    		  timer.schedule(task, 10000, 10000);
//		    	    		  mqttConnect();
//		    	    	  }
//		    	      }  
//		    	   }  
//		    	 )  
//		    	 .setNegativeButton("取消", null)  
//		    	 .show();  
	    	 }
		    //账户名密码方式连接
		    // serial.connectRemoteZll("1934", "fr19"); 
	    	//toast显示连接方式
	    	//Toast.makeText(getApplicationContext(),String.valueOf(serial.getConnectType()), Toast.LENGTH_SHORT).show();
	        //获取设备
	    	 
	    	//serial.getDevices();
		}
	};
	
	//接收广播
	public static class SmsBroadCastReceiver extends BroadcastReceiver    
	{   
	  
	    @Override  
	    public void onReceive(Context context, Intent intent)   
	    { 
	    	//获取设备
	    	deviceInfo = (DeviceInfo) intent.getSerializableExtra("dinfo");
	    	
	    	if (devices.get(String.valueOf(deviceInfo.getUId())) == null) {
	    		devices.put(String.valueOf(deviceInfo.getUId()), deviceInfo);
	    	}
	    	
	    	try {
				postDeviceData(deviceInfo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("SmsBroadCastReceiver", deviceInfo.getDeviceName());
			
	    	if(deviceInfo.hasColourable==(byte)1&&deviceInfo.getDeviceStatus()!=(byte)0){
	    		islight=true;
	    		lightinfo=deviceInfo;
	    		if(islight)
	    		{
	    			Toast.makeText(context,String.valueOf(deviceInfo.getUId()), Toast.LENGTH_SHORT).show();
	    		}
	    	}
	    	
	        //终止广播。   
	        //abortBroadcast();   
	    }   
	       
	}  
	
	//开启灯
    private View.OnClickListener LightOnClick=new View.OnClickListener() {
    	@Override
		public void onClick(View v) {
			
    		final DeviceInfo tiaoguang = devices.get("723015");
    		lightinfo = tiaoguang;
    		serial.setDeviceState(tiaoguang, 1);
    		tiaoguang.setDeviceStatus((byte) 1);
    		
    		Toast.makeText(getApplicationContext(), "deviceCount=" + devices.size(), Toast.LENGTH_SHORT).show();
			//开启
			if(islight){
				Toast.makeText(getApplicationContext(),lightinfo.getDeviceName()+"开启", Toast.LENGTH_SHORT).show();
				serial.setDeviceState(lightinfo, 1);
				lightinfo.setDeviceStatus((byte) 1);
			}
		}
	};

	//关闭灯
    private View.OnClickListener LightOffClick=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			DeviceInfo tiaoguang = devices.get("723015");
    		serial.setDeviceState(tiaoguang, 0);
    		tiaoguang.setDeviceStatus((byte) 0);
    		
			Toast.makeText(getApplicationContext(),"关闭", Toast.LENGTH_SHORT).show();
			//关闭
			if(islight)
			{
				Toast.makeText(getApplicationContext(),lightinfo.getDeviceName()+"关闭", Toast.LENGTH_SHORT).show();
				serial.setDeviceState(lightinfo, 0);
				lightinfo.setDeviceState((byte) 0);
			}
			
		}
	};

	//创景场景
    private View.OnClickListener CreatSenceClick=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(),"创建场景", Toast.LENGTH_SHORT).show();
	        int a=serial.addDeviceToSence(lightinfo.getDeviceName()+(1+Math.random()*(10-1+1)), lightinfo.getUId(), lightinfo.getDeviceId(), lightinfo.getDeviceState(), (byte)100, (byte) 100,  (byte)100, (byte) 0,  (byte)0);
	       if(a>0)
	       {  
	        Toast.makeText(getApplicationContext(),"创建场景成功", Toast.LENGTH_SHORT).show();
	       }
		}
	};

	//调节亮度
	private SeekBar.OnSeekBarChangeListener SeekBarChange = new SeekBar.OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if(islight)
			{	
				serial.setDeviceLevel(lightinfo,(byte)(255*progress/100));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	//调节颜色
	private SeekBar.OnSeekBarChangeListener SeekBarCOLChange = new SeekBar.OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if(islight)
			{
				serial.setDeviceHueSat(lightinfo,(byte)(255*progress/100),(byte)200);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
	};
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mqttDisconnect();
		timer.cancel();
	}
	
	/**
	 * MQTT客户端连接函数
	 */
	private void mqttConnect() {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		// 将ACCESS_TOKEN放入UserName中
		connOpts.setUserName(ACCESS_TOKEN);
		// 设置客户端回调函数
		mOpenIoTMqttClient.setCallback(mMqttCallback);
		try {
			mOpenIoTMqttClient.connect(connOpts, null, new IMqttActionListener() {
				
				@Override
				public void onSuccess(IMqttToken arg0) {
					// TODO Auto-generated method stub
					Log.i(TAG, "MQTT client connected!");
					try {
						mOpenIoTMqttClient.subscribe("v1/devices/me/rpc/request/+", 0);
					} catch (MqttException e) {
						Log.e(TAG, "Unable to subscribe to rpc requests topic", e);
					}
					
					try {
						mOpenIoTMqttClient.publish("v1/devices/me/telemetry", getDeviceStatusMessage(deviceInfo));
					} catch (Exception e) {
						Log.e(TAG, "Unable to publish Device status to OpenIoT Server", e);
					}
				}
				
				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable e) {
					// TODO Auto-generated method stub
					if (e instanceof MqttException) {
						MqttException mqttException = (MqttException) e;
						Log.e(TAG, String.format("Unable to connect to OpenIoT Server: %s, code: %d", mqttException.getMessage(),
								mqttException.getReasonCode()), e);
					} else {
						Log.e(TAG, String.format("Unable to connect to OpenIoT Server: %s", e.getMessage()), e);
					}
				}
			});
		} catch (MqttException e) {
			Log.e(TAG, String.format("Unable to connect to Thingsboard server: %s, code: %d", e.getMessage(), e.getReasonCode()), e);
		}
	}
	
	/**
	 * 断开Mqtt客户端
	 */
	private void mqttDisconnect() {
		try {
			mOpenIoTMqttClient.disconnect();
			Log.i(TAG, "MQTT client disconnected!");
		} catch (MqttException e) {
			Log.e(TAG, "Unable to disconnecte from the OpenIoT Server", e);
		}
	}
	
	/**
	 * 在这个回调函数中写控制设备代码
	 */
	private MqttCallback mMqttCallback = new MqttCallback() {
		
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			// TODO Auto-generated method stub
			Log.d(TAG, String.format("Received message from topic [%s]", topic));
            String requestId = topic.substring("v1/devices/me/rpc/request/".length());
            JSONObject messageData = new JSONObject(new String(message.getPayload()));
            String method = messageData.getString("method");
            if (method != null) {
                if (method.equals("getGpioStatus")) {
                	sendDeviceStatus(requestId);//如果服务器想获得状态，调用这个方法发送到IOT sever
                } else if (method.equals("setGpioStatus")) {
                    JSONObject params = messageData.getJSONObject("params");
                    Integer pin = params.getInt("pin");
                    boolean enabled = params.getBoolean("enabled");
                    if (pin != null) {
                        updateDeviceStatus(pin, enabled, requestId);
                    }
                } else {
                    //Client acts as an echo service
                	mOpenIoTMqttClient.publish("v1/devices/me/rpc/response/" + requestId, message);
                }
}
		}
		
		@Override
		public void deliveryComplete(IMqttDeliveryToken arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void connectionLost(Throwable e) {
			// TODO Auto-generated method stub
			Log.e(TAG, "Disconnected from Thingsboard server", e);
		}
	};
	
	/**
	 * 获取当前设备状态，构造MqttMessage
	 * @return
	 */
	private static MqttMessage getDeviceStatusMessage(DeviceInfo deviceInfo) {
		JSONObject deviceState = new JSONObject();
//        for (int k : mGpiosMap.keySet()) {
//            Gpio gpio = mGpiosMap.get(k);
//            boolean value = gpio.getValue();
//            gpioStatus.put(k + "", value);
//        }
		try {
			deviceState.put("test", "asdf");
		} catch (Exception e) {
			
		}
		
        MqttMessage message = new MqttMessage(deviceState.toString().getBytes());
        return message;
	}
	
	/**
	 * 发送设备状态消息到OpenIoT Server
	 * @param requestId
	 * @throws Exception
	 */
	private void sendDeviceStatus(String requestId) throws Exception {
        mOpenIoTMqttClient.publish("v1/devices/me/rpc/response/" + requestId, getDeviceStatusMessage(null));
    }

    private void updateDeviceStatus(int pin, boolean enabled, String requestId) throws Exception {
        JSONObject response = new JSONObject();
//        Gpio gpio = mGpiosMap.get(pin);
//        if (gpio != null) {
//            gpio.setValue(enabled);
//            response.put(pin + "", gpio.getValue());
//        } else {
//            response.put(pin + "", false);
//        }
//        MqttMessage message = new MqttMessage(response.toString().getBytes());
//        mThingsboardMqttClient.publish("v1/devices/me/rpc/response/" + requestId, message);
//        mThingsboardMqttClient.publish("v1/devices/me/attributes", message);
    }
    
    public static void postDeviceData(DeviceInfo deviceInfo) throws ClientProtocolException, IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	if (deviceInfo == null)
    		return ;
    	
    	DeviceData deviceData = convert(deviceInfo);
    	Gson gson = new Gson();
    	 JSONObject json = new JSONObject();
    	 json.put("uid", deviceInfo.getUId());
    	 json.put("dataType", "attr");
    	 json.put("deviceName", deviceInfo.getDeviceName());
    	String deviceDataStr = gson.toJson(deviceData);
    	json.put("info",deviceDataStr);
    	
        System.out.println("转换后的Json是：" +json);
        
        Properties props = new Properties();  
        //根据这个配置获取metadata,不必是kafka集群上的所有broker,但最好至少有两个
            props.setProperty("metadata.broker.list","localhost:9092，localhost:9091");  
            //消息传递到broker时的序列化方式
            props.setProperty("serializer.class","kafka.serializer.StringEncoder");  
            //0是不获取反馈(消息有可能传输失败)
            //1是获取消息传递给leader后反馈(其他副本有可能接受消息失败)
            //-1是所有in-sync replicas接受到消息时的反馈
            props.put("request.required.acks","1");  
           
                    }
           
    	
    private static DeviceData convert(DeviceInfo deviceInfo) {
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
