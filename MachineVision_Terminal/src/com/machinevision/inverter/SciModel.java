package com.machinevision.inverter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import com.machinevision.inverter.RecvThread.OnRecvListener;
import com.machinevision.serial_jni.SciClass;
import com.machinevision.terminal.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SciModel
{
	public static final int Data_ACK = 0;
	public static final int Data_NAK = 1;

	public static final int Current_Refresh = 2;
	public static final int OutFrq_Refresh = 3;
	public static final int Status_Refresh = 4;

//	public static final int Frq_Ram = 5;
	public static final int Frq_Prom = 6;

	public static final int Alarm = 7;
	public static final int Conn_Error = 8;
	public static final int Get_Alarm = 9;

	public static final String SERIAL_PORT = "/dev/ttyO2";
	public static final int SERIAL_BAUD = 9600;

	private SciClass sci;
	private FileDescriptor fd;

	private RecvThread sciListener;
	private SendThread connectThread;

	private int outFrq = 0;

	//串口开启标志
	private boolean sciOpened = false;
	
	//监控线程开启标志
	private boolean sThreadOpened = false;
	
	//单例模式
	private static SciModel instance = null;
	private static Context mContext;
	private static Handler mHandler;
	
	public void setHandler(Handler h) {
		mHandler = h;
	}
	
    public static SciModel getInstance(Context context){
        if(instance == null){
            instance = new SciModel();
        }
        mContext = context;
        return instance;
    }
    
	public SciModel() {
	}

/****************串口动作**********************/
	//串口开启，线程开启
	public void openSci() {
		// TODO Auto-generated method stub
		if (new File(SERIAL_PORT).exists())
		{			
			//获取串口实例
			sci = SciClass.getInstance();
			fd = sci.openSerialPort(new File(SERIAL_PORT), SERIAL_BAUD,
					SciClass.OLD_CHECK);
			if(fd != null) {
				//接收线程开启
				sciListener = new RecvThread(this);
				sciListener.setOnRecvListener(new OnRecvListener() {						
					@Override
					public void OnRecv(byte[] response) {
						// TODO Auto-generated method stub
//							Log.d("receiveMsg", new String(response));
						Log.v("TimeCheck", new String(response));
						parserResponse(response);
					}

					@Override
					public void OnConnError() {
						// TODO Auto-generated method stub
						Log.d("Terminal", "conn_error");
						Message msg = Message.obtain();
						msg.what = Conn_Error;
						mHandler.sendMessage(msg);
					}
				});
				
				//串口开启标志设置
				setSciOpened(true);
				Toast.makeText(mContext, mContext.getResources().getString(R.string.openSCI_sucssess),
						Toast.LENGTH_SHORT).show();	
				
				//接收线程打开
				sciListener.open();
			}
			else// 串口存在，打开fd=null则说明没有执行权限
			{
				Toast.makeText(mContext, "串口没有执行权限", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(mContext, "本地串口不存在", Toast.LENGTH_SHORT).show();
		}
	}

	//串口关闭
	public void closeSci() {
		// TODO Auto-generated method stub
		if(isSciOpened()) {						
			sci.close(fd);
			setSciOpened(false);
			setSTOpened(false);
			Toast.makeText(mContext, "串口关闭",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	//状态监控线程sendThread开启
	public void sThreadOpen() {
		connectThread = new SendThread(this);
		connectThread.open();
		setSTOpened(true);
	}
	
	//串口发送
	public void send(byte[] data) {
		sciWrite(data);		
	}

	private void sciWrite(final byte[] data)
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

/****************接收数据解析动作**********************/
	//解析获取数据
	private void parserResponse(byte[] response) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		switch(response[0]) {
		//数据代码正确
		case RecvUtils.ACK:
			msg.what = Data_ACK;
			mHandler.sendMessage(msg);
			break;
		//数据代码错误
		case RecvUtils.NAK:
			String daErr = RecvUtils.getDataErr(response[3]);
			msg.what = Data_NAK;
			msg.obj = daErr;
			mHandler.sendMessage(msg);
			break;
		case RecvUtils.STX:
			//MainActivity的数据处理
			processData(connectThread.getSendNum(), response);
			break;	
		}
	}

	//收到为返回数据，获得数据
		private void processData(int i, byte[] response) {
			// TODO Auto-generated method stub
			Message msg = Message.obtain();
			int sData;
			switch(i) {
			case SendUtils.numStatus:
				sData = RecvUtils.getData(response);
				if(sData >= 128) {
					msg.what = SciModel.Alarm;
					mHandler.sendMessage(msg);
				} else {
					msg.what = Status_Refresh;
					mHandler.sendMessage(msg);
				}
				break;
			case SendUtils.numOutFrq:
				sData = RecvUtils.getData(response);
				outFrq = sData/100;
				msg.what = OutFrq_Refresh;
				msg.arg1 = sData;
				mHandler.sendMessage(msg);
				break;
			case SendUtils.numCurrent:
				sData = RecvUtils.getData(response);
				msg.what = Current_Refresh;
				msg.arg1 = sData;
				mHandler.sendMessage(msg);
				break;
			case SendUtils.numGetProm:
				sData = RecvUtils.getData(response);
				msg.what = Frq_Prom;
				msg.arg1 = sData;
				mHandler.sendMessage(msg);
				break;
			case SendUtils.numGetAlarm:
				int alarmCode = RecvUtils.getData(response);
				msg.what = Get_Alarm;
				msg.arg1 = alarmCode;
				mHandler.sendMessage(msg);
				break;
			}
		}

/************频率设置*************/
	public void setFrqRam(int frq) {
		// TODO Auto-generated method stub
		byte[] num = getNumBytes(frq * 100);
		for(int i = 0; i < num.length; i++) {
			SendUtils.setFrqRam[6 + i] = num[i];
		}
		SendUtils.getCheckSum(SendUtils.setFrqRam);
		send(SendUtils.setFrqRam);
	}

	public void setFrqProm(int frq) {
		// TODO Auto-generated method stub
		byte[] num = getNumBytes(frq * 100);
		for(int i = 0; i < num.length; i++) {
			SendUtils.setFrqProm[6 + i] = num[i];
		}
		SendUtils.getCheckSum(SendUtils.setFrqProm);
		send(SendUtils.setFrqProm);
	}
	
	public void frqUp() {
		if(outFrq < 50) {
			outFrq++;
			setFrqRam(outFrq);
		}
		else Toast.makeText(mContext, "已到限制频率", Toast.LENGTH_SHORT).show();
	}
	
	public void frqDown() {
		if(outFrq > 0) {
			outFrq--;
			setFrqRam(outFrq);
		}
		else Toast.makeText(mContext, "已到限制频率", Toast.LENGTH_SHORT).show();
	}
	
	//发送数据的4个数据位
	private byte[] getNumBytes(int frq) {
		String s = Integer.toHexString(frq).toUpperCase();
		String st = String.format("%4s", s).replace(' ', '0');
		return st.getBytes();
	}

/*****************串口开启状态获取与设置*********************/	
	public boolean isSciOpened() {
		return sciOpened;
	}
	
	public void setSciOpened(boolean b) {
		sciOpened = b;
	}

	public boolean isSTOpened() {
		return sThreadOpened;
	}
	
	public void setSTOpened(boolean b) {
		sThreadOpened = b;
	}
	
	public SciClass getSci() {
		return sci;
	}

	public FileDescriptor getFd() {
		return fd;
	}
}