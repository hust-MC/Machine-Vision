package com.machineversion.option;

import com.machineversion.net.CmdHandle;
import com.machineversion.sub_option.DialogBuilder.OnDialogClicked;
import com.machineversion.terminal.R;

import android.os.Bundle;

public class FastenerSettings extends ControlPannelActivity implements
		OnDialogClicked
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

	@Override
	public void onPositiveButtonClicked(String[] value)
	{
		CmdHandle cmdHandle = CmdHandle.getInstance();
		cmdHandle.normal(Integer.parseInt(value[0]));
	}
}
