package mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.Charset;

import config.Config;

/**
 * Created by zy on 2018/5/16.
 */

public class RpcMqttClient {

    public static String rpcToken = "";
    public static String RPC_TOPIC = "";
    static MqttClient rpcMqtt;
    public static void init(){
        try{
            rpcMqtt.disconnect();
            rpcMqtt.close();
            rpcMqtt = null;
            rpcMqtt = new MqttClient(Config.HOST,"receiveRPC",new MemoryPersistence());
            MqttConnectOptions optionforRpcMqtt = new MqttConnectOptions();
            optionforRpcMqtt.setCleanSession(true);
            optionforRpcMqtt.setKeepAliveInterval(2);
            optionforRpcMqtt.setUserName(rpcToken);
            rpcMqtt.setCallback(new RpcMessageCallBack(rpcMqtt));
            rpcMqtt.connect(optionforRpcMqtt);
            rpcMqtt.subscribe(RPC_TOPIC,1);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public static void publicResponce(String topic,String data) throws Exception{
        MqttMessage msg = new MqttMessage();
        msg.setPayload(data.getBytes(Charset.forName("utf-8")));
        if(rpcMqtt.isConnected()){
            rpcMqtt.publish(topic, msg);
        }
    }
}
