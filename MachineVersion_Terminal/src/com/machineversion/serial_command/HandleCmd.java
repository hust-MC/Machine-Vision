package com.machineversion.serial_command;

public class HandleCmd
{
	/************   终端->相机    ******************/
	public static final short MSG_UART_SET_NET = (short) 0xF950;
	public static final short MSG_UART_SET_UARTHECC = (short) 0xF163;
	public static final short MSG_UART_SET_WORKMODE = (short) 0xF84C;
	public static final short MSG_UART_SET_AD9849 = (short) 0xFA72;
	public static final short MSG_UART_RESTORE = (short) 0xCA7A;
	public static final short MSG_UART_SET_TIME = (short) 0xF44D;
	public static final short MSG_UART_SET_CMOS = (short) 0xF343;
	public static final short MSG_UART_GET_NUMBER = (short) 0xF253;
	public static final short MSG_UART_GET_PARAM = (short) 0xE129;
	public static final short MSG_UART_SEND_STATUS = (short) 0xB175;
	public static final short MSG_UART_SHOW_TIME = (short) 0xB264;
	public static final short MSG_UART_SHOW_TEMPER = (short) 0xB453;
	public static final short MSG_UART_SET_ALG = (short) 0xF537;

	/************   相机->终端     ******************/
	public static final short MSG_UART_GET_TEMPER = (short) 0xF744;
	public static final short MSG_UART_GET_TIME = (short) 0xF45E;
	public static final short MSG_UART_ACK = (short) 0xD143;

	CommandFrame frame;
	CommandSend send;

	public HandleCmd()
	{
	}

	public void sendCmd(short cmd)
	{
		frame = new CommandFrame();
		send = new CommandSend();

		switch (cmd)
		{
		case MSG_UART_SET_NET:
			frame.flag = MSG_UART_SET_NET;
		}
	}

	class CommandFrame
	{
		short flag;			// 2字节命令标识
		byte len;			// 1字节命令长度
		Object userData;	// 用户数据（不定长）
		byte checkSum;		// 1字节校验和
	}
}
