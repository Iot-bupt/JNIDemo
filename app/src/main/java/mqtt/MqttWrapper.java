package mqtt;

import java.nio.charset.Charset;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.util.Log;

import config.Config;

public class MqttWrapper {
//	private  MqttClient uploadMqtt;
	private  MqttClient rpcMqtt;
	
	public synchronized void  init(){
	try{
		
//		uploadMqtt = new MqttClient(Config.DATACACHE_MQTT_HOST,"iotUploaddata",new MemoryPersistence());
		rpcMqtt = new MqttClient(Config.THINGSBOARD_MQTT_HOST,"receiveRPC",new MemoryPersistence());

		MqttConnectOptions optionforUploadMqtt = new MqttConnectOptions();
		MqttConnectOptions optionforRpcMqtt = new MqttConnectOptions();
		
		optionforUploadMqtt.setCleanSession(true);
		//optionforRpcMqtt.setCleanSession(true);
		optionforRpcMqtt.setKeepAliveInterval(2);
		optionforRpcMqtt.setUserName(Config.RPC_DEVICE_ACCESSTOKEN);
		
		rpcMqtt.setCallback(new RpcMessageCallBack(rpcMqtt));
	//	uploadMqtt.setCallback(new DataMessageCallBack());
		
//		uploadMqtt.connect(optionforUploadMqtt);
		rpcMqtt.connect(optionforRpcMqtt);
		//rpcMqtt.subscribe();
		rpcMqtt.subscribe(Config.RPC_TOPIC,1);
	
		
	}catch(Exception e){
		e.printStackTrace();
		Log.e("12345", "init rpc mqtt fail");
	}		
	}

	
//	public synchronized boolean publish(String topic,String data){
//	try{
//		MqttMessage msg = new MqttMessage();
//		msg.setPayload(data.getBytes(Charset.forName("utf-8")));
//		uploadMqtt.publish(topic, msg);
//		return true;
//	}catch(Exception e){
//		e.printStackTrace();
//		return false;
//	}
//	}
	public synchronized boolean rpcPublish(String topic,String data){
	try{
		MqttMessage msg = new MqttMessage();
		msg.setPayload(data.getBytes(Charset.forName("utf-8")));
		rpcMqtt.publish(topic, msg);		
		return true;
	}catch(Exception e){
		e.printStackTrace();
		return false;
	}
	}
	
}
