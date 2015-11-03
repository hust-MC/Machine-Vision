package com.machineversion.net;

import java.io.ByteArrayInputStream;
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

	public static int timeoutCount = 0;

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
				tempDos.flush();
			}

			dos.write(baos.toByteArray());
			dos.flush();
			Log.d("MC", "send over:" + baos.toByteArray().length);
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	public static NetPacket recvDataPack(InputStream is)
	{
		NetPacket revPacket = new NetPacket();
		Log.d("MC", "start read:");
		boolean hasMagicRead = false;
		int bufCount = 0;
		int startPos = 0;
		int availableCount = 0;

		byte[] rxBuf = new byte[2 * 1024 * 1024];

		try
		{
			bufCount = is.read(rxBuf);
			Log.d("MC", "rxBuf read finish,count = " + bufCount);
			/*
			 * 判断帧头标示
			 */
			for (int i = 0; i < bufCount - 1; i++)
			{
				if (rxBuf[i] == 0x5a && rxBuf[i + 1] == 0x69
						&& rxBuf[i + 2] == 0x5a && rxBuf[i + 3] == 0x69)
				{
					startPos = i;
					hasMagicRead = true;

					if (startPos != 0)
					{
						Log.e("MC", "startPos=" + startPos);
					}
					break;
				}
			}
			if (!hasMagicRead)
			{
				Log.d("ZY", "hasn't read magic");
				return null;
			}
			availableCount = bufCount - startPos;						// 计算有效长度

			/*
			 * 如果有效长度大于offset，则帧头接收完毕
			 */
			if (availableCount >= offset)
			{
				/*
				 * 解析帧头
				 */
				DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(rxBuf));
				dis.skip(startPos + 4);

				int versionData = readLittleInt(dis);
				if (versionData != version)
				{
					Log.e("ZY", "Version=" + versionData);
					return null;
				}

				int type = readLittleInt(dis);
				int block = readLittleInt(dis);
				int length = readLittleInt(dis);
				int len = length - offset;
				if (readLittleInt(dis) != offset)
				{
					Log.e("MC", "offset=" + offset);
					return null;
				}

				revPacket.minid = readLittleInt(dis);

				if (availableCount < length)								// 判断整包是否接收完毕
				{
					// 将剩余部分接收完并拼接到一起
					int tempCount = 0;
					int tempPos = 0;
					byte[] temp = new byte[length - availableCount];
					do
					{
						tempCount = is.read(temp, tempPos, temp.length
								- tempPos);
						tempPos += tempCount;
					} while (tempPos < length - availableCount);
					System.arraycopy(temp, 0, rxBuf, startPos + availableCount,
							tempPos);
				}

				/*
				 * 接收data数组
				 */
				if (len > 0)
				{
					revPacket.data = new byte[len];
					int count = 0, pos = 0;
					byte[] temp = new byte[len];
					do
					{
						count = dis.read(temp, pos, temp.length - pos);
						pos += count;
					} while (count > 0 && pos < len);
					System.arraycopy(temp, 0, revPacket.data, 0, pos);
				}
				return revPacket;
			}
			else
			{
				Log.d("ZY", "bufCount = " + bufCount);
			}
		} catch (IOException e)
		{
			Log.d("ZY", "time out");
			timeoutCount++;
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
		int ret = swapShortToLittleEndian((short) ((data >> 16) & 0xFFFF))
				| (swapShortToLittleEndian((short) data) << 16);
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
	public static int readLittleInt(DataInputStream dis) throws IOException
	{
		int data = dis.readInt();
		int ret = swapShortToLittleEndian((short) (data >> 16)) & 0xFFFF
				| (swapShortToLittleEndian((short) data) << 16);
		return ret;
	}
	/**
	 * 大小端蝶形交换
	 * 
	 * @param data
	 *            待交换的short数据
	 * @return 转换后的short数
	 */
	private static short swapShortToLittleEndian(short data)
	{
		short ret = (short) ((data << 8) | (data >> 8) & 0x00FF);
		ret = (short) (ret & 0xFFFF);
		return ret;
	}
}
