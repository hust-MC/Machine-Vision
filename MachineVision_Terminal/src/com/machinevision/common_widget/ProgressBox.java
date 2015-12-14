package com.machinevision.common_widget;

import com.machinevision.terminal.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

public class ProgressBox
{
	public static ProgressDialog show(Context context, String content)
	{
		ProgressDialog dialog = new ProgressDialog(context, R.style.dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage(content);
		dialog.setCancelable(true);
		dialog.show();
		return dialog;
	}
}
