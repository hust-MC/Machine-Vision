package com.machineversion.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.machineversion.net.NetFactoryClass.*;
import com.machineversion.net.NetUtils.NetPacket;
import com.machineversion.net.NetUtils.Normal;

public class CmdHandle
{
	private static CmdHandle cmdHandle;

	private Socket socket;
	private OutputStream os;
	private InputStream is;

	private CmdHandle(Socket socket) throws IOException
	{
		this.socket = socket;
		os = socket.getOutputStream();
		os = socket.getOutputStream();
	}

	public static CmdHandle getInstance()
	{
		return cmdHandle;
	}

	public static CmdHandle getInstance(Socket socket) throws IOException
	{
		if (cmdHandle == null)
		{
			cmdHandle = new CmdHandle(socket);
		}
		return cmdHandle;
	}
	public void getVideo(Handler handler) throws IOException,
			InterruptedException
	{
		NetPacket sendPacket = new GetVideoFactory().CreatePacket(), revPacket = new NetPacket();

		revPacket.recvDataPack(is);

		/*
		 * 处理图像数据
		 */
		sendPacket.send(os);
		revPacket.recvDataPack(is);
		if (revPacket.type != 0xaa) // 如果数据正常，表示网络通畅
		{
			int[] data = new int[12];
			for (int i = 0; i < 12; i++)
			{
				data[i] = revPacket.data[i] & 0xFF;
			}
			int len = data[0] | data[1] << 8 | data[2] << 16 | data[3] << 24;
			int width = data[4] | data[5] << 8 | data[6] << 16 | data[7] << 24;
			int height = data[8] | data[9] << 8 | data[10] << 16
					| data[11] << 24;
			byte[] imageBuf = Arrays
					.copyOfRange(revPacket.data, 100, len + 100);

			int[] image = new int[imageBuf.length];
			for (int i = 0; i < image.length; i++)
			{
				int temp;
				temp = imageBuf[i] & 0xff;
				image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
			}

			Message message = Message.obtain();
			message.what = NetUtils.MSG_NET_GET_VIDEO;
			message.obj = Bitmap.createBitmap(image, width, height,
					Config.RGB_565);
			handler.sendMessage(message);
		}
		else
		// 接收的数据不正常，表示网络故障
		// Close
		{
			Log.d("MC", "packet == null");
		}
	}

	public void normal(Handler handler, int algorithm)
	{
		NetPacket sendPacket = new GetNormalFactory().CreatePacket();
		sendPacket.setData(new byte[]
		{ (byte) algorithm });
		sendPacket.send(os);
	}

	public void getState(Handler handler) throws IOException
	{
		int tempInteger = 0, tempFloat = 0;

		NetPacket sendPacket = new GetStateFactory().CreatePacket(), revPacket = new NetPacket();

		sendPacket.send(os);
		revPacket.recvDataPack(is);

		byte[] data = Arrays.copyOfRange(revPacket.data,
				revPacket.data.length - 12, revPacket.data.length);
		switch (getIntFromArray(Arrays.copyOf(data, 4)))
		{
		case 0x01:
			tempInteger = getIntFromArray(Arrays.copyOfRange(data, 4, 8));
			tempFloat = getIntFromArray(Arrays.copyOfRange(data, 8, 12));

			Message message = Message.obtain();
			message.what = NetUtils.MSG_NET_STATE;
			message.arg1 = tempInteger;
			message.arg2 = tempFloat;
			handler.sendMessage(message);
		}
	}

	public void sendImage(Handler handler, int width, int height, int length,
			byte[] image)
	{
		NetPacket sendPacket = new GetSendImageFactory().CreatePacket(), revNetPacket = new NetPacket();
		sendPacket.setData(getArrayFromInt(width, height, length));
	}

	/**
	 * 从字节数组中获取整形数据
	 * 
	 * @param data
	 *            大小为4的字节数组
	 * @return 又四个字节组成的整形数
	 */
	private int getIntFromArray(byte[] data)
	{
		if (data.length != 4)
		{
			return 0xFFFF;
		}
		else
		{
			return data[0] & 0xff | (data[1] << 8) & 0xff00 | (data[2] << 16)
					& 0xff0000 | data[3] << 24;
		}
	}
	private byte[] getArrayFromInt(int... data)
	{
		int i = -1;
		byte[] result = new byte[data.length * 4];

		while (++i < result.length)
		{
			result[i] = (byte) ((data[i / 4] >>> ((3 - i % 4) * 8)) & 0xFF);
		}

		return result;
	}
}
