package com.machineversion.inverter;



import com.machinevision.terminal.R;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InverterActivity extends Activity {

	Button ButtonSciInit;
	Button ButtonSciClose;
	Button mButtonReset;
	Button ButtonRun;
	Button ButtonReverse;
	Button ButtonStop;
	
	LinearLayout mBgRun;
	LinearLayout mBgReverse;
	LinearLayout mBgStop;
	
	Button ButtonSetfrqRam;
	Button ButtonSetfrqProm;	
	
	private TextView TextStatus;
	private TextView TextVelocity;
	private TextView TextCurrent;
	private TextView mTextRam;
	private TextView mTextProm;
	
	private EditText mEdtextRam;
	private EditText mEdtextProm;
		
	private SciModel sciModel;
	
	private RelativeLayout mLayoutMain;
	
	
	private int time = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inverter);
		ButtonSciInit = (Button)findViewById(R.id.Sci_init);
		ButtonSciClose = (Button)findViewById(R.id.Sci_close);
		mButtonReset = (Button)findViewById(R.id.btn_reset);
		
		ButtonRun = (Button)findViewById(R.id.button_run);
		ButtonReverse = (Button)findViewById(R.id.button_reverse);
		ButtonStop = (Button)findViewById(R.id.button_stop);
		
		TextStatus = (TextView)findViewById(R.id.text_status);
		TextVelocity = (TextView)findViewById(R.id.text_velocity);
		TextCurrent = (TextView)findViewById(R.id.text_current);
		mTextRam = (TextView)findViewById(R.id.text_ram);
		mTextProm = (TextView)findViewById(R.id.text_prom);
		
		ButtonSetfrqRam = (Button)findViewById(R.id.button_setfrq_ram);
		ButtonSetfrqProm= (Button)findViewById(R.id.button_setfrq_prom);
		
		mEdtextRam = (EditText)findViewById(R.id.edtext_ram);
		mEdtextProm = (EditText)findViewById(R.id.edtext_prom);
				
		
