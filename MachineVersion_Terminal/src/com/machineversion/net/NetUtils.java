package com.machineversion.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.machineversion.sub_option.SystemSetting_devicePacket.Net;

public class NetUtils
{
	/**
	 * 设置本地IP地址
	 */

	/**
	 * 三种端口定义
	 */
	public final static int listenBroadCastPort = 6019;
	public final static int sendIpPort = 6018;
	public final static int port = 6020;
	public static String ip = "115.156.211.190";
	/**
	 * 指令集常量定义
	 */
	public final static int MSG_NET_GET_VIDEO = 1;
	public final static int MSG_NET_GENERAL = 2;
	public final static int MSG_NET_ISL12026 = 6;
	public final static int MSG_NET_AD9849 = 7;
	public final static int MSG_NET_AT25040 = 8;
	public final static int MSG_NET_TEXTINFO = 9;
	public final static int MSG_NET_LINKINFO = 10;
	public final static int MSG_NET_NORMAL = 11;
	public final static int MSG_NET_FLASH = 12;
	public final static int MSG_NET_STATE = 14;
	public final static int MSG_NET_SEND_IMAGE = 15;
	public final static int MSG_NET_GET_RAW = 16;
	public final static int MSG_NET_UARTHECC = 17;
	public final static int MSG_NET_CONFSAVE = 18;
	public final static int MSG_NET_SETNET = 19;
	public final static int MSG_NET_FACTRESET = 20;
	public final static int MSG_NET_GET_PARAM = 21;
	public final static int MSG_NET_SAVE_VIDEO = 22;
	public final static int MSG_NET_TRIGGER = 23;
	public final static int MSG_NET_DSPTRIG = 24;
	public final static int MSG_NET_HELP_ALG_CMD = 25;
	public final static int MSG_NET_GET_JSON = 26;
	public final static int MSG_NET_ALGRESULT = 200;

	/*
	 * 获取本地IP
	 */
	static
	{
		Enumeration<NetworkInterface> en = null;
		try
		{
			en = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (en.hasMoreElements())
		{
			NetworkInterface intf = en.nextElement();
			if (intf.getName().toLowerCase().equals("eth0"))
			{
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						String ipaddress = inetAddress.getHostAddress()
								.toString();
						if (!ipaddress.contains("::"))
						{// ipV6的地址
							ip = ipaddress;
						}
					}
				}
			}
			else
			{
				continue;
			}
		}
	}

	/**
	 * 网络数据包
	 */
	public static class NetPacket
	{
		public int type;
		public int block;
		public int minid;
		public byte[] data;

		public void setData(byte[] data)
		{
			this.data = data;
		}
		public void send(OutputStream os)
		{
			DataPack.sendDataPack(this, os);
			try
			{
				Thread.sleep(60);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}		// 线程延时100ms,降低发送频率

		}
		public void recvDataPack(InputStream is)
		{
			NetPacket packet = DataPack.recvDataPack(is);

			if (packet == null)
			{
				type = 0xaa;
			}
			else
			{
				type = packet.type;
				block = packet.block;
				minid = packet.minid;

				if (packet.data != null)
				{
					data = packet.data;
				}
			}
		}
	}

	public static class GetVideo extends NetPacket
	{
		public GetVideo()
		{
			minid = MSG_NET_GET_VIDEO;
			type = 1;
			block = 5000;
		}
	}

	public static class Normal extends NetPacket
	{
		public Normal()
		{
			minid = MSG_NET_NORMAL;
			type = 0;
			block = 0;
		}
	}

	public static class State extends NetPacket
	{
		public State()
		{
			minid = MSG_NET_STATE;
			type = 1;
			block = 50000;
			data = new byte[]
			{ 0x01, 0, 0, 0 };							// 设置子命令
		}
	}

	public static class SendImage extends NetPacket
	{
		public SendImage()
		{
			minid = MSG_NET_SEND_IMAGE;
			type = 2;
			block = 0;				// 可设为任意值
		}
	}

	public static class GeneralInfo extends NetPacket
	{
		public GeneralInfo()
		{
			minid = MSG_NET_GENERAL;
			type = 0;
			block = 0;
		}
	}

	public static class GetParam extends NetPacket
	{
		public GetParam()
		{
			minid = MSG_NET_GET_PARAM;
			type = 1;
			block = 1000;
		}
	}

	public static class GetJson extends NetPacket
	{
		public GetJson()
		{
			minid = MSG_NET_GET_JSON;
			type = 1;
			block = 1000;
		}
	}

	public static class SetJson extends NetPacket
	{
		public SetJson()
		{
			minid = MSG_NET_GET_JSON;
			type = 0;
			block = 0;
		}
	}
}
