package com.machineversion.option;

import com.machineversion.terminal.R;

import android.os.Bundle;

public class FastenerSettings extends ControlPannelActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		wholeMenu = new MenuWithSubMenu(R.array.option_fastener_settings,
				R.array.option_fastener_settings_sub,
				R.array.option_fastener_settings_type);
		init_widget();
		setListViewClicked();
	}

}
