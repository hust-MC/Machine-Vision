package com.machinevision.inverter;

public class SendUtils {
	//通讯请求
	public static final byte ENQ = 0x05;
	//变频器站号
	public static final byte NUM = 0x30;
	//变频器等待时间
	public static final byte WAITTIME = 0x31;
	//通讯协议结尾
	public static final byte TAIL = 0x0D;
	
	//字节0
	public static final byte ZERO = 0x30;
	
	
	//发送编号
	public static final int numStatus 	= 1;
	public static final int numOutFrq 	= 2;
	public static final int numCurrent 	= 3;
	public static final int numGetProm 	= 4;
	
	public static final int numRun 		= 10;
	public static final int numReverse 	= 11;
	public static final int numStop 	= 12;
	public static final int numSetRam 	= 13;
	public static final int numSetProm 	= 14;
	public static final int numFrqUp 	= 15;
	public static final int numFrqDown	= 16;
	
	public static final int numReset 	= 18;
	public static final int numGetAlarm = 19;
	

	/**********监视***********/	
	//查询输出电流 H70
	public static final byte[] getCurrent = {ENQ, NUM, NUM, 0x37, 0x30, WAITTIME, 0x46, 0x38, TAIL};
	
	//查询输出频率（速度） H6F
	public static final byte[] getOutFrq = {ENQ, NUM, NUM, 0x36, 0x46, WAITTIME, 0x30, 0x44, TAIL};
	
	//变频器状态监视 H7A
	public static final byte[] getStatus = {ENQ, NUM, NUM, 0x37, 0x41, WAITTIME, 0x30, 0x39, TAIL};
	
	//查询输出电压 H71
	public static final byte[] getVoltage = {ENQ, NUM, NUM, 0x37, 0x31, WAITTIME, 0x46, 0x39, TAIL};
	
	/**********运行频率设置、读出***********/
	
	//设定频率读出（RAM）H6D
	public static final byte[] getFrqRam = {ENQ, NUM, NUM, 0x36, 0x44, WAITTIME, 0x30, 0x42, TAIL};
	
	//设定频率读出（E2PROM）H6E
	public static final byte[] getFrqProm = {ENQ, NUM, NUM, 0x36, 0x45, WAITTIME, 0x30, 0x43, TAIL};
	
	//设定频率写入（RAM）HED ZERO数据为实时计算
	public static final byte[] setFrqRam = {ENQ, NUM, NUM, 0x45, 0x44, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};
	
	//设定频率写入（E2PROM）HEE ZERO数据为实时计算
	public static final byte[] setFrqProm = {ENQ, NUM, NUM, 0x45, 0x45, WAITTIME, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, TAIL};

	/**********报警定义***********/
	
	//报警定义 H74
	public static final byte[] getAlamrCode = {ENQ, NUM, NUM, 0x37, 0x34, WAITTIME, 0x46, 0x43, TAIL};
	
	/**********运行指令FA***********/
	//正转 H02
	public static final byte[] run = {ENQ, NUM, NUM, 0x46, 0x41, WAITTIME, 0x30, 0x32, 0x37, 0x41, TAIL};
	//停止 H00
	public static final byte[] stop = {ENQ, NUM, NUM, 0x46, 0x41, WAITTIME, 0x30, 0x30, 0x37, 0x38, TAIL};
	//反转 H04
	public static final byte[] reverse = {ENQ, NUM, NUM, 0x46, 0x41, WAITTIME, 0x30, 0x34, 0x37, 0x43, TAIL};
	
	/**********复位指令 HFD***********/
	public static final byte[] reset = {ENQ, NUM, NUM, 0x46, 0x44, WAITTIME, 0x39, 0x36, 0x39, 0x36, 0x46, 0x39, TAIL};
	
	/**********校验和***********/
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
