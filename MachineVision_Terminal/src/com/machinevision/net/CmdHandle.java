package com.machinevision.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

import com.machinevision.terminal.NetReceiveThread;

import static com.machinevision.net.NetUtils.*;

public class CmdHandle {
	
	private OutputStream os;
	private InputStream is;

	public CmdHandle(Socket socket) throws IOException {
		os = socket.getOutputStream();
		is = socket.getInputStream();
	}

	/**
	 * 向相机端发送获取一张图片的命令，在发送前修改接收handler
	 * 
	 * @param handler
	 *            处理网络数据的handler
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void getVideo(Handler handler) {
		NetPacketContext context = new NetPacketContext(MSG_NET_GET_VIDEO);
		NetReceiveThread.setHandler(handler);
		context.sendPacket(os);
	}

	/**
	 * 发送选择算法命令
	 * 
	 * @param handler
	 *            处理网络数据的handler
	 * @param algorithm
	 *            要选择的算法编号
	 */

	public void normal(int algorithm) {
		NetPacketContext context = new NetPacketContext(MSG_NET_NORMAL);
		context.setData(new byte[] { (byte) algorithm });
		context.sendPacket(os);
	}

	/**
	 * 发送获取相机温度的命令
	 * 
	 * @param handler
	 *            处理接收到的温度信息
	 */
	public void getState(Handler handler) {
		NetPacketContext context = new NetPacketContext(MSG_NET_STATE);
		NetReceiveThread.setHandler(handler);
		context.sendPacket(os);
	}

	public void getParam(Handler handler) {
		NetPacketContext context = new NetPacketContext(MSG_NET_GET_PARAM);
		NetReceiveThread.setHandler(handler);
		context.sendPacket(os);
	}

	public void getJson() {
		NetPacketContext context = new NetPacketContext(MSG_NET_GET_JSON);
		context.sendPacket(os);
	}

	public void setJson(byte[] data) {
		NetPacketContext context = new NetPacketContext(MSG_NET_SET_JSON);
		context.setData(data);
		Log.d("MC", new String(data));
		context.sendPacket(os);
		System.out.println("----send over");

	}

	public void sendBinary(byte[] data) {
		NetPacketContext context = new NetPacketContext(MSG_NET_SEND_BINARY);
		context.setData(data);
		Log.d("MC", new String(data));
		context.sendPacket(os);
	}

	public void generalInfo(byte[] data) {
		NetPacketContext context = new NetPacketContext(MSG_NET_GENERAL);
		context.setData(data);
	}

	public void sendImage(Handler handler, int width, int height, int length,
			byte[] image) {
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
	public static int getIntFromArray(byte[] data) {
		if (data.length != 4) {
			return 0xFFFF;
		} else {
			return data[0] & 0xff | (data[1] << 8) & 0xff00 | (data[2] << 16)
					& 0xff0000 | data[3] << 24;
		}
	}

	private byte[] getArrayFromInt(int... data) {
		int i = -1;
		byte[] result = new byte[data.length * 4];

		while (++i < result.length) {
			result[i] = (byte) ((data[i / 4] >>> ((3 - i % 4) * 8)) & 0xFF);
		}

		return result;
	}
}
