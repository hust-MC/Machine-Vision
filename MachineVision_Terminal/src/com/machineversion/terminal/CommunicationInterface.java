package com.machineversion.terminal;

/*
 * 定义通信接口
 * @author MC
 */
public interface CommunicationInterface
{
	/**
	 * 打开通信连接
	 * 
	 * @author MC
	 */
	void open();					

	/**
	 * 关闭通信连接
	 * 
	 * @author MC
	 */
	void close();					

	/**
	 * 通过相应端口发送数据
	 * @param data 待发送的字符数组
	 * @param cmd 操作码
	 * 
	 * @author MC
	 */
	void send(final byte[] data, int cmd);		
}
