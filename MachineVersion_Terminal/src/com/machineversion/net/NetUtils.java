package com.machineversion.net;

import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class NetUtils
{
	/**
	 * 设置本地IP地址
	 */
	public final static String ip = "115.156.211.22";
	/**
	 * 三种端口定义
	 */
	public final static int listenBroadCastPort = 6019;
	public final static int sendIpPort = 6018;
	public final static int port = 6020;

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
	public final static int MSG_NET_ALGRESULT = 200;

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
				Thread.sleep(150);
			} catch (InterruptedException e)
			{
				Log.e("ZY", "sleep exception");
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
}
