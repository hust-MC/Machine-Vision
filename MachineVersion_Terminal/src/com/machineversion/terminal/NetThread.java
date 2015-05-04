package com.machineversion.terminal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.machineversion.net.DataPack;
import com.machineversion.net.NetUtils;
import com.machineversion.net.NetUtils.NetPacket;
import com.machineversion.net.UdpServerSocket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetThread extends Thread implements CommunicationInterface
{
	final int timeout = 5000;
	final String ip = "192.168.137.251";

	Socket socket;
	Handler handler;

	private boolean udpConnecteSuccess = false;					// UDP连接是否成功

	public NetThread(Handler netHandler)
	{
		if (ip.isEmpty() || (NetUtils.port <= 0 || NetUtils.port >= 65536))
		{
			Log.d("MC", "PORT error");
		}
		handler = netHandler;
	}

	private void receivePic()
	{
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					InputStream is = socket.getInputStream();
					while (true) // 循环接收相机发来的数据
					{
						NetPacket netPacket = DataPack.recvDataPack(is);

						if (netPacket != null) // 如果数据正常，表示网络通畅
						{

						}
						else
						// 接收的数据不正常，表示网络故障
						// Close
						{
							try
							{
								if (socket != null)
								{
									socket.close();
									socket = null;
								}
							} catch (Exception e)
							{
								Log.d("MC", "break");
							}
							break;
						}
					}
				} catch (Exception e)
				{
				} finally
				{
					Message message = Message.obtain();
					message.obj = null;
					handler.sendMessage(message);
				}
			}
		};

		if (MainActivity.netFlag)							// netFlag为true表示连接成功，启动进程接收图片
		{
			Thread t = new Thread(r);
			t.start();
		}
		else
		// netFlag为false表示连接失败，通知主线程显示失败信息提示
		{
			Message message = Message.obtain();
			message.what = 0x55;	    		// what 为0x55表示连接失败
			handler.sendMessage(message);
		}
	}

	@Override
	public void run()
	{

		// new Thread(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// try
		// {
		// Log.d("MC", "netConnectint");
		// ServerSocket serverSocket = new ServerSocket(NetUtils.port);
		// socket = serverSocket.accept();
		// udpConnecteSuccess = true;
		//
		// Log.d("MC", "netConnected");
		// Message message = handler.obtainMessage();
		// message.what = 0x55;
		// message.setTarget(handler);
		//
		// receivePic();
		// } catch (IOException e)
		// {
		// }
		// }
		// }).start();

		try
		{
			UdpServerSocket udpSocket = new UdpServerSocket(
					NetUtils.listenBroadCastPort);
			while (!udpConnecteSuccess)
			{
				Log.d("MC", udpSocket.receive());
				if (udpSocket.receive() != "Get Server IP")
				{
					udpSocket.response(InetAddress.getLocalHost().getAddress()
							.toString(), NetUtils.sendIpPort);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void open()
	{
		this.start();
	}

	@Override
	public void send(byte[] data, int cmd)
	{
		OutputStream os;
		try
		{
			os = socket.getOutputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.write(data);
			// DataPack.sendDataPack(baos.toByteArray(), os);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{

	}
}
