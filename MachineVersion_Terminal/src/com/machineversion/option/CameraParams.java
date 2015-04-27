package com.machineversion.option;

import com.machineversion.sub_option.OnDialogClicked;
import com.machineversion.terminal.R;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class CameraParams extends ControlPannelActivity implements
		OnDialogClicked
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		wholeMenu = new MenuWithSubMenu(R.array.option_camera_params,
				R.array.option_camera_params_sub,
				R.array.option_camera_params_type);

		init_widget();
		setListViewClicked();
	}

	@Override
	public void onPositiveButtonClicked(String[] value)
	{
		Toast.makeText(this, "数据保存成功", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onSpecialItemClicked(int position)
	{
		switch (position)
		{
		case 5:
			Toast.makeText(this, "设置文件已保存", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
}
