package com.machinevision.inverter;


import android.util.Log;

public class SendThread extends Thread {		
	
	private boolean sendFlag = false;
	
	private int sendNum;
	
	private int paraNum;
	private SciModel sModel;
	
	public SendThread(SciModel sci) {
		sModel = sci;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(sModel.isSciOpened()) {
			while(sendFlag) {
				if(!sModel.getBtnClicked()) {
					if(++sendNum > 4)
						sendNum = 1;
					switch(sendNum) {
					case SendUtils.numStatus:
						sModel.send(SendUtils.getStatus);					
						break;
					case SendUtils.numOutFrq:
						sModel.send(SendUtils.getOutFrq);
						break;
					case SendUtils.numCurrent:
						sModel.send(SendUtils.getCurrent);	
						break;
					case SendUtils.numGetProm:
						sModel.send(SendUtils.getFrqProm);
						break;
					}
					Log.v("TimeCheck", "send");
				}
				else sModel.btnAction();
				
			}
		}
	}

	public void open() {
		sendNum = 0;
		sendFlag = true;
		this.start();
	}
	
	public void setSendFlag(boolean b) {
		sendFlag = b;
	}
	
	public boolean getSendFlag() {
		return sendFlag;
	}

	public int getSendNum() {
		return sendNum;
	}
	

//	public void setSendNum(int sendNum) {
//		this.sendNum = sendNum;
//	}
}
