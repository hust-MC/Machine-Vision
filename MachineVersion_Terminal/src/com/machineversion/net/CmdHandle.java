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

import com.machineversion.net.NetFactoryClass.GetVideoFactory;
import com.machineversion.net.NetUtils.NetPacket;

public class CmdHandle
{
	Socket socket;

	public CmdHandle(Socket socket)
	{
		this.socket = socket;
	}

	public void getVideo(Handler handler) throws IOException,
			InterruptedException
	{
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();

		NetPacket sendPacket = new GetVideoFactory().CreatePacket(), revPacket = new NetPacket();

		revPacket.recvDataPack(is);

		/*
		 * 处理图像数据
		 */
		while (true)
		{
			sendPacket.send(os);
			revPacket.recvDataPack(is);
			Log.d("MC", "rev");
			if (revPacket.type != 0xaa) // 如果数据正常，表示网络通畅
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
}
