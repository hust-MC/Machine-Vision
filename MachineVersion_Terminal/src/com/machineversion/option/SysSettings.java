package com.machineversion.option;

import java.util.ArrayList;
import java.util.List;

import com.machineversion.sub_option.DebugMode;
import com.machineversion.sub_option.DialogBuilder.OnDialogClicked;
import com.machineversion.sub_option.NumberSettingLayout;
import com.machineversion.sub_option.SeekBarEditLayout;
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
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.Toast;

public class SysSettings extends ControlPannelActivity implements
		OnDialogClicked
{
	View layout;
	Spinner spinner;
	NumberSettingLayout numberSettingLayout;
	ViewPager vPager;

	private LinearLayout getPage3()
	{
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

		View page1 = inflater.inflate(R.layout.vpager_device_general, null);
		((SeekBarEditLayout) page1.findViewById(R.id.device_setting_exposure))
				.setText(50);

		LinearLayout page3 = getPage3();

		list.add(page1);
		list.add(inflater.inflate(R.layout.vpager_device_triger, null));
		list.add(page3);

		vPager = (ViewPager) layout.findViewById(R.id.device_setting_vpager);
		vPager.setAdapter(new MyPagerAdapter(vPager, list));

		vPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int arg0)
			{
				if (spinner != null)
				{
					spinner.setSelection(arg0);
				}
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
	private void initSpinner()
	{
		spinner = (Spinner) layout.findViewById(R.id.device_setting_spinner);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (vPager != null)
				{
					Log.d("MC", position + "");
					vPager.setCurrentItem(position, false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				Log.d("MC", "nothing selected");
			}
		});
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spiner, getResources().getStringArray(
						R.array.device_setting));
		spinner.setAdapter(adapter);
	}

	@Override
	protected void onSpecialItemClicked(int position)
	{
		layout = LayoutInflater.from(this).inflate(R.layout.device_setting,
				null);

		initViewPager();
		initSpinner();

		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("常规")
				.setView(layout).setPositiveButton("确定", new ConfirmButton())
				.setNegativeButton("取消", new CancelButton()).create();

		dialog.show();
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		dialog.getWindow().setLayout((int) (size.x * 0.8),
				LayoutParams.WRAP_CONTENT);

		// dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);

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

	class ConfirmButton implements OnClickListener
	{
		@Override
		public void onClick(DialogInterface dialog, int which)
		{

		}
	}

	class CancelButton implements OnClickListener
	{
		@Override
		public void onClick(DialogInterface dialog, int which)
		{

		}
	}

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
			for (View view : list)
			{
				vPager.addView(view);
			}
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			((ViewPager) container).removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position)
		{
			Log.d("MC", position + "");

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
