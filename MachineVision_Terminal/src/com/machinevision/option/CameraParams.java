package com.machinevision.option;

import com.machinevision.terminal.EToast;
import com.machinevision.terminal.R;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;

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
				R.array.option_camera_params_type,0);

		init_widget();
		setListViewClicked();
		Log.d("ZY", "B : onCreate");
	}

	@Override
	public void onPositiveButtonClicked(String[] value)
	{
		EToast.makeText(this, "数据保存成功", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onSpecialItemClicked(int position)
	{
		switch (position)
		{
		case 5:
			EToast.makeText(this, "设置文件已保存", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
}
