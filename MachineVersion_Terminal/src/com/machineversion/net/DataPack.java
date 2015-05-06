package com.machineversion.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.machineversion.net.NetUtils.NetPacket;

public class DataPack
{
	final static int magic = 0x695a695a;
	final static int version = 0;
	final static int offset = 28;

	public static boolean sendDataPack(NetPacket packet, OutputStream os)
	{
		DataOutputStream dos = new DataOutputStream(os);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream tempDos = new DataOutputStream(baos);

		try
		{
			sendLittleInt(tempDos, magic);
			sendLittleInt(tempDos, version);
			sendLittleInt(tempDos, packet.type);
			sendLittleInt(tempDos, packet.block);
			sendLittleInt(tempDos, packet.data == null ? offset
					: packet.data.length + offset);
			sendLittleInt(tempDos, offset);
			sendLittleInt(tempDos, packet.minid);

			if (packet.data != null)
			{
				tempDos.write(packet.data);
			}
			tempDos.flush();

			dos.write(baos.toByteArray());
			dos.flush();
			return true;
		} catch (IOException e)
		{
		}
		return false;
	}

	public static NetPacket recvDataPack(InputStream is) throws IOException
	{
		int type = 0, block = 0;
		byte[] headBuf = new byte[28];

		NetPacket revPacket = new NetPacket();
		DataInputStream dis = new DataInputStream(is);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		dis.read(headBuf);

		try
		{
			if (readLittleInt(dis) != magic || readLittleInt(dis) != version)
			{
				return null;
			}

			type = readLittleInt(dis);
			block = readLittleInt(dis);

			int length = readLittleInt(dis);

			int len = length - offset;
			revPacket.data = new byte[(int) len];

			if (readLittleInt(dis) != offset)
			{
				return null;
			}

			revPacket.minid = readLittleInt(dis);

			/*
			 * 接收data数组
			 */
			int count = 0, pos = 0;
			byte[] temp = new byte[1024];
			while ((count = dis.read(temp)) != -1)
			{
				// tbd
			}
			Log.d("MC", "break");
			return revPacket;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 以小端形式发送一个short数据
	 * 
	 * @param dos
	 *            发送输出流
	 * @param data
	 *            待发送的short数据
	 * @throws IOException
	 */
	private static void sendLittleShort(DataOutputStream dos, short data)
			throws IOException
	{
		dos.writeShort(swapShortToLittleEndian(data));
	}

	/**
	 * 以小端形式接收一个short数据
	 * 
	 * @param dis
	 *            接收输入流
	 * @return 小端形式的short数据
	 * @throws IOException
	 */
	private static short readLittleShort(DataInputStream dis)
			throws IOException
	{
		short data = dis.readShort();
		return swapShortToLittleEndian(data);
	}

	/**
	 * 以小端形式接收一个int数据
	 * 
	 * @param dos
	 *            发送输出流
	 * @param data
	 *            待发送的数据
	 * @throws IOException
	 */
	private static void sendLittleInt(DataOutputStream dos, int data)
			throws IOException
	{
		int ret = swapShortToLittleEndian((short) (data >> 16))
				| (swapShortToLittleEndian((short) data) << 16);
		Log.d("MC", Integer.toHexString(ret));
		dos.writeInt(ret);
	}

	/**
	 * 以小端形式读取一个int数据
	 * 
	 * @param dis
	 *            接收输入流
	 * @return 小端形式的int数
	 * @throws IOException
	 */
	private static int readLittleInt(DataInputStream dis) throws IOException
	{
		int data = dis.readInt();
		if (data < 0)
		{
			data = ~data + 1;
		}

		return swapShortToLittleEndian((short) (data >> 16))
				| (swapShortToLittleEndian((short) data) << 16);
	}
	/**
	 * 大小端蝶形交换
	 * 
	 * @param data
	 *            待交换的short数据
	 * @return 转换后的short数
	 */
	private static short swapShortToLittleEndian(int data)
	{
		return (short) ((data << 8) | (data >> 8) & 0x00FF);
	}

}
