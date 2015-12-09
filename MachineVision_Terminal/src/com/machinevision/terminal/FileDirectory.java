package com.machinevision.terminal;

import android.os.Environment;

public class FileDirectory
{
	private static final String appDirectory = Environment
			.getExternalStorageDirectory() + "/MachineVision/";

	/**
	 * 获取App的目录路径
	 * 
	 * @return app根目录
	 */
	public static String getAppDirectory()
	{
		return appDirectory;
	}

}
