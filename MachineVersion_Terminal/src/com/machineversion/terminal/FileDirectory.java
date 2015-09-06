package com.machineversion.terminal;

import android.os.Environment;

public class FileDirectory
{
	public static final String appDirectory = Environment
			.getExternalStorageDirectory() + "/MachineVision/";

	public static String getAppDirectory()
	{
		return appDirectory;
	}

}
