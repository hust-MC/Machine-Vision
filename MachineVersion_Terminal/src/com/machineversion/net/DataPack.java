package com.machineversion.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.machineversion.net.NetUtils.NetPacket;

public class DataPack
{
	final static int magic = 0x695a695a;
	final static int version = 0;
	final static int offset = 28;

	public static boolean sendDataPack(NetUtils.NetPacket packet,
			OutputStream os)
	{
		DataOutputStream dos = new DataOutputStream(os);
		try
		{
			dos.writeInt(magic);
			dos.writeInt(version);
			dos.writeInt(packet.type);
			dos.writeInt(packet.block);
			dos.writeInt(packet.data.length + offset);
			dos.writeInt(offset);
			dos.writeInt(packet.minid);
			dos.write(packet.data);

			dos.flush();
			return true;
		} catch (IOException e)
		{
		}
		return false;
	}

	public static NetUtils.NetPacket recvDataPack(InputStream is)
	{
		int type = 0, block = 0;
		NetPacket revPacket = new NetPacket();

		DataInputStream dis = new DataInputStream(is);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try
		{
			if (dis.readInt() != magic || dis.readInt() != version)
			{
				return null;
			}

			type = dis.readInt();
			block = dis.readInt();

			int len = dis.readInt() - offset;
			byte[] data = new byte[len];

			if (dis.readInt() != offset)
			{
				return null;
			}

			revPacket.minid = dis.readInt();

			/*
			 * 接收data数组
			 */
			int count = 0;
			dis.read(revPacket.data);

			return revPacket;
		} catch (Exception e)
		{
		}
		return null;
	}
}
