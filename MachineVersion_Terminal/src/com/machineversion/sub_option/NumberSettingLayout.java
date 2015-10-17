package com.machineversion.sub_option;

import com.machineversion.terminal.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumberSettingLayout extends LinearLayout
{
	TextView textView;
	Context context;
	View view;
	NumberPicker picker;

	public NumberSettingLayout(Context context)
	{
		this(context, null);
	}

	public NumberSettingLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		this.context = context;
		view = LayoutInflater.from(context).inflate(R.layout.number_setting,
				null);
		addView(view);

		textView = (TextView) findViewById(R.id.number_setting_textview);
		picker = new NumberPicker(NumberSettingLayout.this.context);
		picker.setMaxValue(500);

		textView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				AlertDialog dialog = new AlertDialog.Builder(
						NumberSettingLayout.this.context)
						.setView(picker)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog,
											int which)
									{
										// textView.setText(picker.getValue());
									}
								}).create();
				dialog.show();
			}
		});
	}

	public void setText(String text)
	{
		if (textView != null)
		{
			textView.setText(text);
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
