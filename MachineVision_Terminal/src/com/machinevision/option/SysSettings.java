package com.machinevision.option;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.emercy.dropdownlist.DropDownList;
import com.emercy.dropdownlist.DropDownList.OnDropListClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.machinevision.terminal.R;
import com.machinevision.common_widget.DialogWindow;
import com.machinevision.common_widget.EToast;
import com.machinevision.common_widget.SeekBarEditLayout;
import com.machinevision.inverter.InverterActivity;
import com.machinevision.net.NetUtils;
import com.machinevision.sub_option.DebugMode;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;
import com.machinevision.sub_option.SystemSetting_devicePacket.AD9849;
import com.machinevision.sub_option.SystemSetting_devicePacket.Net;
import com.machinevision.sub_option.SystemSetting_devicePacket.Parameters;
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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.EditText;
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
	 * agc和aec设置界面
	 * 
	 * @return
	 */
	private View getPage1()
	{
		View page1 = getLayoutInflater().inflate(
				R.layout.vpager_device_mt9v032, null);

		Net net = Parameters.getInstance().net;

		return page1;
	}

	/**
	 * 时间设置界面
	 * 
	 * @return
	 */
	private View getPage2()
	{
		View page2 = getLayoutInflater().inflate(
				R.layout.vpager_device_isl12026, null);
		return page2;
	}

	/**
	 * version设置界面
	 * 
	 * @return
	 */
	private View getPage3()
	{
		View page3 = getLayoutInflater().inflate(
				R.layout.vpager_device_at25040, null);
		Version version = Parameters.getInstance().version;

		((EditText) page3.findViewById(R.id.camera_id)).setText(version.id[0]
				+ "." + version.id[1] + "." + version.id[2] + "."
				+ version.id[3]);
		((EditText) page3.findViewById(R.id.version)).setText(version.version
				+ "");

		((EditText) page3.findViewById(R.id.write_time))
				.setText(version.write_time[0] + "" + version.write_time[1]
						+ "." + version.write_time[2] + "."
						+ version.write_time[3]);

		return page3;
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
	protected String[] getCurrentValue(int position)
	{
		String[] currentValue = new String[getResources().getStringArray(
				R.array.option_camera_params_sub).length];
		int count = 0;
		switch (position)
		{
		// 获取当前网络设置
		case 2:
		{
			currentValue[count++] = NetUtils.ip;
			currentValue[count++] = NetUtils.port + "";
			break;
		}
		}
		return currentValue;
	}
	@Override
	protected void onSpecialItemClicked(int position)
	{
		final Gson gson = new Gson();
		final JsonObject json = new JsonObject();
		switch (position)
		{
		case 1:
		{
			final View viewTrigger = getLayoutInflater().inflate(
					R.layout.vpager_device_triger, null);

			Trigger trigger = Parameters.getInstance().trigger;
			((EditText) viewTrigger
					.findViewById(R.id.device_setting_trigger_delay))
					.setText(trigger.trigDelay + "");

			((EditText) viewTrigger
					.findViewById(R.id.device_setting_trigger_part_delay))
					.setText(trigger.partDelay + "");

			((EditText) viewTrigger
					.findViewById(R.id.device_setting_trigger_velocity))
					.setText(trigger.velocity + "");

			((EditText) viewTrigger
					.findViewById(R.id.device_setting_trigger_depart_wide))
					.setText(trigger.departWide + "");

			((EditText) viewTrigger
					.findViewById(R.id.device_setting_trigger_explead))
					.setText(trigger.expLead + "");
			DialogWindow dialog = new DialogWindow.Builder(this)
					.setTitle(
							getResources().getStringArray(
									R.array.option_sys_settings)[position])
					.setView(viewTrigger)
					.setPositiveButton("应用", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{

							Trigger trigger = Parameters.getInstance().trigger;

							trigger.trigDelay = Integer.valueOf(((EditText) viewTrigger
									.findViewById(R.id.device_setting_trigger_delay))
									.getText().toString());
							trigger.partDelay = Integer.parseInt(((EditText) viewTrigger
									.findViewById(R.id.device_setting_trigger_part_delay))
									.getText().toString());
							trigger.velocity = Integer.parseInt(((EditText) viewTrigger
									.findViewById(R.id.device_setting_trigger_velocity))
									.getText().toString());
							trigger.departWide = Integer.parseInt(((EditText) viewTrigger
									.findViewById(R.id.device_setting_trigger_depart_wide))
									.getText().toString());
							trigger.expLead = Integer.parseInt(((EditText) viewTrigger
									.findViewById(R.id.device_setting_trigger_explead))
									.getText().toString());

							json.add("trigger", gson.toJsonTree(trigger));
							sendJson(json);
						}
					}).setNegativeButton("取消", null).create();
			dialog.show();
			dialog.setWindowWidth(800);
			break;
		}

		// AD9849菜单
		case 3:
		{
			int count = 0; // 计算SeekBar的偏移量
			String[][] items =
			{
					{ "VGA", "SHP", "HPL", "RGPL", "P0GA", "P1GA", "P2GA",
							"P3GA" },
					{ "RGDRV", "SHD", "HNL", "RGNL", "H1DRV", "H2DRV", "H3DRV",
							"H4DRV" } };
			final SeekBarEditLayout[][] seekBarEditLayouts = new SeekBarEditLayout[2][8];
			AD9849 ad9849 = Parameters.getInstance().ad9849;

			final LinearLayout viewAd9849 = new LinearLayout(this);
			viewAd9849.setOrientation(LinearLayout.HORIZONTAL);
			viewAd9849.setPadding(20, 30, 20, 0);
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

				seekBarEditLayouts[0][count] = new SeekBarEditLayout(this);
				paramsInner = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
				seekBarEditLayouts[0][count].setLayoutParams(paramsInner);
				seekBarEditLayouts[0][count]
						.setValue(ad9849.pageContents[count]);

				layout.addView(textView);
				layout.addView(seekBarEditLayouts[0][count++]);

				layoutLeft.addView(layout);
			}
			count = 0;
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

				seekBarEditLayouts[1][count] = new SeekBarEditLayout(this);
				paramsInner = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
				seekBarEditLayouts[1][count].setLayoutParams(paramsInner);
				seekBarEditLayouts[1][count]
						.setValue(ad9849.pageContents[count]);

				layout.addView(textView);
				layout.addView(seekBarEditLayouts[1][count++]);

				layoutRight.addView(layout);
			}

			viewAd9849.addView(layoutLeft, paramsLeft);
			viewAd9849.addView(layoutRight, paramsRight);

			DialogWindow dialog = new DialogWindow.Builder(this)
					.setTitle(getResources().getStringArray(
									R.array.option_sys_settings)[position])
					.setView(viewAd9849)
					.setPositiveButton("应用", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Gson gson = new Gson();
							JsonObject json = new JsonObject();
							AD9849 ad9849 = Parameters.getInstance().ad9849;
							// 第一列
							int vga = seekBarEditLayouts[0][0].getValue();
							ad9849.vga[0] = vga & 0xFF;
							ad9849.vga[1] = (vga >> 8) & 0xFF;
							ad9849.shp = seekBarEditLayouts[0][1].getValue();
							ad9849.hpl = seekBarEditLayouts[0][2].getValue();
							ad9849.rgpl = seekBarEditLayouts[0][3].getValue();
							ad9849.pxga[0] = seekBarEditLayouts[0][4]
									.getValue();
							ad9849.pxga[1] = seekBarEditLayouts[0][5]
									.getValue();
							ad9849.pxga[2] = seekBarEditLayouts[0][6]
									.getValue();
							ad9849.pxga[3] = seekBarEditLayouts[0][7]
									.getValue();
							// 第二列
							ad9849.rgdrv = seekBarEditLayouts[1][0].getValue();
							ad9849.shd = seekBarEditLayouts[1][1].getValue();
							ad9849.hnl = seekBarEditLayouts[1][2].getValue();
							ad9849.rgnl = seekBarEditLayouts[1][3].getValue();
							ad9849.hxdrv[0] = seekBarEditLayouts[1][4]
									.getValue();
							ad9849.hxdrv[1] = seekBarEditLayouts[1][5]
									.getValue();
							ad9849.hxdrv[2] = seekBarEditLayouts[1][6]
									.getValue();
							ad9849.hxdrv[3] = seekBarEditLayouts[1][7]
									.getValue();

							json.add("ad9849", gson.toJsonTree(ad9849));
							sendJson(json);
						}
					}).setNegativeButton("取消", null).create();
			dialog.show();
			break;
		}
		case 4:
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

			dialog.getWindow().setLayout((int) (size.x * 0.8),
					(int) (size.y * 0.7));
			// 让对话框能够弹出输入法
			dialog.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

			((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
					.setTextSize(27F);
			((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE)
					.setTextSize(27F);
			break;
		}
		case 5:
		{
			startActivity(new Intent(this, InverterActivity.class));
			break;
		}
		default:
			Log.e("MC", "sysSettings default");
		}
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
		private void sendPackage()
		{
			View page = pagerAdapter.getCurrentView();
			Gson gson = new Gson();
			JsonObject json = new JsonObject();
			switch (vPager.getCurrentItem())
			{
			case 1:
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

			case 2:
			{
				Version version = Parameters.getInstance().version;

				int i = 0;
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
			}
			default:
			}
			sendJson(json);
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
	public void onPositiveButtonClicked(String[] value, int position)
	{
		switch (position)
		{
		case 0:
			startActivity(new Intent(this, DebugMode.class));
			break;
		// 设置IP
		case 2:
			String[] strs = value[0].split("\\.");
			try
			{

				if (strs.length == 4 && Integer.parseInt(strs[0]) < 255
						&& Integer.parseInt(strs[1]) < 255
						&& Integer.parseInt(strs[2]) < 255
						&& Integer.parseInt(strs[3]) < 255)
				{
					NetUtils.setIp(value[0]);
				}
				else
				{
					EToast.makeText(this, "输入的IP有误", EToast.LENGTH_SHORT)
							.show();
				}
			} catch (NumberFormatException e)
			{
				EToast.makeText(this, "输入的IP有误", EToast.LENGTH_SHORT).show();
				Log.e("MC", e.getMessage());
			}
		default:
			break;
		}
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
