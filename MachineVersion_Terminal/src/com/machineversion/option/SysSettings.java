package com.machineversion.option;

import com.machineversion.sub_option.DebugMode;
import com.machineversion.sub_option.DialogBuilder.OnDialogClicked;
import com.machineversion.terminal.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SysSettings extends ControlPannelActivity implements
		OnDialogClicked
{
	Spinner spinner;

	@Override
	protected void onSpecialItemClicked(int position)
	{
		View layout = LayoutInflater.from(this).inflate(
				R.layout.device_setting, null);

		spinner = (Spinner) layout.findViewById(R.id.device_setting_spinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spiner, getResources().getStringArray(
						R.array.device_setting));
		spinner.setAdapter(adapter);

		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("常规")
				.setView(layout).setPositiveButton("确定", new ConfirmButton())
				.setNegativeButton("取消", new CancelButton()).create();

		dialog.show();

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
}
