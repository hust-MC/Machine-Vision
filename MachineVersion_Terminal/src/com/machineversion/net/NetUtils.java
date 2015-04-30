package com.machineversion.net;

public class NetUtils
{
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
	class NetPacket
	{
		final int magic = 0x695a695a;
		final int version = 0;
		int type;
		int block;
		int length;
		final int offset = 20;
		int minid;
		byte[] data;

		public NetPacket(int minid)
		{
			this.minid = minid;
			switch (minid)
			{
			case MSG_NET_GET_VIDEO:
				type = 1;
				block = 5000;
				break;
			case MSG_NET_GENERAL:
				type = 0;
				block = 0;
				break;
			case MSG_NET_ISL12026:
				type = 1;
				block = 50000;
				break;
			case MSG_NET_AD9849:
				type = 0;
				block = 0;
				break;
			case MSG_NET_AT25040:
				type = 1;
				block = 50000;
				break;
			case MSG_NET_NORMAL:
				type = 0;
				block = 0;
				break;
			case MSG_NET_FLASH:
				type = 1;
				block = 50000;
				break;
			case 
			}
		}

	}

}