//		sciModel = SciModel.getInstance(this);
		
		//��edittext����ý���
		mLayoutMain = (RelativeLayout)findViewById(R.id.layout_inverter_main);
		mLayoutMain.setOnTouchListener(new OnTouchListener() {            
            public boolean onTouch(View v, MotionEvent event) {                   
            	mLayoutMain.setFocusable(true);
            	mLayoutMain.setFocusableInTouchMode(true);
            	mLayoutMain.requestFocus();
            	return false;
            }
		});
		
		mBgRun = (LinearLayout)findViewById(R.id.bg_run);
		mBgReverse = (LinearLayout)findViewById(R.id.bg_reverse);
		mBgStop = (LinearLayout)findViewById(R.id.bg_stop);
		
	}

	
	//������Ϊ͸��
	private void setButtonBgTran() {
		mBgRun.setBackgroundColor(color.transparent);
		mBgReverse.setBackgroundColor(color.transparent);
		mBgStop.setBackgroundColor(color.transparent);
	}
	
	private Handler handler = new Handler() {
		@SuppressLint("ShowToast")
		public void handleMessage(Message msg)
		{
			if(sciModel.isSciOpened()) {
				switch (msg.what)
				{
				case SciModel.Data_ACK:
					Toast.makeText(getApplicationContext(), "������ȷ", Toast.LENGTH_SHORT).show();
					mLayoutMain.setClickable(true);
					break;
				case SciModel.Data_NAK:
					String daErr = "";
					if (msg.obj != null)
						daErr = (String)msg.obj;
					Toast.makeText(getApplicationContext(), "��ݴ���Ϊ��" + daErr, Toast.LENGTH_LONG).show();
					mLayoutMain.setClickable(true);
					break;
				case SciModel.Status_Refresh:
					TextStatus.setText("����");
					break;
				case SciModel.Current_Refresh:
					TextCurrent.setText("" + (1.0f * (int)msg.arg1) /100);
					break;
				case SciModel.OutFrq_Refresh:
					TextVelocity.setText("" + (1.0f * (int)msg.arg1) /100);
					break;
				case SciModel.Frq_Ram:
					mTextRam.setText("" + (1.0f * (int)msg.arg1) /100);
					break;
				case SciModel.Frq_Prom:
					mTextProm.setText("" + (1.0f * (int)msg.arg1) /100);
					break;
				case SciModel.Alarm:
					TextStatus.setText("����");
					sciModel.setBtnClicked(SendUtils.numGetAlarm);
				case SciModel.Conn_Error:
					if(++time == 10) {
						time = 0;				
						Toast.makeText(getApplicationContext(), "���ӳ�ʱ,�������", Toast.LENGTH_SHORT).show();
					}
					mLayoutMain.setClickable(true);
					break;
				case SciModel.Get_Alarm:
					int aCode = msg.arg1;
					break;
				}
			sciModel.setSendFlag(true);
			}
		}
	};
	
	
	public void onButtonClick(View v) {
		if(sciModel.isSciOpened()) {
//			mLoading.show();
			switch (v.getId()) {
			case R.id.button_run:
				if(sciModel.getRun() < 0)
					Toast.makeText(getApplicationContext(), "����ֹͣ���л�����", Toast.LENGTH_SHORT).show();
				else {
					setButtonBgTran();
					mBgRun.setBackgroundColor(Color.BLACK);
					sciModel.setRun(1);
				}
				break;
			case R.id.button_reverse:
				if(sciModel.getRun() > 0) {
					Toast.makeText(getApplicationContext(), "����ֹͣ���л�����", Toast.LENGTH_SHORT).show();
				}
				else {
					setButtonBgTran();
					mBgReverse.setBackgroundColor(Color.BLACK);
					sciModel.setRun(-1);
				}
				break;
			case R.id.button_stop:
				setButtonBgTran();
				mBgStop.setBackgroundColor(Color.BLACK);
				sciModel.setRun(0);
				break;
			case R.id.button_setfrq_ram:
				if(TextUtils.isEmpty(mEdtextRam.getText()))
					Toast.makeText(getApplicationContext(), "����������Ƶ��", Toast.LENGTH_SHORT).show();
				else {
					int frqRam = Integer.parseInt(mEdtextRam.getText().toString());
					if(frqRam > 50)
						Toast.makeText(getApplicationContext(), "��������Ƶ��", Toast.LENGTH_SHORT).show();
					else sciModel.setFrqRam(frqRam);
				}
				break;
			case R.id.button_setfrq_prom:
				if(TextUtils.isEmpty(mEdtextProm.getText()))
					Toast.makeText(getApplicationContext(), "����������Ƶ��", Toast.LENGTH_SHORT).show();
				else {
					int frqProm = Integer.parseInt(mEdtextProm.getText().toString());
					if(frqProm > 50)
						Toast.makeText(getApplicationContext(), "��������Ƶ��", Toast.LENGTH_SHORT).show();
					else sciModel.setFrqRam(frqProm);
				}
				break;
			case R.id.imgbtn_up:
				sciModel.frqUp();
				break;
			case R.id.imgbtn_down:
				sciModel.frqDown();
				break;
			case R.id.btn_reset:
				//����Ƿ�λdialog
				showAlertDialog();
				break;
			}
			mLayoutMain.setClickable(false);
		}
		else Toast.makeText(this, "���ȿ�������", Toast.LENGTH_SHORT).show();
	}
	
	private void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ȷ����λ��");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sciModel.setBtnClicked(SendUtils.numReset);
					}
				});
		builder.setNegativeButton("ȡ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.show();
	}
	
	/*
	 * �������Ӻ���
	 */
	public void onSciClick(View view)
	{
		switch(view.getId()) {
		case R.id.Sci_init:			
			open();
			break;
		case R.id.Sci_close:
			close();
			break;
		}
	}
	
	/*************�����رյİ�����****************/
	private void open() {
		if(sciModel.isSciOpened())
			Toast.makeText(this, "�����ѿ���",
					Toast.LENGTH_SHORT).show();
		else {
//			registerBroadcast();
			sciModel.openSci();		
		}
	}
	
	private void close() {
		if(sciModel.isSciOpened()) {
//			unregisterReceiver(mBroadcastReceiver);
			sciModel.closeSci();
		}
	}
	
	public void onBackPressed() {
		close();
		super.onBackPressed();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sciModel = SciModel.getInstance(this);
		sciModel.setHandler(handler);
		if(sciModel.isSciOpened()) {
//			registerBroadcast();
			sciModel.setSendFlag(true);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(sciModel.isSciOpened()) {
//			unregisterReceiver(mBroadcastReceiver);
			//���ѯ���߳��п�������ͣ
			sciModel.setSendFlag(false);
		}
	}
}
