package com.machinevision.inverter;

public class SendThread extends Thread {		
	
	private boolean sendFlag = false;
	
	private int sendNum;
	
	private SciModel sModel;
	
	public SendThread(SciModel sci) {
		sModel = sci;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(sModel.isSciOpened() && sModel.isSTOpened()) {
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
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
