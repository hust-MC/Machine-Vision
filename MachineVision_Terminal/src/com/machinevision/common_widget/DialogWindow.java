package com.machinevision.common_widget;

import com.machinevision.terminal.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 自定义对话框形式，继承自系统提示对话框AlertDialog，这里对其进行了定制，作用是生成软件中默认的对话框
 * @author M
 *
 */
public class DialogWindow extends AlertDialog
{
	Context context;
	AlertDialog alertDialog;

	private DialogWindow(Context context)
	{
		super(context);
		this.context = context;
	}

	/**
	 * 显示对话框。调用此函数默认宽为自定义600，适用于对话框内容比较宽的场景。
	 */
	@Override
	public void show()
	{
		super.show();
		getWindow().setLayout(600, LayoutParams.WRAP_CONTENT);
		getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(27F);
		getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(27F);
	}

	/**
	 * 显示对话框。调用此函数宽高均为系统自动适应内容模式，适用于对话框内容比较窄的场景。
	 */
	public void showWrapContent()
	{
		super.show();
		getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(27F);
		getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(27F);
	}

	/**
	 * 设置弹窗的宽度，此时高度默认为wrap_content
	 * 
	 * @param width
	 *            弹窗的宽度大小
	 */
	public void setWindowWidth(int width)
	{
		setWindowSize(width, LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 设置弹窗的宽度和高度
	 * 
	 * @param width
	 *            弹窗的宽度
	 * @param height
	 *            弹窗的高度
	 */
	public void setWindowSize(int width, int height)
	{
		getWindow().setLayout(width, height);
	}

	/**
	 * 对话框创建器
	 * @author M
	 *
	 */
	public static class Builder
	{
		Context context;
		DialogWindow dialogWindow;

		public Builder(Context context)
		{
			this.context = context;
			dialogWindow = new DialogWindow(context);
		}

		public Builder setTitle(CharSequence title)
		{
			View titleLayout = LayoutInflater.from(context).inflate(
					R.layout.alert_dialog_tv, null);
			((TextView) titleLayout.findViewById(R.id.alert_dialog_tv))
					.setText(title);
			dialogWindow.setCustomTitle(titleLayout);
			return this;
		}

		public Builder setView(View view)
		{
			dialogWindow.setView(view);
			return this;
		}
		public Builder setPositiveButton(String text, OnClickListener listener)
		{
			dialogWindow.setButton(text, listener);
			return this;
		}
		public Builder setNegativeButton(String text, OnClickListener listener)
		{
			if (listener == null)
			{
				listener = new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
					}
				};
			}
			dialogWindow.setButton2(text, listener);
			return this;
		}
		public DialogWindow create()
		{
			return dialogWindow;
		}
	}
}
