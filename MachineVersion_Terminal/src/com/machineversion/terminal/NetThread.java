package com.machineversion.terminal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.machineversion.net.CmdHandle;
import com.machineversion.net.NetUtils;
import com.machineversion.net.UdpServerSocket;

import android.os.Handler;
import android.util.Log;

/**
 * 网络通信线程
 */
public class NetThread extends Thread implements CommunicationInterface
{
	public static boolean sendSwitch = false;

	public static final int CONNECT_SUCCESS = 100;
	public static final int CONNECT_FAIL = 101;
	private final int RXBUF_SIZE = 300 * 1024;

	public static CurrentState currentState = CurrentState.onStop;
	private Lock lock = new ReentrantLock();
	private Condition cond = lock.newCondition();

	Socket socket;
	UdpServerSocket udpSocket;
	ServerSocket serverSocket;
	Handler handler;

	private boolean udpConnecteSuccess = false;					// UDP连接是否成功

	public CurrentState getCurrentState()
	{
		return currentState;
	}
	public void setCurrentState(CurrentState currentState)
	{
		NetThread.currentState = currentState;
	}

	public NetThread(Handler netHandler)
	{
		handler = netHandler;
	}

	public void signalThread()
	{
		lock.lock();
		cond.signalAll();
		currentState = CurrentState.onReady;
		lock.unlock();
	}
	@Override
	public void run()
	{
		/*
		 * 开启TCP服务器，等待UDP连接成功。
		 */
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Log.d("MC", "netConnectint");
					serverSocket = new ServerSocket(NetUtils.port);
					socket = serverSocket.accept();
					// socket.setSoTimeout(TIMEOUT);
					// socket.setReceiveBufferSize(RXBUF_SIZE);
					udpConnecteSuccess = true;
					udpSocket.close();

					Log.d("MC", "netConnected");
					handler.sendEmptyMessage(CONNECT_SUCCESS);

					currentState = CurrentState.onReady;				// 转换为发送状态

					new NetReceiveThread(socket.getInputStream()).start();

					CmdHandle cmdHandle = CmdHandle.getInstance(socket);
					currentState = CurrentState.onSending;

					/*
					 * 以下代码正式开始发送
					 */

					while (currentState != CurrentState.onStop)
					{
						lock.lock();
						if (currentState == CurrentState.onPause)
						{
							Log.d("ZY", "lock");
							cond.await();
							Log.d("ZY", "unlock");
						}
						else
						{

							currentState = CurrentState.onSending;
							cmdHandle.getParam(handler);

							// cmdHandle.getVideo(handler);

							// cmdHandle.getState(handler);
						}
						lock.unlock();
					}

					CmdHandle.clear();									// 清空单例cmdhandle，便于之后重新生成
				} catch (IOException e)
				{
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				Log.d("MC", "tcp thread stop");
			}
		}).start();

		try
		{
			udpSocket = new UdpServerSocket(NetUtils.listenBroadCastPort);
			udpSocket.setSoTimeout(5000);
			while (!udpConnecteSuccess)
			{
				if (udpSocket.receive().subSequence(0, 13)
						.equals("Get Server IP"))
				{
					Log.d("MC", "send udp");
					udpSocket.response(NetUtils.ip + "\0", NetUtils.sendIpPort);
				}
			}

		} catch (Exception e)
		{
			udpSocket.close();
			handler.sendEmptyMessage(CONNECT_FAIL);
			Log.d("MC", "udp close");
			e.printStackTrace();
		}
		Log.d("MC", "stop UDP thread");
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
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{
		setCurrentState(CurrentState.onStop);
		try
		{
			if (serverSocket != null)
			{
				serverSocket.close();
				serverSocket = null;
			}
			if (socket != null)
			{
				socket.close();
				socket = null;
			}
			if (udpSocket != null)
			{
				udpSocket.close();
				udpSocket = null;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static enum CurrentState
	{
		onReady, onSending, onPause, onStop

	}
}
