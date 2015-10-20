package com.machineversion.sub_option;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DropDownList extends LinearLayout
{
	ClickToDropDown clickToDropDown;
	Context context;
	TextView textView;
	String title;
	OnDropListClickListener listener;
	String[] items;

	public DropDownList(Context context)
	{
		this(context, null);
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
		textView.setTextSize(20F);
		textView.setOnClickListener(new ClickToDropDown());

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.BOTTOM;
		params.topMargin = 2;

		addView(textView, params);
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setItem(String[] str)
	{
		items = str;
		setSelection(0);
	}

	/**
	 * 设置下拉菜单的当前选项
	 * 
	 * @param which
	 *            当前是第几个选项。
	 */
	public void setSelection(int which)
	{
		if (which < 0)
		{
			which = 0;
		}
		else if (which >= items.length)
		{
			which = items.length - 1;
		}

		textView.setText(items[which]);
	}

	public void setOnListClickListener(OnDropListClickListener dropListClick)
	{
		listener = dropListClick;
	}

	class ClickToDropDown implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			AlertDialog dialog = new AlertDialog.Builder(context)
					.setTitle(title).setItems(items, new onItemClick())
					.create();
			dialog.show();
		}
	}

	class onItemClick implements DialogInterface.OnClickListener
	{
		@Override
		public void onClick(DialogInterface dialog, int which)
		{

			if (textView != null)
			{
				textView.setText(items[which]);
			}
			if (listener != null)
			{
				listener.onListItemClick(DropDownList.this, which);
			}
			dialog.dismiss();
		}
	}
}
