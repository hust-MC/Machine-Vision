package com.machineversion.sub_option;

public class SystemSetting_devicePacket
{
	private byte[] data;
	private int cursor;

	public byte[] getData()
	{
		return data;
	}

	public int getCursor()
	{
		return cursor;
	}

	public void setCursor(int cursor)
	{
		this.cursor = cursor;
	}

	public SystemSetting_devicePacket write(byte b)
	{
		data[cursor++] = b;
		return this;
	}

	public SystemSetting_devicePacket write(short s)
	{
		if (cursor % 2 != 0)
		{
			cursor++;
		}
		data[cursor++] = (byte) s;
		data[cursor++] = (byte) (s >> 8);

		return this;
	}

	public SystemSetting_devicePacket write(int i)
	{
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

	public static class Gerneral
	{
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

	public static class Trigger
	{
		int trigDelay; // 0.1mm
		int partDelay; // 0.1mm
		int velocity;  // mm/s
		int departWide;	// ms
		int expLead;	// us

		short checksum;
	}

	public static class AD9849
	{

	}
}
