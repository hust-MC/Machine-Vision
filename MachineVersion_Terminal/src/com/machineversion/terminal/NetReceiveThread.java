package com.machineversion.terminal;

import java.io.InputStream;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.machineversion.net.NetUtils;
import com.machineversion.net.NetUtils.NetPacket;

public class NetReceiveThread extends Thread
{
	private InputStream is;
	public static Handler handler;
	private NetPacket revPacket = new NetPacket();
	private Bitmap bitmap;

	public NetReceiveThread(InputStream is)
	{
		this.is = is;
	}

	static
	{
		System.loadLibrary("picture_process");
	}

	@Override
	public void run()
	{
		while (NetThread.currentState != NetThread.CurrentState.onStop)
		{
			Log.d("ZY", "rev");
			long timer1 = System.currentTimeMillis();
			revPacket.recvDataPack(is);
			if (revPacket.type != 0xaa)// 如果数据正常，表示网络通畅
			{
				switch (revPacket.minid)
				{
				case NetUtils.MSG_NET_GET_VIDEO:

					byte[] rxBuf = revPacket.data;

					int[] data = new int[12];
					for (int i = 0; i < 12; i++)
					{
						data[i] = rxBuf[i] & 0xFF;
					}

					int len = data[0] | data[1] << 8 | data[2] << 16
							| data[3] << 24;
					int width = data[4] | data[5] << 8 | data[6] << 16
							| data[7] << 24;
					int height = data[8] | data[9] << 8 | data[10] << 16
							| data[11] << 24;

					if (bitmap == null || bitmap.getWidth() != width
							|| bitmap.getHeight() != height)
					{
						bitmap = Bitmap.createBitmap(width, height,
								Config.ARGB_8888);
					}

					long timer2 = System.currentTimeMillis();
					int[] image = new int[len];

					// image = pictureProcess(Arrays.copyOfRange(rxBuf, 100,
					// rxBuf.length));

					for (int i = 0; i < len; i++)
					{
						int temp;
						temp = rxBuf[100 + i] & 0xff;
						image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
					}
					long timer3 = System.currentTimeMillis();
					Message message = Message.obtain();
					message.what = NetUtils.MSG_NET_GET_VIDEO;
					bitmap.setPixels(image, 0, width, 0, 0, width, height);

					message.obj = bitmap;
					handler.sendMessage(message);
					long timer4 = System.currentTimeMillis();

					Log.d("MC", "whole = " + (timer4 - timer1));
					Log.d("MC", "rev = " + (timer2 - timer1));
					Log.d("MC", "byteToint = " + (timer3 - timer2));
					Log.d("MC", "create BitMap = " + (timer4 - timer3));

					break;
				default:
					Log.e("MC", "default");
				}
			}
			else
			// 接收的数据不正常，表示网络故障
			// Close
			{
				Log.d("MC", "packet == null");
			}

		}
	}

	private native int[] pictureProcess(byte[] data);
}
