package com.machineversion.inverter;


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
					if(++sendNum > 5)
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
					case SendUtils.numGetRam:
						sModel.send(SendUtils.getFrqRam);
						break;
					case SendUtils.numGetProm:
						sModel.send(SendUtils.getFrqProm);
						break;
					}
					Log.v("TimeCheck", "send");
				}
				else 
//					switch(btnNum) {
//					case SendUtils.numRun:
//						sModel.send(SendUtils.run);
//						break;
//					case SendUtils.numReverse:
//						sModel.send(SendUtils.reverse);
//						break;
//					case SendUtils.numStop:
//						sModel.send(SendUtils.stop);
//						break;
//					case SendUtils.numGetRam:
//						sModel.send(SendUtils.getFrqRam);
//						break;
//					case SendUtils.numGetProm:
//						sModel.send(SendUtils.getFrqProm);
//						break;
//					case SendUtils.numSetRam:
//						sModel.send(SendUtils.setFrqRam);
//						break;
//					case SendUtils.numSetProm:
//						sModel.send(SendUtils.setFrqProm);
//						break;
//					case SendUtils.numReset:
//						sModel.send(SendUtils.reset);
//						break;
//					case SendUtils.numGetAlarm:
//						sModel.send(SendUtils.getAlamrCode);
//						break;
//					}
//					btnClick = false;
					sModel.btnAction();
				
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
