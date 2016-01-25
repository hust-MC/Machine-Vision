package com.machinevision.terminal;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.machinevision.net.CmdHandle;
import com.machinevision.net.NetUtils;
import com.machinevision.net.UdpServerSocket;

import android.os.Handler;
import android.os.Message;
import android.text.GetChars;
import android.util.Log;

/**
 * 网络通信线程
 */
public class NetThread extends Thread implements CommunicationInterface
{	
	public static boolean sendSwitch = false;
	
	public static final int TIMEOUT = 5000;
	public static final int CONNECT_SUCCESS = 100;
	public static final int CONNECT_FAIL = 101;
	private final int RXBUF_SIZE = 300 * 1024;

	public CurrentState currentState = CurrentState.onStop;
	public NetReceiveThread recthread;

	private Lock lock = new ReentrantLock();
	private Condition cond = lock.newCondition();
	private int CameraID;
	
	Socket socket;
	
	UdpServerSocket udpSocket;
	ServerSocket serverSocket;
	
	Handler handler;

	private boolean udpConnecteSuccess = false;					// UDP连接是否成功

	public Socket getSocket()
	{
		return socket;
	}

	public void seRecvHandler(Handler _handler)
	{
		if (recthread != null)
			recthread.setHandler(_handler);
	}
	
	public CurrentState getCurrentState()
	{
		return currentState;
	}
	
	public void setCurrentState(CurrentState _currentState)
	{
		currentState = _currentState;
	}

	public NetThread(Handler netHandler, int CameraNum)
	{
		handler = netHandler;
		CameraID = CameraNum;
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
					Log.d("MC", "netConnecting");
					serverSocket = new ServerSocket(NetUtils.port);
					serverSocket.setSoTimeout(TIMEOUT);
					socket = serverSocket.accept();
					socket.setSoTimeout(TIMEOUT);
					// socket.setReceiveBufferSize(RXBUF_SIZE);
					Message msg = Message.obtain();
					msg.arg1 = CameraID;
					msg.what = CONNECT_SUCCESS;
					handler.sendMessage(msg);
					
					recthread = new NetReceiveThread(socket.getInputStream(), NetThread.this);
					recthread.setName("receive thread");
					recthread.start();		
					recthread.setHandler(handler);
		
					currentState = CurrentState.onReady;				// 转换为发送状态
					udpConnecteSuccess = true;
					udpSocket.close();
					udpSocket = null;
					Log.d("MC", "netConnected");
					currentState = CurrentState.onSending;				
					
					CmdHandle cmdHandle = null;
					while (cmdHandle == null) {
						cmdHandle = MainActivity.getCmdHandle(CameraID);
					}
					
					/*
					 * 以下代码正式开始发送
					 */
					while (currentState != CurrentState.onStop)
					{
						lock.lock();
						if (currentState == CurrentState.onPause)
						{
							Log.d("CJ", "lock");
							cond.await();
							Log.d("CJ", "unlock");
						}
						else if (currentState != CurrentState.onStop)
						{
							currentState = CurrentState.onSending;
							cmdHandle.getState(handler);
							Thread.sleep(5000);
						}
						lock.unlock();
					}
				} 
				catch (Exception e)
				{
					close();
				}
			}	
		}).start();

		try
		{
			udpSocket = new UdpServerSocket(NetUtils.listenBroadCastPort);
			udpSocket.setSoTimeout(TIMEOUT);
			while (!udpConnecteSuccess)
			{
				if (udpSocket.receive().subSequence(0, 13)
						.equals("Get Server IP"))
				{
					udpSocket.response(NetUtils.ip + "\0", NetUtils.sendIpPort);
				}
			}
		} catch (Exception e)
		{
			if (udpSocket != null)
			{
				udpSocket.close();
				udpSocket = null;
			}
			if (!udpConnecteSuccess)
			{				
				Message msg = Message.obtain();
				msg.arg1 = CameraID	;
				msg.what = CONNECT_FAIL;
				handler.sendMessage(msg);
			}
			e.printStackTrace();
		}
	}
	
	@Override
	public void open()
	{
		this.start();
	}

	@Override
	public void send(byte[] data, int cmd)
	{
		OutputStream os1;		
		try
		{
			os1 = socket.getOutputStream();
//			ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			DataOutputStream dos1 = new DataOutputStream(os1);
			dos1.write(data);
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
			recthread = null;
		} catch (IOException e)
		{
			Log.e("MC", "closeNet" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static enum CurrentState
	{
		onReady, onSending, onPause, onStop
	}
}
