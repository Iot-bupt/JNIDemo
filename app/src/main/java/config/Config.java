package config;
import java.util.concurrent.atomic.AtomicInteger;;

public class Config {
	
//	public static String THINGSBOARD_MQTT_HOST = "tcp://13.113.21.176:1883";
	public static String THINGSBOARD_MQTT_HOST = "tcp://39.104.84.131:1883";
	public static String DATACACHE_MQTT_HOST = "tcp://10.108.218.64:1883";
//	public static String RPC_DEVICE_ACCESSTOKEN = "rEYZzdRoLuLW1Rk5T7f8";
	public static String RPC_DEVICE_ACCESSTOKEN = "rpc-proxy";
	public static String RPC_TOPIC = "v1/devices/me/rpc/request/+";
	public static String DATA_TOPIC = "iotTest";
	public static volatile boolean ISFIRST = true;
}
