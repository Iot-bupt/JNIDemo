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
		
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		SecondActivity.wrapper.init();
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

		String method_uid = messageData.getString("method");
		Log.e("12345",method_uid );
		String sarray[]=method_uid.split("_"); 
		String method = sarray[0];
		String uid =sarray[1];
		if(devices.get(uid)==null){
			return;
		}
		boolean params = messageData.getBoolean("params");
		int state;
		if(params == true){
			 state = 1;
		}else{
			 state = 0;
		}
		if(method.startsWith("set")){
			if(method.contains("door")){
				serial.setGatedoorState(devices.get(uid), state, "81581581".getBytes());
			}else{
				serial.setDeviceState(devices.get(uid), state);
			}
			
		}else if(method.startsWith("get")){
			String resTopic = topic.replace("request", "responce");
			//MqttMessage message = new MqttMessage("1".getBytes());
			SecondActivity.wrapper.rpcPublish(resTopic,"1");
		}
        
//		switch(uid_int){
//		
//		case 723015:
//			if(method.equals("setDeviceState")){
//				serial.setDeviceState(devices.get("723015"), state);
//			}
//		}
		
//		String data = new String(msg.getPayload());
//		Log.e("1234", data);
//		if(devices.containsKey("723015")){
//			int i = new Random().nextInt()%2;
//			Log.e("1234", i+"");
//			serial.setDeviceState(devices.get("723015"), i);
//		}
	}

}
