package com.machineversion.sub_option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

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
			General general = new General();
			general.input = (byte) ((DropDownList) layout
					.findViewById(R.id.device_setting_input_type))
					.getCurrentIndex();
			general.output = (byte) ((DropDownList) layout
					.findViewById(R.id.device_setting_output_type))
					.getCurrentIndex();
			general.horzStartPix = (short) (((NumberSettingLayout) layout
					.findViewById(R.id.device_setting_start_x)).getValue());
			general.vertStartPix = (short) (((NumberSettingLayout) layout
					.findViewById(R.id.device_setting_start_y)).getValue());
			general.inWidth = (short) (((NumberSettingLayout) layout
					.findViewById(R.id.device_setting_input_w)).getValue());
			general.inHeight = (short) (((NumberSettingLayout) layout
					.findViewById(R.id.device_setting_input_w)).getValue());
			general.outWidth = (short) (((NumberSettingLayout) layout
					.findViewById(R.id.device_setting_output_w)).getValue());
			general.outHeight = (short) (((NumberSettingLayout) layout
					.findViewById(R.id.device_setting_output_h)).getValue());
			general.expTime = (short) ((SeekBarEditLayout) layout
					.findViewById(R.id.device_setting_exposure)).getValue();
			general.bitType = (byte) (((RadioGroup) layout
					.findViewById(R.id.device_setting_bit_radio1)).isSelected() ? 0
					: 1);
			// general.inited =
			// general.trigger =

			try
			{
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(file));
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			break;
		default:
			break;
		}
	}
}
