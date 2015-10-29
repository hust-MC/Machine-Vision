package com.machineversion.sub_option;

import java.io.Serializable;
import java.util.Arrays;

public class SystemSetting_devicePacket
{
	private byte[] data;
	private int cursor;
	private int maxType = TYPE_BYTE;

	private static final int TYPE_BYTE = 0;
	private static final int TYPE_SHORT = 1;
	private static final int TYPE_INT = 2;

	public SystemSetting_devicePacket()
	{
		data = new byte[1024];
	}

	/**
	 * 获取最后对齐的数组，还需要在最后面补齐
	 * 
	 * @return 已经按照结构体方式对齐的数组
	 */
	public byte[] getData()
	{
		if (maxType == TYPE_SHORT)
		{
			if (cursor % 2 != 0)
			{
				cursor++;
			}
		}

		else if (maxType == TYPE_INT)
		{
			if (cursor % 4 != 0)
			{
				cursor += (4 - cursor % 4);
			}
		}

		return Arrays.copyOf(data, cursor);
	}

	/**
	 * 获取当前数据长度
	 * 
	 * @return 数组的长度
	 */
	public int getSize()
	{
		return cursor;
	}

	/**
	 * 设置游标
	 * 
	 * @param cursor
	 *            游标位置
	 */
	public void setCursor(int cursor)
	{
		this.cursor = cursor;
	}

	public SystemSetting_devicePacket write(byte b)
	{

		data[cursor++] = b;

		return this;
	}

	public SystemSetting_devicePacket write(byte[] bArray)
	{
		for (byte b : bArray)
		{
			write(b);
		}
		return this;
	}

	public SystemSetting_devicePacket write(short s)
	{
		if (maxType < TYPE_SHORT)
		{
			maxType = TYPE_SHORT;
		}

		if (cursor % 2 != 0)
		{
			cursor++;
		}
		data[cursor++] = (byte) s;
		data[cursor++] = (byte) (s >> 8);

		return this;
	}

	public SystemSetting_devicePacket write(short[] sArray)
	{
		for (short s : sArray)
		{
			write(s);
		}

		return this;
	}

	public SystemSetting_devicePacket write(int i)
	{
		if (maxType < TYPE_INT)
		{
			maxType = TYPE_INT;
		}

		if (cursor % 4 != 0)
		{
			cursor += (4 - cursor % 4);
		}
		data[cursor++] = (byte) i;
		data[cursor++] = (byte) (i >> 8);
		data[cursor++] = (byte) (i >> 16);
		data[cursor++] = (byte) (i >> 24);

		return this;
	}

	public SystemSetting_devicePacket write(int[] iArray)
	{
		for (int i : iArray)
		{
			write(i);
		}
		return this;
	}

	public static class General implements DevicePacketBuilt
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 6803891809250009377L;

		public static final int LENGTH = 160;

		/* input camera type CAMERA_Type */
		byte input;
		/* output display way 0:lcd; 1:net; 2:crt */
		byte output;
		/* fpga 获取图像数据位数 0：8bit; 1:16bits */
		byte bitType;
		/* 所使用的算法 */
		byte algorithm;
		/* fpga 控制曝光时间 0-65535 */
		short expTime;

		byte inited;
		/* 触发模式选择 0->auto, 1->dsp, 2->outside */
		byte trigger;
		/* ccdc 获取图像数据横向起始位置 */
		short horzStartPix;
		/* ccdc 获取图像数据纵向起始位置 */
		short vertStartPix;
		/* ccdc 获取图像数据实际宽度 */
		short inWidth;
		/* ccdc 获取图像数据实际高度 */
		short inHeight;

		short outWidth;
		short outHeight;

		public byte[] buildData()
		{
			SystemSetting_devicePacket packet = new SystemSetting_devicePacket();

			packet.write(input).write(output).write(bitType).write(algorithm)
					.write(expTime).write(inited).write(trigger)
					.write(horzStartPix).write(vertStartPix).write(inWidth)
					.write(inHeight).write(outWidth).write(outHeight);

			return packet.getData();
		}
	}

	public static class Trigger implements DevicePacketBuilt
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6661255058156487062L;
		int trigDelay; // 0.1mm
		int partDelay; // 0.1mm
		int velocity;  // mm/s
		int departWide;	// ms
		int expLead;	// us
		short checksum;

		public byte[] buildData()
		{
			SystemSetting_devicePacket packet = new SystemSetting_devicePacket();

			packet.write(trigDelay).write(partDelay).write(velocity)
					.write(departWide).write(expLead);

			return packet.getData();
		}
	}

	public static class AD9849 implements DevicePacketBuilt
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1625998561140958827L;
		byte[] vga = new byte[2];
		byte[] pxga = new byte[4];
		byte[] hxdrv = new byte[4];
		byte rgdrv;
		byte shp, shd;
		byte hpl, hnl;
		byte rgpl, rgnl;

		public byte[] buildData()
		{
			SystemSetting_devicePacket packet = new SystemSetting_devicePacket();
			packet.write(vga).write(pxga).write(hxdrv).write(rgdrv).write(shp)
					.write(shd).write(hpl).write(hnl).write(rgpl).write(rgnl);
			return packet.getData();
		}
	}

	interface DevicePacketBuilt extends Serializable
	{
		public byte[] buildData();
	}
}
