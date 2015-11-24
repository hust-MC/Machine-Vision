package com.machineversion.sub_option;

import java.io.Serializable;

public class SystemSetting_devicePacket
{
	public static class General extends DevicePacketBuilt
	{
		General general;

		private General()
		{
		}

		public General getInstance()
		{
			if (general == null)
			{
				general = new General();
			}
			return general;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 6803891809250009377L;

		public static final int LENGTH = 160;

		/* input camera type CAMERA_Type */
		public String input;
		/* output display way 0:lcd; 1:net; 2:crt */
		public String output;
		/* fpga 获取图像数据位数 0：8bit; 1:16bits */
		public String bitType;
		/* 所使用的算法 */
		public String algorithm;
		/* fpga 控制曝光时间 0-65535 */
		public String expTime;

		public String inited;
		/* 触发模式选择 0->auto, 1->dsp, 2->outside */
		public String trigger;
		/* ccdc 获取图像数据横向起始位置 */
		public String horzStartPix;
		/* ccdc 获取图像数据纵向起始位置 */
		public String vertStartPix;
		/* ccdc 获取图像数据实际宽度 */
		public String inWidth;
		/* ccdc 获取图像数据实际高度 */
		public String inHeight;

		public String outWidth;
		public String outHeight;
	}

	public static class Trigger extends DevicePacketBuilt
	{
		Trigger trigger;

		private Trigger()
		{
		}

		public Trigger getInstance()
		{
			if (trigger == null)
			{
				trigger = new Trigger();
			}
			return trigger;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -6661255058156487062L;
		public String trigDelay; // 0.1mm
		public String partDelay; // 0.1mm
		public String velocity;  // mm/s
		public String departWide;	// ms
		public String expLead;	// us
		public String checksum;
	}

	public static class AD9849 extends DevicePacketBuilt
	{
		AD9849 ad9849;

		private AD9849()
		{
		}

		public AD9849 getInstance()
		{
			if (ad9849 == null)
			{
				ad9849 = new AD9849();
			}
			return ad9849;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1625998561140958827L;
		public String[] vga = new String[2];
		public String[] pxga = new String[4];
		public String[] hxdrv = new String[4];
		public String rgdrv;
		public String shp, shd;
		public String hpl, hnl;
		public String rgpl, rgnl;
	}
}

class DevicePacketBuilt implements Serializable
{
	public byte[] buildData()
	{
		return null;
	}
}