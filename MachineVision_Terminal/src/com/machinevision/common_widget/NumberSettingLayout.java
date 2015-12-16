package com.machinevision.common_widget;

import com.machinevision.terminal.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

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

		addView(linearLayout);

		editText = (EditText) findViewById(R.id.number_setting_edittext); // 数字输入文本框
		picker = new NumberPicker(NumberSettingLayout.this.context);
		picker.setMaxValue(500); // 默认最大值

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

		editText.setFocusable(false);
		editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		editText.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					dialog.show();
					return true;
				}
				return false;
			}
		});
	}
	public void setValue(int value)
	{
		if (editText != null)
		{
			editText.setText(value + "");
		}
	}
	public int getValue()
	{
		if (editText != null)
		{
			return Integer.parseInt(editText.getText().toString());
		}
		return 0;
	}

	public void setMaxValue(int max)
	{
		if (picker != null)
		{
			picker.setMaxValue(max);
		}
	}
}
