package com.machineversion.camera;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/*
 * 用于处理相机进程的服务
 */
public class CameraServerService extends Service
{
	CameraServer mCs = null;
	WakeLock mWakeLock = null;

	Thread mUdpBroadCastThread = null;
	boolean StillbroadCast = false;

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		final int port = intent.getIntExtra("PORT_VALUE", 0);

		mCs = new CameraServer();
		if (mCs.Listening(port))
		{
			mCs.start();
		}

		acquireWakeLock();
		super.onStartCommand(intent, flags, startId);
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy()
	{
		releaseWakeLock();
		mCs.onDestroy();
		StillbroadCast = false;
		try
		{
			mCs.join();
			mUdpBroadCastThread.join();
		} catch (Exception e)
		{
		}
	}

	private void acquireWakeLock()
	{

		if (null == mWakeLock)
		{
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "LOCK");
			if (null != mWakeLock)
			{
				mWakeLock.acquire();
			}
		}
	}

	private void releaseWakeLock()
	{
		if (null != mWakeLock)
		{
			mWakeLock.release();
			mWakeLock = null;
		}
	}

}
