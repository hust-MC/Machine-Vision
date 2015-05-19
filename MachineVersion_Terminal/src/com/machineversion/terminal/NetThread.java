package com.machineversion.terminal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import com.machineversion.net.CmdHandle;
import com.machineversion.net.NetUtils;
import com.machineversion.net.UdpServerSocket;
import com.machineversion.net.NetFactoryClass.GetVideoFactory;
import com.machineversion.net.NetUtils.NetPacket;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetThread extends Thread implements CommunicationInterface
{
	public static boolean sendSwitch = false;
	private final int TIMEOUT = 7000;
	private final int RXBUF_SIZE = 300 * 1024;

	Socket socket;
	UdpServerSocket udpSocket;
	ServerSocket serverSocket;
	Handler handler;

	private boolean udpConnecteSuccess = false;					// UDP连接是否成功

	public NetThread(Handler netHandler)
	{
		handler = netHandler;
	}

	@Override
	public void run()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Log.d("MC", "netConnectint");
					serverSocket = new ServerSocket(NetUtils.port);
					socket = serverSocket.accept();
					socket.setSoTimeout(TIMEOUT);
					socket.setReceiveBufferSize(RXBUF_SIZE);
					udpConnecteSuccess = true;
					udpSocket.close();

					Log.d("MC", "netConnected");
					Message message = Message.obtain();
					message.what = 0x55;				// 返回0x55说明连接成功
					handler.sendMessage(message);

					CmdHandle cmdHandle = CmdHandle.getInstance(socket);
					new NetPacket().recvDataPack(socket.getInputStream());
					while (true)
					{
						cmdHandle.getVideo(handler);
						cmdHandle.getState(handler);
					}
				} catch (IOException e)
				{
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				Log.d("MC", "tcp thread stop");
			}
		}).start();

		try
		{
			udpSocket = new UdpServerSocket(NetUtils.listenBroadCastPort);
			while (!udpConnecteSuccess)
			{
				if (udpSocket.receive().subSequence(0, 13)
						.equals("Get Server IP"))
				{
					Log.d("MC", "send udp");
					udpSocket.response(NetUtils.ip + "\0", NetUtils.sendIpPort);
				}

			}
		} catch (Exception e)
		{
			Log.d("MC", "udp close");
			e.printStackTrace();
		}
		Log.d("MC", "stop UDP thread");
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
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{
		try
		{
			if (serverSocket != null)
			{
				serverSocket.close();
				serverSocket = null;
			}
			if (socket != null)
			{
				socket.close();
				socket = null;
			}
			if (udpSocket != null)
			{
				udpSocket.close();
				udpSocket = null;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
