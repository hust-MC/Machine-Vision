package com.machinevision.inverter;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;



import com.machinevision.serial_jni.SciClass;

import android.util.Log;



public class RecvThread extends Thread {

	private SciModel sModel;
	private SciClass sci;
	private FileDescriptor fd;

	private OnRecvListener mListener;

	int num1 = 0;
	
//	public RecvThread(SciClass s, FileDescriptor f) {
//		// TODO Auto-generated constructor stub
//		sci = s;
//		fd = f;
//	}

	public RecvThread(SciModel sciModel) {
		// TODO Auto-generated constructor stub
		sModel = sciModel;
		sci = sModel.getSci();
		fd = sModel.getFd();
	}

	@Override
	public void run()
	{
		while (sModel.isSciOpened())
		{
			boolean m = false;
			try
			{
				m = sci.select(fd);
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			if (m)
			{
				Log.d("num1", "" + num1);
				num1 = 0;
				try {
					byte[] result = readData(sci);
					if(mListener != null) {
						if (result!= null) {
//							Log.d("receiveMsg", result.toString());
							
							mListener.OnRecv(result);
						}
						else mListener.OnConnError();
					}					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (sModel.isSTOpened() && ++num1 >= 5000) {
				num1 = 0;
				mListener.OnConnError();
			}
		}
	}

	private byte[] readData(SciClass sci) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 1;
        int i = 0;
        while(buffer[len-1] != 0x0D){
        	len = sci.read(buffer);
        	baos.write(buffer, 0, len);
        	if(++i == 1000) {
        		byte[] data = baos.toByteArray();
        		Log.d("receiveErr", new String(data));
        		baos.close();
        		return null;
        	}
        }
        byte[] data = baos.toByteArray();
        baos.close();
		return data;		
	}

	public void open()
	{
		this.start();
	}

	public interface OnRecvListener
	{
		public void OnRecv(byte[] response);
		public void OnConnError();
	}
	
	public void setOnRecvListener(OnRecvListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
	}
}