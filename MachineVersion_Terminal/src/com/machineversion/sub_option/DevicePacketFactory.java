package com.machineversion.sub_option;

import java.io.File;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.emercy.dropdownlist.DropDownList;
import com.machineversion.sub_option.SystemSetting_devicePacket.*;
import com.machineversion.terminal.R;

public class DevicePacketFactory
{
	ViewPager vPager;

	public DevicePacketFactory(ViewPager vPager)
	{
		this.vPager = vPager;
	}

	public void savePacket(Context context, File file)
	{
		View layout = vPager.getChildAt(vPager.getCurrentItem());

		switch (vPager.getCurrentItem())
		{
		case 0:
			Gerneral gerneral = new Gerneral();
			gerneral.input = (byte) ((DropDownList) layout
					.findViewById(R.id.device_setting_input_type))
					.getCurrentIndex();
			gerneral.output = (byte) ((DropDownList) layout
					.findViewById(R.id.device_setting_output_type))
					.getCurrentIndex();
			gerneral.horzStartPix = Short.parseShort((((EditText) layout
					.findViewById(R.id.device_setting_start_x)).getText()
					.toString()));
			gerneral.vertStartPix = Short.parseShort((((EditText) layout
					.findViewById(R.id.device_setting_start_y)).getText()
					.toString()));
			gerneral.inWidth = 

			break;

		default:
			break;
		}
	}
}
