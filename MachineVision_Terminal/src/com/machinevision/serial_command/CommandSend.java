package com.machinevision.serial_command;

public class CommandSend
{
	class BOOT_Config_Net
	{
		short port;
		byte work_mode;
		byte ip_address[] = new byte[4];
		byte remote_ip[] = new byte[4];
		byte mac_address[] = new byte[6];
		byte gateway[] = new byte[4];
		byte ip_mask[] = new byte[4];
		byte dns[] = new byte[4];
	}

	class UARTH_Info
	{
		byte heccBaudrate;
		byte uartBaudrate;
		byte uartStop;
		byte uartDataLen;
		byte uartParity;
		byte heccID;
	}

	class GENERAL_Info
	{
		byte input;
		byte output;
		byte bitType;
		byte algorithm;
		short expTime;
		byte inited;
		byte trigger;
		short horzStartPix;
		short vertStartPix;
		short inWidth;
		short inHetght;
		short outWidth;
		short outHeight;
	}

	class AD9849_Info
	{
		byte vga[] = new byte[2];       // VGA gain
		byte pxga[] = new byte[4];      // PxGA Color x Gain
		byte hxdrv[] = new byte[4];     // Hx Drive Strength
		byte rgdrv;          			// RG Drive Strength
		byte shp, shd;
		byte hpl, hnl;
		byte rgpl, rgnl;
	}

	class RTC_Time
	{
		byte tm_sec;     // SC - second
		byte tm_min;     // MN - minuite
		byte tm_hour;    // HR - hour
		byte tm_mday;    // DT - month day
		byte tm_mon;     // MO - month
		byte tm_year;    // YR - year
		byte tm_wday;    // DW - week day
		byte tm_yday;    // Y2K -
	}
}
