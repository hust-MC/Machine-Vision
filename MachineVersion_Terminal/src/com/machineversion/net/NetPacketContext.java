package com.machineversion.net;

import android.util.Log;
import com.machineversion.net.NetUtils.*;

public class NetPacketContext
{
	NetPacket packet;

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
}
