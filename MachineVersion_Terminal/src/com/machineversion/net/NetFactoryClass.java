package com.machineversion.net;

import com.machineversion.net.NetUtils.*;

public class NetFactoryClass
{
	static interface NetFactory
	{
		NetPacket CreatePacket();
	}

	public static class GetStateFactory implements NetFactory
	{
		@Override
		public NetPacket CreatePacket()
		{
			return new State();
		}
	}

	public static class GetVideoFactory implements NetFactory
	{
		@Override
		public NetPacket CreatePacket()
		{
			return new GetVideoPacket();
		}
	}

}