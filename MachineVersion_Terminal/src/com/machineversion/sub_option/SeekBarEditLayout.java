package com.machineversion.sub_option;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SeekBarEditLayout extends LinearLayout
{
	Context context;
	SeekBar seekBar;
	EditText editText;

	public SeekBarEditLayout(Context context)
	{
		this(context, null);
	}

	public SeekBarEditLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		setOrientation(LinearLayout.HORIZONTAL);
		init_widget();

	}
	private void init_widget()
	{
		seekBar = new SeekBar(context);
		editText = new EditText(context);

		LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT,
				3.5F);
		seekBar.setLayoutParams(params);
		seekBar.setMax(1000);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				editText.setText(progress + "");
			}
		});

		params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
		editText.setLayoutParams(params);
		editText.setInputType(InputType.TYPE_CLASS_PHONE);
		editText.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event)
			{
				Log.d("MC", "editorAction");
				if (seekBar != null)
				{
					seekBar.setProgress(Integer.valueOf(v.getText().toString()));
				}
				return false;
			}
		});

		addView(seekBar);
		addView(editText);
	}
	public void setText(int value)
	{
		if (editText != null)
		{
			editText.setText(value + "");
		}
	}

}
