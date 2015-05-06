package com.machineversion.terminal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

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
	final String ip = "115.156.211.22";

	Socket socket;
	Handler handler;

	private boolean udpConnecteSuccess = false;					// UDP连接是否成功

	public NetThread(Handler netHandler)
	{
		handler = netHandler;
	}

	private void receivePic() throws IOException
	{
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();

		NetPacket sendPacket = new NetPacket(NetUtils.MSG_NET_GET_VIDEO, null), revPacket = new NetPacket();

		while (true) // 循环接收相机发来的数据
		{
			// DataPack.sendDataPack(sendPacket, os);

			revPacket = DataPack.recvDataPack(is);
			while (true)
			{
				DataPack.sendDataPack(sendPacket, os);
				revPacket = DataPack.recvDataPack(is);

				if (revPacket != null) // 如果数据正常，表示网络通畅
				{
					Message message = handler.obtainMessage();
					message.obj = revPacket;
					handler.sendMessage(message);
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
				}
			}
		}
	}
	@Override
	public void run()
	{
		boolean flag = false;
		Thread tcpServer = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Log.d("MC", "netConnectint");
					ServerSocket serverSocket = new ServerSocket(NetUtils.port);
					socket = serverSocket.accept();
					udpConnecteSuccess = true;

					Log.d("MC", "netConnected");
					// Message message = handler.obtainMessage();
					// message.what = 0x55;
					// handler.sendMessage(message);

					receivePic();
				} catch (IOException e)
				{
				}
			}
		});

		tcpServer.start();

		try
		{
			UdpServerSocket udpSocket = new UdpServerSocket(
					NetUtils.listenBroadCastPort);
			while (!udpConnecteSuccess)
			{
				if (udpSocket.receive().subSequence(0, 13)
						.equals("Get Server IP"))
				{
					// if (!flag)
					// {
					// flag = true;
					// tcpServer.start();
					// }
					udpSocket.response(ip + "\0", NetUtils.sendIpPort);
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
