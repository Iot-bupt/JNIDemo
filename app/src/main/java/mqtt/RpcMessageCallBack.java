package mqtt;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import android.util.Log;

import com.example.jnidemo.SecondActivity;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.Serial;

public class RpcMessageCallBack implements MqttCallback{
	Serial serial;
	Map<String, DeviceInfo> devices;
	
	public RpcMessageCallBack(MqttClient rpcMqtt){
		this.serial = SecondActivity.serial;
		this.devices = SecondActivity.devices;
	}
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		Log.e("12345", "connection lost");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SecondActivity.wrapper.init();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		Log.e("12345", "delivery complete");
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		// TODO Auto-generated method stub
	
		JSONObject messageData = new JSONObject(new String(msg.getPayload()));

		String method = messageData.getString("method");
		JSONObject params = messageData.getJSONObject("params");
		String uid = params.getString("uid");

		Log.e("12345", "params  ="+params );
		Log.e("12345", "method ="+method );

		if(!devices.containsKey(uid)) return;
		Object status = params.get("status");
		int  a = 0;
		if(status instanceof String){
			if(status.equals("true")) a = 1;
		}else if(status instanceof Boolean){
			a = (Boolean)status?1:0;
		}
		//int  a = status?1:0;
		Log.e("12345", "a ="+a );
		if(method.startsWith("set")){
			if(method.contains("door")){
				serial.setGatedoorState(devices.get(uid), a, "81581581".getBytes());
			}else{
				serial.setDeviceState(devices.get(uid), a);
			}
		}else{
			String resTopic = topic.replace("request", "responce");
			//MqttMessage message = new MqttMessage("1".getBytes());
			SecondActivity.wrapper.rpcPublish(resTopic,"1");
		}
	}
}
