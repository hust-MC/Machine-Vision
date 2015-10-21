package com.machineversion.net;

import java.io.OutputStream;
import java.util.Arrays;

import android.util.Log;

import com.machineversion.net.NetUtils.*;

public class NetPacketContext
{
	NetPacket packet;

	/**
	 * 采用策略模式生成算法
	 * 
	 * @param type
	 *            根据类型判断需要生成的包
	 */
	public NetPacketContext(int type)
	{
		switch (type)
		{
		case NetUtils.MSG_NET_GET_VIDEO:
			packet = new GetVideo();
			break;

		case NetUtils.MSG_NET_NORMAL:
			packet = new Normal();
			break;

		case NetUtils.MSG_NET_STATE:
			packet = new State();
			break;
		default:
			Log.e("MC", "netPacket error");
		}
	}

	/**
	 * 发送数据包
	 * 
	 * @param os
	 *            socket输出流
	 */
	public void sendPacket(OutputStream os)
	{
		packet.send(os);

	}

	/**
	 * 设置数据包的数据区
	 * 
	 * @param data
	 *            数据包的数据区
	 */
	public void setData(byte[] data)
	{
		packet.setData(data);
	}
}
