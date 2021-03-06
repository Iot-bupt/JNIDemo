package com.fbee.zllctl;

import java.io.Serializable;

/**
 * ÿ�����������������У�tasktype��(senceId / �̵�ַ) ��endpoint��Device Id
 * ,data1��data2��data3,data4,data5,data6,data7 ����ͨ��tasktype ��ȷ�����������ͣ� ��ʱ�� 0x01
 * ������ 0x02 �豸�� 0x03
 *
 * @author Administrator
 */
public class TaskDeviceAction implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -6788487849446346915L;
    private int uId;
    // �˿ں�
    private short deviceId;

    private byte condition1;
    private int data1;
    private byte condition2;
    private int data2;

    public int getData2()
    {
        return data2; 
    }

    public void setData2(int data2)
    {
        this.data2 = data2;
    }

    public byte getCondition1()
    {
        return condition1;
    }

    public void setCondition1(byte condition1)
    {
        this.condition1 = condition1;
    }

    public byte getCondition2()
    {
        return condition2;
    }

    public void setCondition2(byte condition2)
    {
        this.condition2 = condition2;
    }

    public int getData1()
    {
        return data1;
    }

    public void setData1(int data1)
    {
        this.data1 = data1;
    }

    public int getuId()
    {
        return uId;
    }

    public void setuId(int uId)
    {
        this.uId = uId;
    }

    public short getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(short deviceId)
    {
        this.deviceId = deviceId;
    }

}
