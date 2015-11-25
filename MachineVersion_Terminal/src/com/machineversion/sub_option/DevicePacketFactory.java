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

	public void savePacket(Context context)
	{
		// File file = null;
		//
		// View layout = vPager.getChildAt(vPager.getCurrentItem());
		//
		// DevicePacketBuilt packetBuilt = null;
		//
		// switch (vPager.getCurrentItem())
		// {
		// case 0:
		// file = new File(file_sysSettingDeviceGeneral);
		// General general = new General();
		//
		// general.input = ((DropDownList) layout
		// .findViewById(R.id.device_setting_input_type))
		// .getCurrentIndex()
		// + "";
		// general.output = ((DropDownList) layout
		// .findViewById(R.id.device_setting_output_type))
		// .getCurrentIndex()
		// + "";
		// general.horzStartPix = (((NumberSettingLayout) layout
		// .findViewById(R.id.device_setting_start_x)).getValue())
		// + "";
		// general.vertStartPix = (((NumberSettingLayout) layout
		// .findViewById(R.id.device_setting_start_y)).getValue())
		// + "";
		// general.inWidth = (((NumberSettingLayout) layout
		// .findViewById(R.id.device_setting_input_w)).getValue())
		// + "";
		// general.inHeight = (((NumberSettingLayout) layout
		// .findViewById(R.id.device_setting_input_h)).getValue())
		// + "";
		// general.outWidth = (((NumberSettingLayout) layout
		// .findViewById(R.id.device_setting_output_w)).getValue())
		// + "";
		// general.outHeight = (((NumberSettingLayout) layout
		// .findViewById(R.id.device_setting_output_h)).getValue())
		// + "";
		// general.expTime = ((SeekBarEditLayout) layout
		// .findViewById(R.id.device_setting_exposure)).getValue()
		// + "";
		// general.bitType = (((RadioButton) layout
		// .findViewById(R.id.device_setting_bit_radio1)).isSelected() ? 0
		// : 1)
		// + "";
		// packetBuilt = general;
		//
		// break;
		// case 1:
		// file = new File(file_sysSettingDeviceTrigger);
		// Trigger trigger = new Trigger();
		//
		// trigger.trigDelay = ((EditText) layout
		// .findViewById(R.id.device_setting_trigger_delay)).getText()
		// .toString();
		// trigger.partDelay = ((EditText) layout
		// .findViewById(R.id.device_setting_trigger_part_delay))
		// .getText().toString();
		// trigger.velocity = ((EditText) layout
		// .findViewById(R.id.device_setting_trigger_velocity))
		// .getText().toString();
		// trigger.departWide = ((EditText) layout
		// .findViewById(R.id.device_setting_trigger_depart_wide))
		// .getText().toString();
		// trigger.expLead = ((EditText) layout
		// .findViewById(R.id.device_setting_trigger_explead))
		// .getText().toString();
		// packetBuilt = trigger;
		// break;
		// case 2:
		// int count = 0;
		// file = new File(file_sysSettingDeviceAD9849);
		// AD9849 ad = new AD9849();
		//
		// /*
		// * 左边一侧菜单
		// */
		// int vga = ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// ad.vga[0] = vga + "";
		// ad.vga[1] = (vga >> 8) + "";
		//
		// ad.shp = (byte) ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// ad.hpl = (byte) ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// ad.rgpl = (byte) ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// for (int i = 0; i < ad.pxga.length; i++)
		// {
		// ad.pxga[i] = (byte) ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// }
		//
		// /*
		// * 右边一侧菜单
		// */
		// ad.rgdrv = (byte) ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// ad.shd = (byte) ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// ad.hnl = (byte) ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// ad.rgnl = (byte) ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// for (int i = 0; i < ad.hxdrv.length; i++)
		// {
		// ad.hxdrv[i] = (byte) ((SeekBarEditLayout) layout
		// .findViewById(SysSettings.SEEKBAR_START_ID + count++))
		// .getValue();
		// }
		//
		// break;
		//
		// default:
		// break;
		// }

	}
}
