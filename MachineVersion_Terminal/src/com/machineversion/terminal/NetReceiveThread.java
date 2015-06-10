package com.machineversion.terminal;

import java.io.IOException;
import java.io.InputStream;

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

	public NetReceiveThread(InputStream is)
	{
		this.is = is;
	}
	@Override
	public void run()
	{
		while (NetThread.currentState != NetThread.CurrentState.onStop)
		{
			try
			{
				Log.d("ZY", "rev");
				revPacket.recvDataPack(is);

				switch (revPacket.minid)
				{
				case NetUtils.MSG_NET_GET_VIDEO:
					byte[] rxBuf = revPacket.data;

					if (revPacket.type != 0xaa
							&& revPacket.minid == NetUtils.MSG_NET_GET_VIDEO) // 如果数据正常，表示网络通畅
					{
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

						int[] image = new int[len];
						for (int i = 0; i < len; i++)
						{
							int temp;
							temp = rxBuf[100 + i] & 0xff;
							image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
						}

						if (len <= 0)
						{
							Log.d("MC", "len=" + len);
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
					break;
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	}
}
