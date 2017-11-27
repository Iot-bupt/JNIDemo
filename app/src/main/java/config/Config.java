package config;
import java.util.concurrent.atomic.AtomicInteger;;

public class Config {
	
	public static String THINGSBOARD_MQTT_HOST = "tcp://10.108.217.227:1883";
	public static String DATACACHE_MQTT_HOST = "tcp://10.108.219.132:1883";
	public static String RPC_DEVICE_ACCESSTOKEN = "rEYZzdRoLuLW1Rk5T7f8";
	public static String RPC_TOPIC = "v1/devices/me/rpc/request/+";
	public static String DATA_TOPIC = "iotTest";
	public static volatile boolean    ISFIRST = true;
}
