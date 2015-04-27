package com.machineversion.serial_command;

import java.io.Serializable;

public class CommandRev implements Serializable
{
	class BOOT_ConfigParam
	{
		BOOT_ConfigNet netConf;
		BOOT_ConfigHecc heccConf;
		BOOT_ConfigUart uartConf;
		BOOT_ConfigSensor sencorConf;
		BOOT_ConfigMode modeConf;
		BOOT_RecordVersion versionRec;
		AD9849_Info ad9849Reg;
	}

	class BOOT_ConfigNet 			// 30
	{
		short port;					// 因为对齐原因，放在最前面
		byte work_mode;				// 1,2 tcpip server and client; 3,4 udp
		byte ip_address[] = new byte[4];
		byte remote_ip[] = new byte[4];
		byte mac_address[] = new byte[6];
		byte gateway[] = new byte[4];
		byte ip_mask[] = new byte[4];
		byte dns[] = new byte[4];

		byte checksum;
	}

	class BOOT_ConfigUart			// 3
	{
		byte baudRate;			// 1, 115200, 2, 19200, 3, 9600
		byte work_mode;			// D1~D0, 00 无校验，01 奇校验， 10，偶校验
						// D3~D2, 00 1位停止位， 10， 2位停止位
						// D7~D4，数据位数，5~8
		byte checksum;
	}

	class BOOT_ConfigHecc			// 3
	{
		byte baudRate;
		byte id;

		byte checksum;
	}

	class BOOT_ConfigSensor			// 16
	{
		short width_max;
		short height_max;
		short width_input;
		short height_input;
		short startPixel_width;
		short startPixel_height;
		byte sensor_number;
		byte isColour;
		byte bitPixel;

		byte checksum;
	}

	public class BOOT_ConfigMode implements Serializable		// 13
	{
		public int expoTime = 11;
		public byte trigger = 22;
		public byte algorithm = 33;
		public short width_crt = 44;
		public short height_crt = 55;
		public byte output_mode = 66;			// 1, crt, 2, net, 3, lcd
		public byte bitType = 77;

		public byte checksum = 0xf;
	}

	class BOOT_RecordVersion		// 13
	{
		byte id[] = new byte[4];
		short version;
		byte write_time[] = new byte[6];

		byte checksum;
	}

	class AD9849_Info				// 17
	{
		byte vga[] = new byte[2];
		byte pxga[] = new byte[4];
		byte hxdrv[] = new byte[4];
		byte rgdrv;
		byte shp, shd;
		byte hpl, hnl;
		byte rgpl, rgnl;
	}

}
