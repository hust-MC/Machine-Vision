package com.machineversion.terminal;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import com.machineversion.serial_jni.SciClass;

import android.os.Handler;
import android.os.Message;

public class SciThread extends Thread implements CommunicationInterface
{
	public static final String SERIAL_PORT = "/dev/ttyO0";
	public static final int SERIAL_BAUD = 9600;

	boolean openFlag;
	private FileDescriptor fd;
	private Handler sciHandler;
	private SciClass sci;

	byte[] buf = new byte[2048];

	public SciThread(Handler handler)
	{
		sciHandler = handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		sci = SciClass.getInstance();
		fd = sci.openSerialPort(new File(SERIAL_PORT), SERIAL_BAUD,
				SciClass.OLD_CHECK);
		if (fd != null)
		{
			openFlag = true;

			while (openFlag)
			{
				boolean m = false;
				try
				{
					m = sci.select(fd);
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
				int n = 0;
				String text = "";
				if (m)
				{
					try
					{
						n = sci.read(buf);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					for (int i = 0; i < n; i++)
					{
						text += (char) buf[i];
					}
					Message message = Message.obtain();
					message.obj = text;
					sciHandler.sendMessage(message);
				}
			}
		}
		else
		// 串口存在，打开fd=null则说明没有执行权限
		{
			Message message = Message.obtain();
			message.what = 0x55;
			sciHandler.sendMessage(message);
		}
	}

	@Override
	public void send(final byte[] data, int cmd)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					sci.write(fd, data);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void open()
	{
		this.start();
	}

	/*
	 * 关闭SCI
	 */
	@Override
	public void close()
	{
		openFlag = false;
		sci.close(fd);
	}

}
