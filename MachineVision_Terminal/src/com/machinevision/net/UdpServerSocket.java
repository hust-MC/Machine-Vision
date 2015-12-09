package com.machinevision.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.util.Log;

public class UdpServerSocket
{
	private byte[] buffer = new byte[1024];

	private DatagramSocket ds = null;

	private DatagramPacket packet = null;

	private InetSocketAddress socketAddress = null;

	private String orgIp;

	/**
	 * 构造函数，绑定主机和端口.
	 * 
	 * @param host
	 *            主机
	 * @param port
	 *            端口
	 * @throws Exception
	 */
	public UdpServerSocket(int port) throws Exception
	{
		ds = new DatagramSocket(port);
		System.out.println("服务端启动!");
	}

	public final String getOrgIp()
	{
		return orgIp;
	}

	/**
	 * 设置超时时间，该方法必须在bind方法之后使用.
	 * 
	 * @param timeout
	 *            超时时间
	 * @throws Exception
	 */
	public final void setSoTimeout(int timeout) throws Exception
	{
		ds.setSoTimeout(timeout);
	}

	/**
	 * 获得超时时间.
	 * 
	 * @return 返回超时时间.
	 * @throws Exception
	 */
	public final int getSoTimeout() throws Exception
	{
		return ds.getSoTimeout();
	}

	/**
	 * 绑定监听地址和端口.
	 * 
	 * @param host
	 *            主机IP
	 * @param port
	 *            端口
	 * @throws SocketException
	 */
	public final void bind(String host, int port) throws SocketException
	{
		socketAddress = new InetSocketAddress(host, port);
		ds = new DatagramSocket(socketAddress);
	}

	/**
	 * 接收数据包，该方法会造成线程阻塞.
	 * 
	 * @return 返回接收的数据串信息
	 * @throws IOException
	 */
	public final String receive()
	{
		packet = new DatagramPacket(buffer, buffer.length);
		try
		{
			ds.receive(packet);
		} catch (IOException e)
		{
			Log.d("MC", "udp exception");
			e.printStackTrace();
		}
		orgIp = packet.getAddress().getHostAddress();
		String info = new String(packet.getData(), 0, packet.getLength());
		return info;
	}

	/**
	 * 将响应包发送给请求端.
	 * 
	 * @param bytes
	 *            回应报文
	 * @throws IOException
	 */
	public final void response(String info) throws IOException
	{
		System.out.println("客户端地址 : " + packet.getAddress().getHostAddress()
				+ ",端口：" + packet.getPort());
		DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length,
				packet.getSocketAddress());
		dPacket.setData(info.getBytes());
		ds.send(dPacket);
	}

	public final void response(String info, int port) throws IOException
	{
		DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length,
				packet.getAddress(), port);
		dPacket.setData(info.getBytes());
		ds.send(dPacket);
	}

	/**
	 * 设置报文的缓冲长度.
	 * 
	 * @param bufsize
	 *            缓冲长度
	 */
	public final void setLength(int bufsize)
	{
		packet.setLength(bufsize);
	}

	/**
	 * 获得发送回应的IP地址.
	 * 
	 * @return 返回回应的IP地址
	 */
	public final InetAddress getResponseAddress()
	{
		return packet.getAddress();
	}

	/**
	 * 获得回应的主机的端口.
	 * 
	 * @return 返回回应的主机的端口.
	 */
	public final int getResponsePort()
	{
		return packet.getPort();
	}

	/**
	 * 关闭udp监听口.
	 */
	public final void close()
	{
		try
		{
			ds.close();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}