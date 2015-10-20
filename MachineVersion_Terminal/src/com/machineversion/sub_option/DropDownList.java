package com.machineversion.sub_option;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DropDownList extends LinearLayout
{
	ClickToDropDown clickToDropDown;
	Context context;
	TextView textView;
	String title;
	String[] items;

	public DropDownList(Context context)
	{
		this(context, (AttributeSet) null);
	}

	public DropDownList(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;

		init_widget();
	}

	private void init_widget()
	{
		textView = new TextView(context);
		textView.setOnClickListener(new ClickToDropDown());
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setItem(String[] str)
	{
		items = str;
	}

	class ClickToDropDown implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			AlertDialog dialog = new AlertDialog.Builder(context)
					.setTitle(title).setItems(items, null).create();
			dialog.show();
		}
	}
}
