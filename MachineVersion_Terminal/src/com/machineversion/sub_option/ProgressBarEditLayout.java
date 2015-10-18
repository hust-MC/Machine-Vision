package com.machineversion.sub_option;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarEditLayout extends LinearLayout
{
	Context context;
	ProgressBar progressBar;
	TextView textView;

	public ProgressBarEditLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;

		init_widget();

	}
	private void init_widget()
	{
		LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);

		progressBar
				.setScrollBarStyle(android.R.attr.progressBarStyleHorizontal);

		textView = new TextView(context);

		addView(progressBar);
		addView(textView);

	}
}
