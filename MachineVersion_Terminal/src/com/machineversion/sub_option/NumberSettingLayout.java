package com.machineversion.sub_option;

import com.machineversion.terminal.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumberSettingLayout extends LinearLayout
{
	EditText editText;
	Context context;
	LinearLayout linearLayout;
	NumberPicker picker;
	AlertDialog dialog;

	public NumberSettingLayout(Context context)
	{
		this(context, null);
	}

	public NumberSettingLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		this.context = context;
		linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.number_setting, null);

		// linearLayout.setLayoutParams(params);

		addView(linearLayout);

		editText = (EditText) findViewById(R.id.number_setting_textview);		// 数字输入文本框
		picker = new NumberPicker(NumberSettingLayout.this.context);
		picker.setMaxValue(500);			// 默认最大值

		dialog = new AlertDialog.Builder(NumberSettingLayout.this.context)
				.setView(picker)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						editText.setText(picker.getValue() + "");
					}
				}).create();

		editText.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				dialog.show();
			}
		});
	}
	public void setText(String text)
	{
		if (editText != null)
		{
			editText.setText(text);
		}
	}

	public void setMaxValue(int max)
	{
		if (picker != null)
		{
			picker.setMaxValue(max);
		}
	}
}
