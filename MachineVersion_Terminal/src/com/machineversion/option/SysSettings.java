package com.machineversion.option;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.emercy.dropdownlist.DropDownList;
import com.emercy.dropdownlist.DropDownList.OnDropListClickListener;
import com.machineversion.sub_option.DebugMode;
import com.machineversion.sub_option.DevicePacketFactory;
import com.machineversion.sub_option.DialogBuilder.OnDialogClicked;
import com.machineversion.sub_option.NumberSettingLayout;
import com.machineversion.sub_option.SeekBarEditLayout;
import com.machineversion.sub_option.SystemSetting_devicePacket.General;
import com.machineversion.sub_option.SystemSetting_devicePacket.Trigger;
import com.machineversion.terminal.FileDirectory;
import com.machineversion.terminal.R;

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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SysSettings extends ControlPannelActivity implements
		OnDialogClicked
{
	public static final String file_sysSeting = FileDirectory.getAppDirectory()
			+ "SysSetting/";
	public static final String file_sysSettingDevice = file_sysSeting
			+ "device/";

	public static final int SEEKBAR_START_ID = SeekBarEditLayout
			.generateViewId();

	View layout;
	Spinner spinner;
	NumberSettingLayout numberSettingLayout;
	ViewPager vPager;
	DropDownList dropList;

	private View getPage1()
	{
		View page1 = getLayoutInflater().inflate(
				R.layout.vpager_device_general, null);
		// 以下两句设置下拉菜单的内容
		((DropDownList) page1.findViewById(R.id.device_setting_input_type))
				.setItem(R.array.device_setting_input_type);
		((DropDownList) page1.findViewById(R.id.device_setting_output_type))
				.setItem(R.array.device_setting_output_type);

		((SeekBarEditLayout) page1.findViewById(R.id.device_setting_exposure))
				.setMax(1720);

		File file = new File(DevicePacketFactory.file_sysSettingDeviceGeneral);

		if (file.exists())
		{
			General general = null;
			try
			{
				ObjectInputStream inputStream = new ObjectInputStream(
						new FileInputStream(file));
				general = (General) inputStream.readObject();
				inputStream.close();

				((DropDownList) page1
						.findViewById(R.id.device_setting_input_type))
						.setSelection(general.input);
				((DropDownList) page1
						.findViewById(R.id.device_setting_output_type))
						.setSelection(general.output);
				((NumberSettingLayout) page1
						.findViewById(R.id.device_setting_start_x))
						.setValue(general.horzStartPix);
				((NumberSettingLayout) page1
						.findViewById(R.id.device_setting_start_y))
						.setValue(general.vertStartPix);
				((NumberSettingLayout) page1
						.findViewById(R.id.device_setting_input_w))
						.setValue(general.inWidth);
				((NumberSettingLayout) page1
						.findViewById(R.id.device_setting_input_w))
						.setValue(general.inHeight);
				((NumberSettingLayout) page1
						.findViewById(R.id.device_setting_output_w))
						.setValue(general.outWidth);
				((NumberSettingLayout) page1
						.findViewById(R.id.device_setting_output_h))
						.setValue(general.outHeight);
				((SeekBarEditLayout) page1
						.findViewById(R.id.device_setting_exposure))
						.setValue(general.expTime);

				((RadioButton) page1
						.findViewById(R.id.device_setting_bit_radio0
								+ general.bitType)).setSelected(true);

			} catch (ClassNotFoundException | IOException e)
			{
				e.printStackTrace();
			}
		}
		return page1;
	}

	private View getPage2()
	{
		View page2 = getLayoutInflater().inflate(R.layout.vpager_device_triger,
				null);

		File file = new File(file_sysSettingDevice, "trigger");

		if (file.exists())
		{
			try
			{
				ObjectInputStream inputStream = new ObjectInputStream(
						new FileInputStream(file));
				Trigger trigger = (Trigger) inputStream.readObject();

				inputStream.close();

				((EditText) page2
						.findViewById(R.id.device_setting_trigger_delay))
						.setText(trigger.trigDelay + "");

				((EditText) page2
						.findViewById(R.id.device_setting_trigger_part_delay))
						.setText(trigger.partDelay + "");

				((EditText) page2
						.findViewById(R.id.device_setting_trigger_velocity))
						.setText(trigger.velocity + "");

				((EditText) page2
						.findViewById(R.id.device_setting_trigger_depart_wide))
						.setText(trigger.departWide + "");

				((EditText) page2
						.findViewById(R.id.device_setting_trigger_explead))
						.setText(trigger.expLead + "");
			} catch (ClassNotFoundException | IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return page2;
	}

	private LinearLayout getPage3()
	{
		int count = 0;					// 计算SeekBar的ID偏移量
		String[][] items =
		{
		{ "VGA", "SHP", "HPL", "RGPL", "P0GA", "P1GA", "P2GA", "P3GA" },
		{ "RGDRV", "SHD", "HNL", "RGNL", "H1DRV", "H2DRV", "H3DRV", "H4DRV" } };

		LinearLayout layoutOuter = new LinearLayout(this);
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

			layout.addView(textView);
			layout.addView(seekBarEditLayout);
			seekBarEditLayout.setId(SEEKBAR_START_ID + count++);

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
	 * 设置ViewPager的内容
	 */
	private void initViewPager()
	{
		LayoutInflater inflater = getLayoutInflater();
		List<View> list = new ArrayList<View>();

		View page1 = getPage1();
		View page2 = getPage2();
		LinearLayout page3 = getPage3();

		list.add(page1);
		list.add(page2);
		list.add(page3);

		vPager = (ViewPager) layout.findViewById(R.id.device_setting_vpager);
		vPager.setOffscreenPageLimit(2);
		vPager.setAdapter(new MyPagerAdapter(vPager, list));

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
		// ProgressDialog progressDialog = ProgressDialog.show(this, null,
		// "正在下载数据，请稍候...", true, false);
		layout = LayoutInflater.from(this).inflate(R.layout.device_setting,
				null);

		initViewPager();
		initDropDownList();
		// progressDialog.dismiss();

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
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			Field field;
			try
			{
				// 点击后阻止关闭
				field = dialog.getClass().getSuperclass()
						.getDeclaredField("mShowing");
				field.setAccessible(true);
				field.set(dialog, false); 					// false - 使之不能关闭(此为机关所在，其它语句相同)

				DevicePacketFactory factory = new DevicePacketFactory(vPager);

				factory.savePacket(SysSettings.this);

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
		Toast.makeText(this, "数据保存成功", Toast.LENGTH_SHORT).show();
	}

	private class MyPagerAdapter extends PagerAdapter
	{
		private List<View> list;

		public MyPagerAdapter(ViewPager vPager, List<View> list)
		{
			this.list = list;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			Log.d("MC", "remove");
			((ViewPager) container).removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position)
		{
			Log.d("MC", "instantiate");
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
	}
}
