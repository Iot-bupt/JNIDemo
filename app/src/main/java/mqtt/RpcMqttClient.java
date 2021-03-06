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

//    public static String rpcToken = "gbNJ8K5a0Hggwd66vHqn";
//    public static String RPC_TOPIC = "v1/devices/me/rpc/request/+";
    static MqttClient rpcMqtt;
    public static void init(){
        try{
            if(rpcMqtt!=null){
//                rpcMqtt.disconnect();
                rpcMqtt.close();
            }
            rpcMqtt = null;
            rpcMqtt = new MqttClient(Config.HOST,"receiveRPC",new MemoryPersistence());
            MqttConnectOptions optionforRpcMqtt = new MqttConnectOptions();
            optionforRpcMqtt.setCleanSession(true);
            optionforRpcMqtt.setConnectionTimeout(5);
            optionforRpcMqtt.setKeepAliveInterval(20);
            optionforRpcMqtt.setUserName(Config.RPC_DEVICE_ACCESSTOKEN);
            rpcMqtt.setCallback(new RpcMessageCallBack(rpcMqtt));
            rpcMqtt.connect(optionforRpcMqtt);
            rpcMqtt.subscribe(Config.RPC_TOPIC,1);
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
