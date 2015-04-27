package com.machineversion.option;

import com.machineversion.sub_option.OnDialogClicked;
import com.machineversion.terminal.R;

import android.os.Bundle;
import android.widget.Toast;

public class SysSettings extends ControlPannelActivity implements
		OnDialogClicked
{

	@Override
	protected void onSpecialItemClicked(int position)
	{
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		wholeMenu = new MenuWithSubMenu(R.array.option_sys_settings,
				R.array.option_sys_settings_sub,
				R.array.option_sys_settings_type);
		init_widget();
		setListViewClicked();
	}

	@Override
	public void onPositiveButtonClicked(String[] value)
	{
		Toast.makeText(this, "数据保存成功", Toast.LENGTH_SHORT).show();
	}
}
