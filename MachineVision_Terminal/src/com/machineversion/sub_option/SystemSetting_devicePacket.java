package com.machineversion.sub_option;

public class SystemSetting_devicePacket
{
	public static class General
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

	public static class Sensor
	{
		private static Sensor sensor;

		private Sensor()
		{
		}

		public int width_max;
		public int height_max;
		public int width_input;
		public int height_input;
		public int startPixel_height;
		public int startPixel_width;
		public int sensor_number;
		public int isColour;
		public int bitPixel;
	}

	public static class Mode
	{

		private static Mode mode;

		private Mode()
		{

		}

		public int expoTime;
		public int trigger;
		public int algorithm;
		public int width_crt;
		public int height_crt;
		public int work_mode;
		public int bitType;
	}

	public static class Trigger
	{
		private static Trigger trigger;

		private Trigger()
		{
		}

		public int trigDelay; // 0.1mm
		public int partDelay; // 0.1mm
		public int velocity;  // mm/s
		public int departWide;	// ms
		public int expLead;	// us
		public int checksum;
	}

	public static class AD9849
	{
		static AD9849 ad9849;

		private AD9849()
		{
		}

		public int[] vga = new int[2];
		public int[] pxga = new int[4];
		public int[] hxdrv = new int[4];
		public int rgdrv;
		public int shp, shd;
		public int hpl, hnl;
		public int rgpl, rgnl;
		public int[] pageContents = new int[16];				// 方便界面显示
	}

	public static class MT9V032
	{
		static MT9V032 mt9v032;

		private MT9V032()
		{
		}

		public int isAgc;
		public int isAec;
		public int agVal;
		public int aeVal;
	}

	public static class ISL12026
	{
		static ISL12026 isl12026;

		private ISL12026()
		{

		}

		public int[] time = new int[8];
		public int[] alarm0 = new int[8];
		public int[] alarm1 = new int[8];
	}

	public static class Net
	{
		static Net net;

		private Net()
		{
		}

		public int port;
		public int work_mode;	// 1,2 tcpip server and client; 3,4 udp
		public int[] ip_address = new int[4];
		public int[] remote_ip = new int[4];
		public int[] mac_address = new int[6];
		public int[] gateway = new int[4];
		public int[] ip_mask = new int[4];
		public int[] dns = new int[4];

		public int checksum;
	}

	public static class UART
	{
		static UART uart;

		private UART()
		{
		}

		public int baudRate;
		public int work_mode;
		public int checksum;
	}

	public static class HECC
	{
		static HECC hecc;

		private HECC()
		{
		}

		public int baudRate;
		public int id;
		public int checksum;
	}

	public static class AT25040
	{

		static AT25040 at25040;

		private AT25040()
		{
		}

		public int version;
		public int[] macAddr = new int[6];
		public int[] ipAddr = new int[4];
		public int port;
	}

	public static class Version
	{
		static Version version1;

		private Version()
		{
		}

		public int[] id = new int[4];
		public int version;
		public int[] write_time = new int[4];
	}

	public static class Parameters
	{
		private static Parameters parameters;

		private Parameters()
		{
		}
		public static Parameters getInstance()
		{
			if (parameters == null)
			{
				parameters = new Parameters();
				parameters.hecc = new HECC();
				parameters.mode = new Mode();
				parameters.net = new Net();
				parameters.version = new Version();
				parameters.sensor = new Sensor();
				parameters.ad9849 = new AD9849();
				parameters.trigger = new Trigger();
				parameters.uart = new UART();
			}

			return parameters;
		}
		public static void setInstance(Parameters p)
		{
			parameters = p;
		}

		public Net net;
		public HECC hecc;
		public UART uart;
		public Sensor sensor;
		public Mode mode;
		public Version version;
		public AD9849 ad9849;
		public Trigger trigger;
	}
}
