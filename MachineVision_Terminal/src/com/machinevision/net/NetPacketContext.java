package com.machinevision.net;

import java.io.OutputStream;

import android.util.Log;

import com.machinevision.net.NetUtils.*;

/**
 * 使用工厂方法模式根据需要创建相应的网络包。
 * @author MC
 *
 */
public class NetPacketContext
{
	NetPacket packet;

	/**
	 * 采用策略模式生成算法
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
		case NetUtils.MSG_NET_GENERAL:
			packet = new GeneralInfo();
			break;
		case NetUtils.MSG_NET_GET_PARAM:
			packet = new GetParam();
			break;
		case NetUtils.MSG_NET_GET_JSON:
			packet = new GetJson();
			break;
		case NetUtils.MSG_NET_SET_JSON:
			packet = new SetJson();
			break;
		case NetUtils.MSG_NET_SEND_BINARY:
			packet = new SendBinary();
			break;
		default:
			Log.e("MC", "netPacketContext: netPacket error");
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
