package mqtt;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.example.jnidemo.SecondActivity;

public class DataMessageCallBack implements MqttCallback{

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		SecondActivity.wrapper.init();
		Log.e("12345", "data connection lost" );
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
