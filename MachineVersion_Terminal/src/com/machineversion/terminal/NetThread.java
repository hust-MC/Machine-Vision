package com.machineversion.terminal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import com.machineversion.net.DataPack;
import com.machineversion.net.NetUtils;
import com.machineversion.net.NetUtils.NetPacket;
import com.machineversion.net.UdpServerSocket;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetThread extends Thread implements CommunicationInterface
{
	public static boolean sendSwitch = false;
	final int timeout = 5000;

	Socket socket;
	UdpServerSocket udpSocket;
	Handler handler;

	private boolean udpConnecteSuccess = false;					// UDP连接是否成功

	public NetThread(Handler netHandler)
	{
		handler = netHandler;
	}

	private void receivePic() throws IOException, InterruptedException
	{
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();

		NetPacket sendPacket = new NetPacket(NetUtils.MSG_NET_GET_VIDEO, null), revPacket = new NetPacket();

		revPacket = DataPack.recvDataPack(is);

		/*
		 * 处理
		 */
		while (true)
		{
			DataPack.sendDataPack(sendPacket, os);
			revPacket = DataPack.recvDataPack(is);
			Log.d("MC", "rev");
			if (revPacket != null) // 如果数据正常，表示网络通畅
			{
				int[] data = new int[12];
				for (int i = 0; i < 12; i++)
				{
					data[i] = revPacket.data[i] & 0xFF;
				}

				int len = data[0] | data[1] << 8 | data[2] << 16
						| data[3] << 24;
				int width = data[4] | data[5] << 8 | data[6] << 16
						| data[7] << 24;
				int height = data[8] | data[9] << 8 | data[10] << 16
						| data[11] << 24;
				byte[] imageBuf = Arrays.copyOfRange(revPacket.data, 100,
						len + 100);

				int[] image = new int[imageBuf.length];
				for (int i = 0; i < image.length; i++)
				{
					int temp;
					temp = imageBuf[i] & 0xff;
					image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
				}

				Message message = Message.obtain();
				message.obj = Bitmap.createBitmap(image, width, height,
						Config.RGB_565);
				handler.sendMessage(message);
			}
			else
			// 接收的数据不正常，表示网络故障
			// Close
			{
				Log.d("MC", "packet == null");
				// try
				// {
				// if (socket != null)
				// {
				// socket.close();
				// socket = null;
				// }
				// } catch (Exception e)
				// {
				// Log.d("MC", "break");
				// }
			}
		}
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
					ServerSocket serverSocket = new ServerSocket(NetUtils.port);
					socket = serverSocket.accept();
					udpConnecteSuccess = true;
					udpSocket.close();

					Log.d("MC", "netConnected");
					// Message message = Message.obtain();
					// message.what = 0x55;
					// handler.sendMessage(message);

					receivePic();
				} catch (IOException e)
				{
				} catch (InterruptedException e)
				{
					e.printStackTrace();

				}
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
					udpSocket.response(NetUtils.ip + "\0", NetUtils.sendIpPort);
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
		try
		{
			if (socket != null)
			{
				Log.d("MC", "close");
				socket.close();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
