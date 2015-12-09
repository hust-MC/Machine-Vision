package com.machinevision.serial_jni;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy.Parameters;

import android.R.anim;

public class SciClass
{
	/**
	 * 奇偶校验位的设置
	 */
	public final static int OLD_CHECK = 1;
	public final static int NONE_CHECK = 0;
	public final static int EVEN_CHECK = 2;

	private FileDescriptor fd;				// 申明文件描述类用于本地C程序调用
	private FileInputStream in;
	private FileOutputStream out;
	private static SciClass sci;

	/**
	 * 串口服务类的单例模式
	 * 
	 * @author MC
	 */
	private SciClass()
	{
	}

	/**
	 * 获取SCI实例，单例模式
	 * 
	 * @return sci 当前的SCI实例
	 */
	public static SciClass getInstance()
	{
		if (sci == null)
		{
			sci = new SciClass();
		}
		return sci;
	}

	// =============控制数据流的开关=================
	private void openFileStream(FileDescriptor fd)
	{
		this.fd = fd;
		in = new FileInputStream(fd);
		out = new FileOutputStream(fd);
	}

	private void closeFileStream()
	{
		try
		{
			in.close();
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// =================END=======================

	// ==========================应用程序接口==============================
	/**
	 * 打开串口卡设备
	 * 
	 * @param device
	 *            串口文件对象
	 * @param baud
	 *            波特率
	 * @param checkout
	 *            奇偶校验位
	 * @return 串口文件对应的文件描述符
	 * 
	 * @author MC
	 */
	public FileDescriptor openSerialPort(File device, int baud, int checkout)
	{
		return open(device.getAbsolutePath(), baud, checkout);
	}

	/**
	 * 向串口设备写入数据
	 * 
	 * @param fd
	 *            相应串口的文件描述符
	 * @param data
	 *            待发送的数据
	 * @throws IOException
	 * 
	 * @author MC
	 */
	public void write(FileDescriptor fd, byte[] data) throws IOException
	{
		out.write(data);
	}

	/**
	 * 从SCI读取数据到buf中
	 * 
	 * @param buf
	 *            存储从SCI读取的数据
	 * @return 返回实际读取的字节数
	 * @author MC
	 */
	public int read(byte[] buf) throws IOException
	{
		return in.read(buf);
	}

	/**
	 * 轮询串口设备，检查是否有数据输入
	 * 
	 * @param fd
	 *            对应串口的文件描述符
	 * @return 是否有数据输入
	 * @throws IOException
	 * 
	 * @author MC
	 */
	public boolean select(FileDescriptor fd) throws IOException
	{
		openFileStream(fd);

		return in.available() > 0 ? true : false;
	}

	/**
	 * 关闭串口设备
	 * 
	 * @param fd
	 *            对应的串口描述符
	 * 
	 * @author MC
	 */
	public void close(FileDescriptor fd)
	{
		closeFileStream();
		close();
	}

	// ===============================END============================

	// =========================JNI函数申明==============================
	private native FileDescriptor open(String path, int baudrate, int check);

	private native void close();

	// ==============================END============================
	static
	{
		System.loadLibrary("serial_port");
	}
 }