package com.machinevision.inverter;

import com.machinevision.common_widget.EToast;
import com.machinevision.terminal.R;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InverterActivity extends Activity
{

	LinearLayout mBgRun;
	LinearLayout mBgStop;

	Button ButtonSetfrqRam;
	Button ButtonSetfrqProm;

	private TextView TextStatus;
	private TextView TextVelocity;
	private TextView TextCurrent;
	private TextView mTextProm;

	private EditText mEdtextRam;
	private EditText mEdtextProm;

	private TextView mTextRamSet;
	private TextView mTextPromSet;

	private SciModel sciModel;

	private RelativeLayout mLayoutMain;

	private int time = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inverter);

		TextStatus = (TextView) findViewById(R.id.text_status);
		TextVelocity = (TextView) findViewById(R.id.text_velocity);
		TextCurrent = (TextView) findViewById(R.id.text_current);
		mTextProm = (TextView) findViewById(R.id.text_prom);

		ButtonSetfrqRam = (Button) findViewById(R.id.button_setfrq_ram);
		ButtonSetfrqProm = (Button) findViewById(R.id.button_setfrq_prom);

		mEdtextRam = (EditText) findViewById(R.id.edtext_ram);
		mEdtextProm = (EditText) findViewById(R.id.edtext_prom);

		mTextRamSet = (TextView) findViewById(R.id.text_ram_set);
		mTextPromSet = (TextView) findViewById(R.id.text_prom_set);

		// 让edittext不获得焦点
		mLayoutMain = (RelativeLayout) findViewById(R.id.layout_inverter_main);
		mLayoutMain.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				mLayoutMain.setFocusable(true);
				mLayoutMain.setFocusableInTouchMode(true);
				mLayoutMain.requestFocus();
				return false;
			}
		});

		mBgRun = (LinearLayout) findViewById(R.id.bg_run);
		mBgStop = (LinearLayout) findViewById(R.id.bg_stop);

	}

	// 按键背景设为透明
	private void setButtonBgTran()
	{
		mBgRun.setBackgroundColor(color.transparent);
		mBgStop.setBackgroundColor(color.transparent);
	}

	private Handler handler = new Handler()
	{
		@SuppressLint("ShowToast")
		public void handleMessage(Message msg)
		{
			if (sciModel.isSciOpened())
			{
				switch (msg.what)
				{
				case SciModel.Data_ACK:
					EToast.makeText(getApplicationContext(), "操作正确",
							EToast.LENGTH_SHORT).show();
					mLayoutMain.setClickable(true);
					break;
				case SciModel.Data_NAK:
					String daErr = "";
					if (msg.obj != null)
						daErr = (String) msg.obj;
					EToast.makeText(getApplicationContext(), "数据错误为：" + daErr,
							EToast.LENGTH_LONG).show();
					mLayoutMain.setClickable(true);
					break;
				case SciModel.Status_Refresh:
					TextStatus.setText("正常工作");
					break;
				case SciModel.Current_Refresh:
					TextCurrent.setText("" + (1.0f * (int) msg.arg1) / 100);
					break;
				case SciModel.OutFrq_Refresh:
					TextVelocity.setText("" + (1.0f * (int) msg.arg1) / 100);
					break;
				case SciModel.Frq_Prom:
					mTextProm.setText("" + (1.0f * (int) msg.arg1) / 100);
					break;
				case SciModel.Alarm:
					TextStatus.setText("发生警报");
				case SciModel.Conn_Error:
					if (++time == 10)
					{
						time = 0;
						EToast.makeText(getApplicationContext(), "连接超时,检查连线",
								EToast.LENGTH_SHORT).show();
					}
					mLayoutMain.setClickable(true);
					break;
				case SciModel.Get_Alarm:
					int aCode = msg.arg1;
					EToast.makeText(getApplicationContext(),
							RecvUtils.alarms_title[aCode], EToast.LENGTH_LONG)
							.show();
					break;
				}
				mLayoutMain.setClickable(true);
			}
		}
	};

	public void onButtonClick(View v)
	{
		if (sciModel.isSciOpened())
		{
			switch (v.getId())
			{
			case R.id.button_run:
				setButtonBgTran();
				mBgRun.setBackgroundColor(Color.BLACK);
				sciModel.send(SendUtils.run);
				break;
			case R.id.button_stop:
				setButtonBgTran();
				mBgStop.setBackgroundColor(Color.BLACK);
				sciModel.send(SendUtils.stop);
				break;
			case R.id.button_setfrq_ram:
				if (TextUtils.isEmpty(mEdtextRam.getText()))
					EToast.makeText(getApplicationContext(), "请输入设置频率",
							EToast.LENGTH_SHORT).show();
				else
				{
					int frqRam = Integer.parseInt(mEdtextRam.getText()
							.toString());
					if (frqRam > 50)
						EToast.makeText(getApplicationContext(), "超过限制频率",
								EToast.LENGTH_SHORT).show();
					else
					{
						mEdtextRam.setText("");
						mTextRamSet.setText("设定值：" + frqRam);
						sciModel.setFrqRam(frqRam);
					}
				}
				break;
			case R.id.button_setfrq_prom:
				if (TextUtils.isEmpty(mEdtextProm.getText()))
					EToast.makeText(getApplicationContext(), "请输入设置频率",
							EToast.LENGTH_SHORT).show();
				else
				{
					int frqProm = Integer.parseInt(mEdtextProm.getText()
							.toString());
					if (frqProm > 50)
						EToast.makeText(getApplicationContext(), "超过限制频率",
								EToast.LENGTH_SHORT).show();
					else
					{
						mEdtextProm.setText("");
						mTextPromSet.setText("设定值：" + frqProm);
						sciModel.setFrqProm(frqProm);
					}
				}
				break;
			case R.id.imgbtn_up:
				sciModel.frqUp();
				break;
			case R.id.imgbtn_down:
				sciModel.frqDown();
				break;
			}
			mLayoutMain.setClickable(false);
		}
		else
			EToast.makeText(this, "请先开启串口", EToast.LENGTH_SHORT).show();
	}

	private void showAlertDialog()
	{
		ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(
				InverterActivity.this, R.style.dialog);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				contextThemeWrapper);
		builder.setTitle("确定复位吗？");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				sciModel.send(SendUtils.reset);
			}
		});
		builder.setPositiveButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
		builder.show();
	}

	/*
	 * 串口连接函数
	 */
	public void onBtnClick(View view)
	{
		switch (view.getId())
		{
		case R.id.btn_reset:
			// 跳出是否复位dialog
			showAlertDialog();
			break;
		case R.id.btn_back:
			onBackPressed();
			break;
		}
	}

	public void onBackPressed()
	{
		close();
		super.onBackPressed();
	}

	private void close()
	{
		if (sciModel.isSciOpened())
		{
			sciModel.closeSci();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		if (sciModel == null)
		{
			sciModel = SciModel.getInstance(this);
			sciModel.setHandler(handler);
		}
		if (!sciModel.isSciOpened())
		{
			// 开启应用，即时开启串口
			sciModel.openSci();
		}
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
	}
}
