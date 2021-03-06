package com.fbee.zllctl;

import java.io.Serializable; 
import java.util.Arrays;

public class DeviceInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String deviceSnid;
	private String deviceName;
	private byte deviceStatus;// 设备是否在线；
	private byte deviceState; // 设备状态（灯:开、关)
	private int uId;
	private short deviceId;
	private short ProfileId;
	public String type = "Unknown Device";

	private int Sensordata = 0;// 用来存储传感器设备上传的数据
	private short clusterId;
	private short attribID;
	// 判断设备类型
	public byte hasColourable = 0;
	public byte hasDimmable = 0;
	public byte hasSwitchable = 0;
	public byte hasThermometer = 0;
	public byte hasPowerUsage = 0;
	public byte hasOutSwitch = 0;
	public byte hasOutLeveL = 0;
	public byte hasOutColor = 0;
	public byte hasOutScene = 0;
	public byte hasOutGroup = 0;
	public byte hasSensor = 0; // 是传感器
	public byte issmartplug = 0; // 是智能开关
	public short zoneType = 0;
	private byte[] IEEE = new byte[8];
	public DeviceInfo() {
	
	}
	public DeviceInfo(String deviceName, int uId, short deviceId,
			short profileId, byte hasColourable, byte hasDimmable,
			byte hasSwitchable, byte hasThermometer, byte hasPowerUsage,
			byte hasOutSwitch, byte hasOutLeveL, byte hasOutColor,
			byte hasOutScene, byte hasOutGroup, byte hasSensor, byte issmartplug, short zoneType) {
		super();
		this.deviceName = deviceName;
		this.uId = uId;
		this.deviceId = deviceId;
		ProfileId = profileId;
		this.hasColourable = hasColourable;
		this.hasDimmable = hasDimmable;
		this.hasSwitchable = hasSwitchable;
		this.hasThermometer = hasThermometer;
		this.hasPowerUsage = hasPowerUsage;
		this.hasOutSwitch = hasOutSwitch;
		this.hasOutLeveL = hasOutLeveL;
		this.hasOutColor = hasOutColor;
		this.hasOutScene = hasOutScene;
		this.hasOutGroup = hasOutGroup;
		this.hasSensor = hasSensor;
		this.issmartplug = issmartplug;
		this.zoneType = zoneType;
	}

	public DeviceInfo(int uId, int data, short clusterId, short attribID,String deviceName) {
		// TODO Auto-generated constructor stub
		this.uId = uId;
		this.Sensordata = data;
		this.clusterId = clusterId;
		this.attribID = attribID;
		this.deviceName = deviceName;
	}
	public String getDeviceName() {
		if (deviceName == null) {
			return "";
		}
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getUId() {
		return uId;
	}

	public void setUId(int uid) {
		this.uId = uid;
	}

	public short getDeviceId() {
		return deviceId;
	}

	public short getProfileId() {
		return ProfileId;
	}

	public void setProfileId(short profileId) {
		ProfileId = profileId;
	}

	public byte getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(byte deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public byte getDeviceState() {
		return deviceState;
	}

	public void setDeviceState(byte deviceState) {
		this.deviceState = deviceState;
	}

	public int getSensordata() {
		return Sensordata;
	}

	public void setSensordata(int sensordata) {
		Sensordata = sensordata;
	}
	public short getClusterId() {
		return clusterId;
	}
	public void setClusterId(short clusterId) {
		this.clusterId = clusterId;
	}
	public short getAttribID() {
		return attribID;
	}
	public void setAttribID(short attribID) {
		this.attribID = attribID;
	}

	public String getDeviceSnid() {
		return deviceSnid;
	}
	@Override
	public String toString() {
		return "DeviceInfo [deviceSnid=" + deviceSnid + ", deviceName="
				+ deviceName + ", deviceStatus=" + deviceStatus
				+ ", deviceState=" + deviceState + ", uId=" + uId
				+ ", deviceId=" + deviceId + ", ProfileId=" + ProfileId
				+ ", type=" + type + ", Sensordata=" + Sensordata
				+ ", clusterId=" + clusterId + ", attribID=" + attribID
				+ ", hasColourable=" + hasColourable + ", hasDimmable="
				+ hasDimmable + ", hasSwitchable=" + hasSwitchable
				+ ", hasThermometer=" + hasThermometer + ", hasPowerUsage="
				+ hasPowerUsage + ", hasOutSwitch=" + hasOutSwitch
				+ ", hasOutLeveL=" + hasOutLeveL + ", hasOutColor="
				+ hasOutColor + ", hasOutScene=" + hasOutScene
				+ ", hasOutGroup=" + hasOutGroup + ", hasSensor=" + hasSensor
				+ ", issmartplug=" + issmartplug + ", zoneType=" + zoneType
				+ ", IEEE=" + Arrays.toString(IEEE) + "]";
	}
}
