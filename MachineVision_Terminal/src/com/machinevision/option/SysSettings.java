package com.machinevision.option;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.emercy.dropdownlist.DropDownList;
import com.emercy.dropdownlist.DropDownList.OnDropListClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.machinevision.terminal.R;
import com.machinevision.net.CmdHandle;
import com.machinevision.sub_option.DebugMode;
import com.machinevision.sub_option.NumberSettingLayout;
import com.machinevision.sub_option.SeekBarEditLayout;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;
import com.machinevision.sub_option.SystemSetting_devicePacket.AD9849;
import com.machinevision.sub_option.SystemSetting_devicePacket.Mode;
import com.machinevision.sub_option.SystemSetting_devicePacket.Net;
import com.machinevision.sub_option.SystemSetting_devicePacket.Parameters;
import com.machinevision.sub_option.SystemSetting_devicePacket.Sensor;
import com.machinevision.sub_option.SystemSetting_devicePacket.Trigger;
import com.machinevision.sub_option.SystemSetting_devicePacket.UART;
import com.machinevision.sub_option.SystemSetting_devicePacket.Version;
import com.machinevision.terminal.FileDirectory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class SysSettings extends ControlPannelActivity implements
		OnDialogClicked
{
	public static final String file_sysSeting = FileDirectory.getAppDirectory()
			+ "SysSetting/";

	public static final int SEEKBAR_START_ID = 0x84569874;

	View layout;
	ViewPager vPager;
	DropDownList dropList;				// viewPager页卡切换下拉菜单
	MyPagerAdapter pagerAdapter;

	/**
	 * 通用界面设置
	 * 
	 * @return
	 */
	private View getPage1()
	{
		View page1 = getLayoutInflater().inflate(
				R.layout.vpager_device_general, null);
		Sensor sensor = Parameters.getInstance().sensor;
		Mode mode = Parameters.getInstance().mode;

		// 以下两句设置下拉菜单的内容
		((DropDownList) page1.findViewById(R.id.device_setting_input_type))
				.setItem(R.array.device_setting_input_type);
		((DropDownList) page1.findViewById(R.id.device_setting_output_type))
				.setItem(R.array.device_setting_output_type);
		((SeekBarEditLayout) page1.findViewById(R.id.device_setting_exposure))
				.setMax(65536);

		((NumberSettingLayout) page1.findViewById(R.id.device_setting_start_x))
				.setValue(sensor.startPixel_width);
		((NumberSettingLayout) page1.findViewById(R.id.device_setting_start_y))
				.setValue(sensor.startPixel_height);
		((NumberSettingLayout) page1.findViewById(R.id.device_setting_input_w))
				.setValue(sensor.width_input);
		((NumberSettingLayout) page1.findViewById(R.id.device_setting_input_h))
				.setValue(sensor.height_input);

		((SeekBarEditLayout) page1.findViewById(R.id.device_setting_exposure))
				.setValue(mode.expoTime);

		if (mode.bitType == 8)
			((RadioButton) page1.findViewById(R.id.device_setting_bit_radio0))
					.setSelected(true);
		else
			((RadioButton) page1.findViewById(R.id.device_setting_bit_radio1))
					.setSelected(true);

		if (mode.trigger == 0)
			((CheckBox) page1.findViewById(R.id.device_setting_mode_checkbox0))
					.setChecked(true);
		else if (mode.trigger == 1)
			((CheckBox) page1.findViewById(R.id.device_setting_mode_checkbox1))
					.setChecked(true);
		else
			((CheckBox) page1.findViewById(R.id.device_setting_mode_checkbox2))
					.setChecked(true);

		return page1;
	}
	/**
	 * trigger设置界面
	 * 
	 * @return
	 */
	private View getPage2()
	{
		View page2 = getLayoutInflater().inflate(R.layout.vpager_device_triger,
				null);

		Trigger trigger = Parameters.getInstance().trigger;
		((EditText) page2.findViewById(R.id.device_setting_trigger_delay))
				.setText(trigger.trigDelay + "");

		((EditText) page2.findViewById(R.id.device_setting_trigger_part_delay))
				.setText(trigger.partDelay + "");

		((EditText) page2.findViewById(R.id.device_setting_trigger_velocity))
				.setText(trigger.velocity + "");

		((EditText) page2.findViewById(R.id.device_setting_trigger_depart_wide))
				.setText(trigger.departWide + "");

		((EditText) page2.findViewById(R.id.device_setting_trigger_explead))
				.setText(trigger.expLead + "");
		return page2;
	}

	/**
	 * ad9849界面设置
	 * 
	 * @return
	 */
	private LinearLayout getPage3()
	{
		int count = 0; // 计算SeekBar的ID偏移量
		String[][] items =
		{
		{ "VGA", "SHP", "HPL", "RGPL", "P0GA", "P1GA", "P2GA", "P3GA" },
		{ "RGDRV", "SHD", "HNL", "RGNL", "H1DRV", "H2DRV", "H3DRV", "H4DRV" } };
		AD9849 ad9849 = Parameters.getInstance().ad9849;

		LinearLayout layoutOuter = new LinearLayout(this);
		layoutOuter.setId(SEEKBAR_START_ID - 1);
		layoutOuter.setOrientation(LinearLayout.HORIZONTAL);
		layoutOuter.setPadding(20, 30, 20, 0);
		LinearLayout layoutLeft = new LinearLayout(this);
		layoutLeft.setOrientation(LinearLayout.VERTICAL);
		LinearLayout layoutRight = new LinearLayout(this);
		layoutRight.setOrientation(LinearLayout.VERTICAL);

		LayoutParams paramsLeft = new LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1);
		LayoutParams paramsRight = new LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1);
		paramsRight.leftMargin = 28;

		/**
		 * 生成条目
		 */
		for (String item : items[0])
		{
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.HORIZONTAL);

			LayoutParams paramsInner = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1);
			paramsInner.rightMargin = 8;
			TextView textView = new TextView(this);
			textView.setLayoutParams(paramsInner);
			textView.setText(item);
			textView.setGravity(Gravity.END);

			SeekBarEditLayout seekBarEditLayout = new SeekBarEditLayout(this);
			paramsInner = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
			seekBarEditLayout.setLayoutParams(paramsInner);
			seekBarEditLayout.setValue(ad9849.pageContents[count]);
			seekBarEditLayout.setId(SEEKBAR_START_ID + count++);

			layout.addView(textView);
			layout.addView(seekBarEditLayout);

			layoutLeft.addView(layout);
		}

		for (String item : items[1])
		{
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.HORIZONTAL);

			LayoutParams paramsInner = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1);
			paramsInner.rightMargin = 8;
			TextView textView = new TextView(this);
			textView.setLayoutParams(paramsInner);
			textView.setText(item);
			textView.setGravity(Gravity.END);

			SeekBarEditLayout seekBarEditLayout = new SeekBarEditLayout(this);
			paramsInner = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
			seekBarEditLayout.setLayoutParams(paramsInner);
			seekBarEditLayout.setValue(ad9849.pageContents[count]);
			seekBarEditLayout.setId(SEEKBAR_START_ID + count++);

			layout.addView(textView);
			layout.addView(seekBarEditLayout);

			layoutRight.addView(layout);
		}

		layoutOuter.addView(layoutLeft, paramsLeft);
		layoutOuter.addView(layoutRight, paramsRight);
		return layoutOuter;
	}

	/**
	 * agc和aec设置界面
	 * 
	 * @return
	 */
	private View getPage4()
	{
		View page4 = getLayoutInflater().inflate(
				R.layout.vpager_device_mt9v032, null);

		Net net = Parameters.getInstance().net;

		return page4;
	}

	/**
	 * 时间设置界面
	 * 
	 * @return
	 */
	private View getPage5()
	{
		View page5 = getLayoutInflater().inflate(
				R.layout.vpager_device_isl12026, null);
		return page5;
	}

	/**
	 * 网络设置界面
	 * 
	 * @return
	 */

	private View getPage6()
	{
		View page6 = getLayoutInflater().inflate(R.layout.vpager_device_net,
				null);
		Net net = Parameters.getInstance().net;
		((EditText) page6.findViewById(R.id.device_setting_mac_address))
				.setText(net.mac_address[0] + ":" + net.mac_address[1] + ":"
						+ net.mac_address[2] + ":" + net.mac_address[3] + ":"
						+ net.mac_address[4] + ":" + net.mac_address[5]);

		((EditText) page6.findViewById(R.id.device_setting_ip_address))
				.setText(net.ip_address[0] + "." + net.ip_address[1] + "."
						+ net.ip_address[2] + "." + net.ip_address[3]);

		((EditText) page6.findViewById(R.id.device_setting_server_ip_address))
				.setText(net.remote_ip[0] + "." + net.remote_ip[1] + "."
						+ net.remote_ip[2] + "." + net.remote_ip[3]);

		((EditText) page6.findViewById(R.id.device_setting_tcp_port))
				.setText(net.port + "");

		return page6;
	}

	/**
	 * 串口和can设置界面
	 * 
	 * @return
	 */
	private View getPage7()
	{
		View page7 = getLayoutInflater().inflate(
				R.layout.vpager_device_uart_hecc, null);
		UART uart = Parameters.getInstance().uart;
		byte baurate = (byte) uart.baudRate;
		byte work_mode = (byte) uart.work_mode;

		Spinner uart_baudrate = (Spinner) page7
				.findViewById(R.id.uart_baudrate);
		Spinner parity = (Spinner) page7.findViewById(R.id.parity);
		Spinner stop_bit = (Spinner) page7.findViewById(R.id.stop_bit);
		Spinner data_len = (Spinner) page7.findViewById(R.id.data_len);
		Spinner hecc_baudrate = (Spinner) page7
				.findViewById(R.id.hecc_baudrate);

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				R.layout.spinner2, getResources().getStringArray(
						R.array.uart_baudrate));
		uart_baudrate.setAdapter(adapter1);
		uart_baudrate.setSelection(baurate - 1);

		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				R.layout.spinner2, getResources()
						.getStringArray(R.array.parity));
		parity.setAdapter(adapter2);
		parity.setSelection(work_mode & 0x03);

		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				R.layout.spinner2, getResources().getStringArray(
						R.array.stop_bit));
		stop_bit.setAdapter(adapter3);
		stop_bit.setSelection((work_mode & 0x08) >> 3);

		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
				R.layout.spinner2, getResources().getStringArray(
						R.array.data_len));
		data_len.setAdapter(adapter4);
		data_len.setSelection(((work_mode & 0xf0) >> 4) - 5);

		ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this,
				R.layout.spinner2, getResources().getStringArray(
						R.array.hecc_baurate));
		hecc_baudrate.setAdapter(adapter5);

		return page7;
	}

	/**
	 * version设置界面
	 * 
	 * @return
	 */
	private View getPage8()
	{
		View page8 = getLayoutInflater().inflate(
				R.layout.vpager_device_at25040, null);
		Version version = Parameters.getInstance().version;

		((EditText) page8.findViewById(R.id.camera_id)).setText(version.id[0]
				+ "." + version.id[1] + "." + version.id[2] + "."
				+ version.id[3]);
		((EditText) page8.findViewById(R.id.version)).setText(version.version
				+ "");

		((EditText) page8.findViewById(R.id.write_time))
				.setText(version.write_time[0] + "" + version.write_time[1]
						+ "." + version.write_time[2] + "."
						+ version.write_time[3]);

		return page8;
	}

	/**
	 * 设置ViewPager的内容
	 */
	private void initViewPager()
	{
		List<View> list = new ArrayList<View>();

		list.add(getPage1());
		list.add(getPage2());
		list.add(getPage3());
		list.add(getPage4());
		list.add(getPage5());
		list.add(getPage6());
		list.add(getPage7());
		list.add(getPage8());

		pagerAdapter = new MyPagerAdapter(vPager, list);
		vPager = (ViewPager) layout.findViewById(R.id.device_setting_vpager);
		vPager.setOffscreenPageLimit(2);
		vPager.setAdapter(pagerAdapter);

		vPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int arg0)
			{
				dropList.setSelection(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
	}

	private void initDropDownList()
	{
		dropList = (DropDownList) layout
				.findViewById(R.id.device_setting_droplist);
		dropList.setItem(getResources().getStringArray(R.array.device_setting));

		dropList.setOnListClickListener(new OnDropListClickListener()
		{
			@Override
			public void onListItemClick(DropDownList dropDownList, int which)
			{
				if (vPager != null)
				{
					vPager.setCurrentItem(which);
				}
			}
		});
	}

	@Override
	protected void onSpecialItemClicked(int position)
	{
		layout = LayoutInflater.from(this).inflate(R.layout.device_setting,
				null);

		initViewPager();
		initDropDownList();

		AlertDialog dialog = new AlertDialog.Builder(SysSettings.this)
				.setTitle("常规").setView(layout)
				.setPositiveButton("应用", new ApplyButton())
				.setNegativeButton("关闭", new CancelButton()).create();

		dialog.show();
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		dialog.getWindow()
				.setLayout((int) (size.x * 0.8), (int) (size.y * 0.7));
		// 让对话框能够弹出输入法
		dialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
				.setTextSize(27F);
		((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE)
				.setTextSize(27F);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_option);

		wholeMenu = new MenuWithSubMenu(R.array.option_sys_settings,
				R.array.option_sys_settings_sub,
				R.array.option_sys_settings_type, 0);
		init_widget();
		setListViewClicked();
	}

	/**
	 * 设备参数设置，确认按钮事件
	 * 
	 * @author M C
	 * 
	 */
	class ApplyButton implements OnClickListener
	{
		CmdHandle cmdHandle = CmdHandle.getInstance();

		private void sendPackage()
		{
			View page = pagerAdapter.getCurrentView();
			Gson gson = new Gson();
			JsonObject json = new JsonObject();
			switch (vPager.getCurrentItem())
			{
			case 0:
				Sensor sensor = Parameters.getInstance().sensor;
				Mode mode = Parameters.getInstance().mode;

				sensor.startPixel_width = ((NumberSettingLayout) page
						.findViewById(R.id.device_setting_start_x)).getValue();
				sensor.startPixel_height = ((NumberSettingLayout) page
						.findViewById(R.id.device_setting_start_y)).getValue();
				sensor.width_input = ((NumberSettingLayout) page
						.findViewById(R.id.device_setting_input_w)).getValue();
				sensor.height_input = ((NumberSettingLayout) page
						.findViewById(R.id.device_setting_input_h)).getValue();

				Log.d("MC", "before:" + Parameters.getInstance().mode.expoTime);
				mode.expoTime = ((SeekBarEditLayout) page
						.findViewById(R.id.device_setting_exposure)).getValue();
				Log.d("MC", "after:" + Parameters.getInstance().mode.expoTime);
				mode.bitType = ((RadioButton) page
						.findViewById(R.id.device_setting_bit_radio0))
						.isChecked() ? 8 : 16;

				if (((CheckBox) page
						.findViewById(R.id.device_setting_mode_checkbox0))
						.isChecked())
				{
					mode.trigger = 0;
				}
				else if (((CheckBox) page
						.findViewById(R.id.device_setting_mode_checkbox1))
						.isChecked())
				{
					mode.trigger = 1;
				}
				else
				{
					mode.trigger = 2;
				}

				json.add("mode", gson.toJsonTree(mode));
				JsonObject jsonSonsor = new JsonObject();
				jsonSonsor.add("sensor", gson.toJsonTree(sensor));

				cmdHandle.setJson((jsonSonsor.toString() + "\0").getBytes());
				break;
			case 1:
				Trigger trigger = Parameters.getInstance().trigger;

				trigger.trigDelay = Integer.valueOf(((EditText) page
						.findViewById(R.id.device_setting_trigger_delay))
						.getText().toString());
				trigger.partDelay = Integer.parseInt(((EditText) page
						.findViewById(R.id.device_setting_trigger_part_delay))
						.getText().toString());
				trigger.velocity = Integer.parseInt(((EditText) page
						.findViewById(R.id.device_setting_trigger_velocity))
						.getText().toString());
				trigger.departWide = Integer.parseInt(((EditText) page
						.findViewById(R.id.device_setting_trigger_depart_wide))
						.getText().toString());
				trigger.expLead = Integer.parseInt(((EditText) page
						.findViewById(R.id.device_setting_trigger_explead))
						.getText().toString());

				json.add("trigger", gson.toJsonTree(trigger));
				break;

			case 2:
				int count = 0;
				AD9849 ad9849 = Parameters.getInstance().ad9849;
				// 第一列
				int vga = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.vga[0] = vga & 0xFF;
				ad9849.vga[1] = (vga >> 8) & 0xFF;
				ad9849.shp = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.hpl = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.rgpl = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.pxga[0] = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.pxga[1] = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.pxga[2] = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.pxga[3] = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				// 第二列
				ad9849.rgdrv = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.shd = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.hnl = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.rgnl = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.hxdrv[0] = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.hxdrv[1] = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.hxdrv[2] = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();
				ad9849.hxdrv[3] = ((SeekBarEditLayout) findViewById(SEEKBAR_START_ID
						+ count++)).getValue();

				json.add("ad9849", gson.toJsonTree(ad9849));
				break;

			case 3:
				break;
			case 4:
				break;
			case 5:
				Net net = Parameters.getInstance().net;
				int i = 0;
				for (String str : ((EditText) page
						.findViewById(R.id.device_setting_mac_address))
						.getText().toString().split("."))
				{
					net.mac_address[i++] = Integer.parseInt(str);
				}
				i = 0;
				for (String str : ((EditText) page
						.findViewById(R.id.device_setting_ip_address))
						.getText().toString().split("."))
				{
					net.ip_address[i++] = Integer.parseInt(str);
				}
				i = 0;
				for (String str : ((EditText) page
						.findViewById(R.id.device_setting_server_ip_address))
						.getText().toString().split("."))
				{
					net.remote_ip[i++] = Integer.parseInt(str);
				}

				net.port = Integer.parseInt(((EditText) page
						.findViewById(R.id.device_setting_tcp_port)).getText()
						.toString());

				json.add("net", gson.toJsonTree(net));
				break;

			case 6:
				UART uart = Parameters.getInstance().uart;

				Spinner uart_baudrate = (Spinner) page
						.findViewById(R.id.uart_baudrate);
				Spinner parity = (Spinner) page.findViewById(R.id.parity);
				Spinner stop_bit = (Spinner) page.findViewById(R.id.stop_bit);
				Spinner data_len = (Spinner) page.findViewById(R.id.data_len);

				uart.baudRate = uart_baudrate.getSelectedItemPosition() + 1;
				uart.work_mode = parity.getSelectedItemPosition()
						| stop_bit.getSelectedItemPosition()
						| data_len.getSelectedItemPosition();

				json.add("uart", gson.toJsonTree(uart));
				break;
			case 7:
				Version version = Parameters.getInstance().version;

				i = 0;
				for (String str : ((EditText) page.findViewById(R.id.camera_id))
						.getText().toString().split("\\."))
				{
					version.id[i++] = Integer.parseInt(str);
				}

				version.version = Integer.parseInt(((EditText) page
						.findViewById(R.id.version)).getText().toString());

				String[] strs = ((EditText) page.findViewById(R.id.write_time))
						.getText().toString().split("\\.");
				version.write_time[0] = Integer.parseInt(strs[0]
						.substring(0, 2));
				version.write_time[1] = Integer.parseInt(strs[0]
						.substring(2, 4));
				version.write_time[2] = Integer.parseInt(strs[1]);
				version.write_time[3] = Integer.parseInt(strs[2]);

				json.add("version", gson.toJsonTree(version));
				break;
			default:
			}
			cmdHandle.setJson((json.toString() + "\0").getBytes());
		}
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			try
			{
				// 点击后阻止关闭
				Field field = dialog.getClass().getSuperclass()
						.getDeclaredField("mShowing");
				field.setAccessible(true);
				field.set(dialog, false); 					// false - 使之不能关闭(此为机关所在，其它语句相同)

				sendPackage();

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	class CancelButton implements OnClickListener
	{
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			Field field;
			try
			{
				// 解除点击后阻止关闭
				field = dialog.getClass().getSuperclass()
						.getDeclaredField("mShowing");
				field.setAccessible(true);
				field.set(dialog, true); // true - 使之自动关闭(此为机关所在，其它语句相同)
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 自动生成界面时，确认按钮事件
	 */
	@Override
	public void onPositiveButtonClicked(String[] value)
	{
		startActivity(new Intent(this, DebugMode.class));
	}

	private class MyPagerAdapter extends PagerAdapter
	{
		private List<View> list;
		private View currentView;

		public View getCurrentView()
		{
			return currentView;
		}

		public MyPagerAdapter(ViewPager vPager, List<View> list)
		{
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			((ViewPager) container).removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position)
		{
			((ViewPager) container).addView(list.get(position));
			return list.get(position);
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object)
		{
			currentView = (View) object;
		}

	}

}
