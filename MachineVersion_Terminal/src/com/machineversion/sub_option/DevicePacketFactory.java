package com.machineversion.sub_option;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.RadioButton;

import com.emercy.dropdownlist.DropDownList;
import com.machineversion.option.SysSettings;
import com.machineversion.sub_option.SystemSetting_devicePacket.*;
import com.machineversion.terminal.R;

public class DevicePacketFactory
{
	ViewPager vPager;

	public DevicePacketFactory(ViewPager vPager)
	{
		this.vPager = vPager;
	}

	public void sendPacket(Context context)
	{
		View layout = vPager.getChildAt(vPager.getCurrentItem());

		DevicePacketBuilt packetBuilt = null;

		switch (vPager.getCurrentItem())
		{
		case 0:

		}
	}
}
