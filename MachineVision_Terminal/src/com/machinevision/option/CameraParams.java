package com.machinevision.option;

import com.machinevision.terminal.R;
import com.machinevision.common_widget.EToast;
import com.machinevision.net.NetUtils;
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
				R.array.option_camera_params_type, 0);

		init_widget();
		setListViewClicked();
	}

	@Override
	public void onPositiveButtonClicked(String[] value, int position)
	{
		switch (position)
		{
		case 0:
			break;
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
					EToast.makeText(this, "输入的IP有误", Toast.LENGTH_SHORT);
				}
			} catch (NumberFormatException e)
			{
				EToast.makeText(this, "输入的IP有误", Toast.LENGTH_SHORT);
				Log.e("MC", e.getMessage());
			}
		default:
			break;
		}
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
