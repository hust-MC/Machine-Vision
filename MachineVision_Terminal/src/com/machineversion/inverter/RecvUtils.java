package com.machineversion.inverter;


public class RecvUtils {
	//数据开始
	public static final byte STX = 0x02;
	//数据结束
	public static final byte ETX = 0x03;
	//没发现数据错误
	public static final byte ACK = 0x06;
	//数据错误
	public static final byte NAK = 0x15;	
	//数据结束(回车）
	public static final byte CR = 0x0D;
	
	public static final String[] alarms_title = {"没有报警", "OC1", "OC2", "OC3", "OV1", "OV2", "OV3", "THT", "THM", "FIN", "OLT", 
		"BE", "GF", "LF", "OHT", "OPT", "PE", "PUE", "RET", "P24", "E.3", "E.6", "E.7", "获取报警错误"};
	
	public static String getDataErr(byte input) {
		// TODO Auto-generated method stub
		switch(input) {
		case 0x30:
			return "终端NAK错误";
		case 0x31:
			return "奇偶校验错误";
		case 0x32:
			return "总和校验错误";
		case 0x33:
			return "协议错误";
		case 0x34:
			return "格式错误";
		case 0x35:
			return "溢出错误";
		case 0x37:
			return "字符错误";
		case 0x41:
			return "模式错误";
		case 0x42:
			return "指令代码错误";
		case 0x43:
			return "数据范围错误";
		}
		return null;
	}

	public static int getData(byte[] input) {
		// TODO Auto-generated method stub
		int data;
		switch(input.length) {
		case 9:
			data = ascToInt(input[3]) * 16 + ascToInt(input[4]);
			return data;
		case 11:
			data = (int) (ascToInt(input[3]) * 4096 + ascToInt(input[4]) * 256
					+ ascToInt(input[5]) * 16 + ascToInt(input[6]));
			return data;
		default:
			return 0;
		}
	}

	private static int ascToInt(byte a) {
		if (a >= '0' && a <= '9') 
			return (a - '0');
		else if (a >= 'A' && a <= 'F') 
			return (a - 'A' + 10);
		else if (a >= 'a' && a <= 'f')
			return (a - 'a' + 10);
		return 0;
	}


}
