package com.machineversion.sub_option;

public class SystemSetting_device
{
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
			byte[] data = null;
			int cursor = 0;

			// data[cursor] =

			return data;
		}
	}

	public static class Trigger
	{

	}

	public static class AD9849
	{

	}
}
