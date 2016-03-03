package com.machineversion.inverter;

public class SendUtils {
	//ͨѶ����
	public static final byte ENQ = 0x05;
	//��Ƶ��վ��
	public static final byte NUM = 0x30;
	//��Ƶ���ȴ�ʱ��
	public static final byte WAITTIME = 0x31;
	//ͨѶЭ���β
	public static final byte TAIL = 0x0D;
	
	//�ֽ�0
	public static final byte ZERO = 0x30;
	
	
	//���ͱ��
	public static final int numStatus 	= 1;
	public static final int numOutFrq 	= 2;
	public static final int numCurrent 	= 3;
	public static final int numGetRam 	= 4;
	public static final int numGetProm 	= 5;
	
	public static final int numRun 		= 10;
	public static final int numReverse 	= 11;
	public static final int numStop 	= 12;
	public static final int numSetRam 	= 13;
	public static final int numSetProm 	= 14;
	public static final int numFrqUp 	= 15;
	public static final int numFrqDown	= 16;
	
	public static final int numReset 	= 18;
	public static final int numGetAlarm = 19;
	

	/**********����***********/	
	//��ѯ������� H70
	public static final byte[] getCurrent = {ENQ, NUM, NUM, 0x37, 0x30, WAITTIME, 0x46, 0x38, TAIL};
	
	//��ѯ���Ƶ�ʣ��ٶȣ� H6F
	public static final byte[] getOutFrq = {ENQ, NUM, NUM, 0x36, 0x46, WAITTIME, 0x30, 0x44, TAIL};
	
	//��Ƶ��״̬���� H7A
	public static final byte[] getStatus = {ENQ, NUM, NUM, 0x37, 0x41, WAITTIME, 0x30, 0x39, TAIL};
	
	//��ѯ�����ѹ H71
	public static final byte[] getVoltage = {ENQ, NUM, NUM, 0x37, 0x31, WAITTIME, 0x46, 0x39, TAIL};
	
	/**********����Ƶ�����á�����***********/
	
	//�趨Ƶ�ʶ�����RAM��H6D
	public static final byte[] getFrqRam = {ENQ, NUM, NUM, 0x36, 0x44, WAITTIME, 0x30, 0x42, TAIL};
	
	//�趨Ƶ�ʶ�����E2PROM��H6E
	public static final byte[] getFrqProm = {ENQ, NUM, NUM, 0x36, 0x45, WAITTIME, 0x30, 0x43, TAIL};
	
	//�趨Ƶ��д�루RAM��HED ZERO���Ϊʵʱ����
	public static final byte[] setFrqRam = {ENQ, NUM, NUM, 0x45, 0x44, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	//�趨Ƶ��д�루E2PROM��HEE ZERO���Ϊʵʱ����
	public static final byte[] setFrqProm = {ENQ, NUM, NUM, 0x45, 0x45, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};

	/**********��������***********/
	
	//�������� H74
	public static final byte[] getAlamrCode = {ENQ, NUM, NUM, 0x37, 0x34, WAITTIME, 0x46, 0x43, TAIL};
	
	/**********����ָ��FA***********/
	//��ת H02
	public static final byte[] run = {ENQ, NUM, NUM, 0x46, 0x41, WAITTIME, 0x30, 0x32, 0x37, 0x41, TAIL};
	//ֹͣ H00
	public static final byte[] stop = {ENQ, NUM, NUM, 0x46, 0x41, WAITTIME, 0x30, 0x30, 0x37, 0x38, TAIL};
	//��ת H04
	public static final byte[] reverse = {ENQ, NUM, NUM, 0x46, 0x41, WAITTIME, 0x30, 0x34, 0x37, 0x43, TAIL};
	
	/**********��λָ�� HFD***********/
	public static final byte[] reset = {ENQ, NUM, NUM, 0x46, 0x44, WAITTIME, 0x39, 0x36, 0x39, 0x36, 0x46, 0x39, TAIL};
	
	/**********����д��***********/
	
	//����0  ת������ H80
	public static final byte[] set0 = {ENQ, NUM, NUM, 0x38, 0x30, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	//����1  ����Ƶ�� H81
	public static final byte[] set1 = {ENQ, NUM, NUM, 0x38, 0x31, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	//����2  ����Ƶ�� H82
	public static final byte[] set2 = {ENQ, NUM, NUM, 0x38, 0x32, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	//����7  ����ʱ�� H87
	public static final byte[] set7 = {ENQ, NUM, NUM, 0x38, 0x37, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	//����8 ����ʱ�� H88
	public static final byte[] set8 = {ENQ, NUM, NUM, 0x38, 0x38, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	//����9 ���ӹ�������� H89
	public static final byte[] set9 = {ENQ, NUM, NUM, 0x38, 0x39, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	//����14 ���ø���ѡ�� H8E
	public static final byte[] set14 = {ENQ, NUM, NUM, 0x38, 0x45, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	//����71 ���õ�� HC7
	public static final byte[] set71 = {ENQ, NUM, NUM, 0x43, 0x37, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	/**********�������***********/
	
	//����0  ת������ H00
	public static final byte[] get0 = {ENQ, NUM, NUM, 0x30, 0x30, WAITTIME, 0x46, 0x31, TAIL};
	
	//����1  ����Ƶ�� H01
	public static final byte[] get1 = {ENQ, NUM, NUM, 0x30, 0x31, WAITTIME, 0x46, 0x32, TAIL};
	
	//����2  ����Ƶ�� H02
	public static final byte[] get2 = {ENQ, NUM, NUM, 0x30, 0x32, WAITTIME, 0x46, 0x33, TAIL};
	
	//����7  ����ʱ�� H07
	public static final byte[] get7 = {ENQ, NUM, NUM, 0x30, 0x37, WAITTIME, 0x46, 0x38, TAIL};
	
	//����8 ����ʱ�� H08
	public static final byte[] get8 = {ENQ, NUM, NUM, 0x30, 0x38, WAITTIME, 0x46, 0x39, TAIL};
	
	//����9 ���ӹ�������� H09
	public static final byte[] get9 = {ENQ, NUM, NUM, 0x30, 0x39, WAITTIME, 0x46, 0x41, TAIL};
	
	//����14 ���ø���ѡ�� H0E
	public static final byte[] get14 = {ENQ, NUM, NUM, 0x30, 0x45, WAITTIME, 0x30, 0x36, TAIL};
	
	//����71 ���õ�� H47
	public static final byte[] get71 = {ENQ, NUM, NUM, 0x34, 0x37, WAITTIME, 0x46, 0x43, TAIL};
	
	/**********У���***********/
	public static void getCheckSum(byte[] a) {
		byte sum = 0x00;
		for(int i = 1; i <= 9; i++) {
			sum += a[i];
		}
		byte[] bSum = Integer.toHexString(sum & 0xff).toUpperCase().getBytes();
		if(bSum.length == 1) {
			a[10] = 0x30;
			a[11] = bSum[0];
		}
		else {
			a[10] = bSum[0];
			a[11] = bSum[1];
		}
	}
}
