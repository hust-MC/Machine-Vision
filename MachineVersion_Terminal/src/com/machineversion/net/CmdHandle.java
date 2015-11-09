package com.machineversion.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.machineversion.terminal.NetReceiveThread;

import static com.machineversion.net.NetUtils.*;

public class CmdHandle
{
	private static CmdHandle cmdHandle;
	private OutputStream os;
	private InputStream is;

	private CmdHandle(Socket socket) throws IOException
	{
		os = socket.getOutputStream();
		is = socket.getInputStream();
	}

	public static CmdHandle getInstance()
	{
		return cmdHandle;
	}

	public synchronized static CmdHandle getInstance(Socket socket)
			throws IOException
	{
		if (cmdHandle == null)
		{
			cmdHandle = new CmdHandle(socket);
		}
		return cmdHandle;
	}

	public static void clear()
	{
		cmdHandle = null;
	}

	/**
	 * 向相机端发送获取一张图片的命令，在发送前修改接收handler
	 * 
	 * @param handler
	 *            处理网络数据的handler
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void getVideo(Handler handler) throws IOException,
			InterruptedException
	{
		NetPacketContext context = new NetPacketContext(MSG_NET_GET_VIDEO);

		NetReceiveThread.handler = handler;
		context.sendPacket(os);
		Log.d("send", "send over");
	}

	/**
	 * 发送选择算法命令
	 * 
	 * @param handler
	 *            处理网络数据的handler
	 * @param algorithm
	 *            要选择的算法编号
	 */
	public void normal(int algorithm)
	{
		NetPacketContext context = new NetPacketContext(MSG_NET_NORMAL);
		context.setData(new byte[]
		{ (byte) algorithm });
		context.sendPacket(os);
	}

	/**
	 * 发送获取相机温度的命令
	 * 
	 * @param handler
	 *            处理接收到的温度信息
	 */
	public void getState(Handler handler)
	{
		int tempInteger = 0, tempFloat = 0;

		NetPacketContext context = new NetPacketContext(MSG_NET_STATE);

		NetPacket revPacket = new NetPacket();
		context.sendPacket(os);
		revPacket.recvDataPack(is);

		if (revPacket.type != 0xaa && revPacket.minid == NetUtils.MSG_NET_STATE)
		{
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
		else
		{
			Log.d("MC", "packet == null");
		}
	}

	public void generalInfo(byte[] data)
	{
		NetPacketContext context = new NetPacketContext(MSG_NET_GENERAL);

		context.setData(data);
	}

	public void sendImage(Handler handler, int width, int height, int length,
			byte[] image)
	{
		NetPacketContext context = new NetPacketContext(MSG_NET_SEND_IMAGE);

		NetPacket revNetPacket = new NetPacket();

		context.setData(getArrayFromInt(width, height, length));
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
