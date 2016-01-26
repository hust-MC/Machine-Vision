package com.machinevision.terminal;

import android.os.Environment;

public class FileDirectory
{
	private static final String appDirectory = Environment
			.getExternalStorageDirectory() + "/MachineVision/";
	
	private static final String UsbDirectory = Environment
			.getExternalStorageDirectory().getParent() + "/usb1/";

	private static final String AlgDirectory = Environment
			.getExternalStorageDirectory() + "/MachineVision/Alg/";
	private static final String AlgUSBDirectory = UsbDirectory;
	
	/**
	 * 获取App的目录路径
	 * 
	 * @return app根目录
	 */
	public static String getAppDirectory()
	{
		return appDirectory;
	}
	
	public static String getUsbDirectory()
	{
		return UsbDirectory;
	}
	
	public static String getAlgDirectory()
	{
		return AlgDirectory;
	}
	
	public static String getAlgUSBDirectory()
	{
		return AlgUSBDirectory;
	}
}
