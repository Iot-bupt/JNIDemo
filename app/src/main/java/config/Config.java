package config;
import java.util.concurrent.atomic.AtomicInteger;;

public class Config {

	public static String HOST = "tcp://10.108.218.108:1883";
	public static String RPC_DEVICE_ACCESSTOKEN = "rpc-proxy";
	public static String RPC_TOPIC = "v1/devices/me/rpc/request/+";
	public static String datatopic = "v1/devices/me/telemetry";
	public static String attributetopic = "v1/devices/me/attributes";


}
