package com.machinevision.terminal;

import java.io.InputStream;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.machinevision.net.CmdHandle;
import com.machinevision.net.NetUtils;
import com.machinevision.net.NetUtils.NetPacket;
import com.machinevision.sub_option.SystemSetting_devicePacket.AD9849;
import com.machinevision.sub_option.SystemSetting_devicePacket.Parameters;

public class NetReceiveThread extends Thread
{
	private InputStream is;
	private static Handler handler;
	private NetPacket revPacket = new NetPacket();

	int countQualified, countDisqualified;

	public NetReceiveThread(InputStream is)
	{
		this.is = is;
	}

	static
	{
		System.loadLibrary("picture_process");
	}

	public static Handler getHandler()
	{
		return handler;
	}
	public static void setHandler(Handler handler)
	{
		NetReceiveThread.handler = handler;
	}

	@Override
	public void run()
	{
		while (NetThread.currentState != NetThread.CurrentState.onStop)
		{
			double time1 = System.currentTimeMillis();

			revPacket.recvDataPack(is);
			if (revPacket.type != 0xaa)// 如果数据正常，表示网络通畅
			{
				Log.d("CJ", "minid =" + revPacket.minid);
				switch (revPacket.minid)
				{
				case NetUtils.MSG_NET_GET_VIDEO:
				{
					Bitmap bitmap = null;

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

					int[] image = new int[len];
					// image = pictureProcess(Arrays.copyOfRange(rxBuf, 100,
					// rxBuf.length));

					for (int i = 0; i < len; i++)
					{
						int temp;
						temp = rxBuf[100 + i] & 0xff;
						image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
					}
					Message message = Message.obtain();
					message.what = NetUtils.MSG_NET_GET_VIDEO;
					bitmap.setPixels(image, 0, width, 0, 0, width, height);

					message.obj = bitmap;
					handler.sendMessage(message);

					break;
				}
				case NetUtils.MSG_NET_STATE:
				{
					byte[] temp = Arrays.copyOfRange(revPacket.data,
							revPacket.data.length - 12, revPacket.data.length);
					switch (CmdHandle.getIntFromArray(Arrays.copyOf(temp, 4)))
					{
					case 0x01:
						int tempInteger = CmdHandle.getIntFromArray(Arrays
								.copyOfRange(temp, 4, 8));
						int tempFloat = CmdHandle.getIntFromArray(Arrays
								.copyOfRange(temp, 8, 12));

						Message message = Message.obtain();
						message.what = NetUtils.MSG_NET_STATE;
						message.arg1 = tempInteger;
						message.arg2 = tempFloat;
						handler.sendMessage(message);
					}
					break;
				}
				case NetUtils.MSG_NET_GET_JSON:
				{
					String str = new String(Arrays.copyOfRange(revPacket.data,
							100, revPacket.data.length));

					JsonParser jParser = new JsonParser();
					Gson gson = new Gson();

					Parameters.setInstance(gson.fromJson(jParser.parse(str)
							.getAsJsonObject().toString(), Parameters.class));
					Parameters p = Parameters.getInstance();
					AD9849 ad = p.ad9849;
					ad.pageContents[0] = ad.vga[0] + ad.vga[1] << 8;
					ad.pageContents[1] = ad.shp;
					ad.pageContents[2] = ad.hpl;
					ad.pageContents[3] = ad.rgpl;
					ad.pageContents[4] = ad.pxga[0];
					ad.pageContents[5] = ad.pxga[1];
					ad.pageContents[6] = ad.pxga[2];
					ad.pageContents[7] = ad.pxga[3];
					ad.pageContents[8] = ad.rgdrv;
					ad.pageContents[9] = ad.shd;
					ad.pageContents[10] = ad.hnl;
					ad.pageContents[11] = ad.rgnl;
					ad.pageContents[12] = ad.hxdrv[0];
					ad.pageContents[13] = ad.hxdrv[1];
					ad.pageContents[14] = ad.hxdrv[2];
					ad.pageContents[15] = ad.hxdrv[3];

					break;
				}
				case NetUtils.MSG_NET_RESULT:
				{
					byte[] rxBuf = revPacket.data;

					Message message = Message.obtain();
					message.what = NetUtils.MSG_NET_RESULT;

					message.obj = rxBuf;

					Bundle bundle = new Bundle();
					// 不合格
					if (rxBuf[rxBuf.length - 1] == 0)
					{
						++countDisqualified;
						bundle.putBoolean("result", false);
					}
					else
					{
						++countQualified;
						bundle.putBoolean("result", true);
					}
					bundle.putInt("qualified", countQualified);
					bundle.putInt("disqualified", countDisqualified);
					message.setData(bundle);

					handler.sendMessage(message);

					break;
				}
				default:
					Log.e("MC", "default: minid=" + revPacket.minid);
				}
			}
			else
			// 接收的数据不正常，表示网络故障
			// Close
			{
				Log.e("MC", "packet == null");
			}

		}
	}
	private native int[] pictureProcess(byte[] data);
}
