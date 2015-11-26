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
		public int input;
		/* output display way 0:lcd; 1:net; 2:crt */
		public int output;
		/* fpga 获取图像数据位数 0：8bit; 1:16bits */
		public int bitType;
		/* 所使用的算法 */
		public int algorithm;
		/* fpga 控制曝光时间 0-65535 */
		public int expTime;

		public int inited;
		/* 触发模式选择 0->auto, 1->dsp, 2->outside */
		public int trigger;
		/* ccdc 获取图像数据横向起始位置 */
		public int horzStartPix;
		/* ccdc 获取图像数据纵向起始位置 */
		public int vertStartPix;
		/* ccdc 获取图像数据实际宽度 */
		public int inWidth;
		/* ccdc 获取图像数据实际高度 */
		public int inHeight;

		public int outWidth;
		public int outHeight;
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
		public int trigDelay; // 0.1mm
		public int partDelay; // 0.1mm
		public int velocity;  // mm/s
		public int departWide;	// ms
		public int expLead;	// us
		public int checksum;
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
		public int[] vga = new int[2];
		public int[] pxga = new int[4];
		public int[] hxdrv = new int[4];
		public int rgdrv;
		public int shp, shd;
		public int hpl, hnl;
		public int rgpl, rgnl;
	}

	public static class MT9V032 extends DevicePacketBuilt
	{
		MT9V032 mt9v032;

		private MT9V032()
		{
		}
		public MT9V032 getInstance()
		{
			if (mt9v032 == null)
			{
				mt9v032 = new MT9V032();
			}
			return mt9v032;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 7173873001878958176L;
		byte isAgc;
		byte isAec;
		short agVal;
		short aeVal;
	}

	public static class ISL12026 extends DevicePacketBuilt
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4929503182824463L;
		ISL12026 isl12026;

		private ISL12026()
		{

		}
		public ISL12026 getInstatce()
		{
			if (isl12026 == null)
			{
				isl12026 = new ISL12026();
			}
			return isl12026;
		}

		byte[] time = new byte[8];
		byte[] alarm0 = new byte[8];
		byte[] alarm1 = new byte[8];
	}

	public static class Net extends DevicePacketBuilt
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 6830004797705175554L;
		Net net;

		private Net()
		{
		}
		public Net getInstance()
		{
			if (net == null)
			{
				net = new Net();
			}
			return net;
		}

		short port;
		byte work_mode;	// 1,2 tcpip server and client; 3,4 udp
		byte[] ip_address = new byte[4];
		byte[] remote_ip = new byte[4];
		byte[] mac_address = new byte[6];
		byte[] gateway = new byte[4];
		byte[] ip_mask = new byte[4];
		byte[] dns = new byte[4];

		byte checksum;
	}

	public static class UART_HECC extends DevicePacketBuilt
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -8443600832368353788L;
		UART_HECC uart;

		private UART_HECC()
		{
		}
	}

}

class DevicePacketBuilt implements Serializable
{
	public byte[] buildData()
	{
		return null;
	}
}