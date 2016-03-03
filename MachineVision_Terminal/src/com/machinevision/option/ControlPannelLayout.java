package com.machinevision.option;

import com.machinevision.terminal.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

public class ControlPannelLayout extends LinearLayout implements
		OnClickListener
{
	private Context context;

	ImageButton bt_top, bt_bottom, bt_right, bt_left;
	Button bt_confirm, bt_cancel;

	public ControlPannelLayout(Context context)
	{
		super(context, null);
	}

	public ControlPannelLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		// 在构造函数中将Xml中定义的布局解析出来。
		LayoutInflater.from(context).inflate(R.layout.control_pannel, this,
				true);
		init_widget();
	}

	private void init_widget()
	{
		bt_top = (ImageButton) findViewById(R.id.bt_control_pannel_top);
		bt_bottom = (ImageButton) findViewById(R.id.bt_control_pannel_bottom);
		bt_right = (ImageButton) findViewById(R.id.bt_control_pannel_right);
		bt_left = (ImageButton) findViewById(R.id.bt_control_pannel_left);
		bt_confirm = (Button) findViewById(R.id.bt_control_pannel_confirm);
		bt_cancel = (Button) findViewById(R.id.bt_control_pannel_cancel);

		bt_top.setOnClickListener(this);
		bt_bottom.setOnClickListener(this);
		bt_right.setOnClickListener(this);
		bt_left.setOnClickListener(this);
		bt_confirm.setOnClickListener(this);
		bt_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_control_pannel_left:
		case R.id.bt_control_pannel_right:
		case R.id.bt_control_pannel_top:
		case R.id.bt_control_pannel_bottom:
			((ControlPannelActivity) context).onDirectionClicked(v.getId());
			break;
		case R.id.bt_control_pannel_confirm:
		case R.id.bt_control_pannel_cancel:
			((ControlPannelActivity) context).onButtonClicked(v.getId());
			break;
		}

	}
}
