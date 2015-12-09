package com.machineversion.option;

import com.machineversion.option.ControlPannelActivity.MenuWithSubMenu;
import com.machineversion.terminal.R;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class Help extends ControlPannelActivity
{
	@Override
	protected void init_widget()
	{
		super.init_widget();
		listview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		wholeMenu = new MenuWithSubMenu(R.array.option_help, 0);
		init_widget();
	}
}
