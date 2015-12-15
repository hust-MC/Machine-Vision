package com.machinevision.common_widget;

import com.machinevision.terminal.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class DialogWindow extends AlertDialog
{
	Context context;

	protected DialogWindow(Context context)
	{
		super(context);
		this.context = context;
	}

	@Override
	public void show()
	{
		super.show();
		getWindow().setLayout(600, LayoutParams.WRAP_CONTENT);
		getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(27F);
		getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(27F);
	}

	public static class DialogWindowBuilder extends AlertDialog.Builder
	{
		Context context;

		public DialogWindowBuilder(Context context)
		{
			super(context);
			this.context = context;
		}

		@Override
		public Builder setTitle(CharSequence title)
		{
			View titleLayout = LayoutInflater.from(context).inflate(
					R.layout.alert_dialog_tv, null);
			((TextView) titleLayout.findViewById(R.id.alert_dialog_tv))
					.setText(title);
			return super.setCustomTitle(titleLayout);
		}

		@Override
		public Builder setTitle(int titleId)
		{
			return setTitle(context.getResources().getString(titleId));
		}

	}

}
