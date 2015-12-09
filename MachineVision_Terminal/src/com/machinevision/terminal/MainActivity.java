package com.machinevision.terminal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.machinevision.terminal.R;
import com.machinevision.net.NetUtils;
import com.machinevision.option.CameraParams;
import com.machinevision.option.FastenerSettings;
import com.machinevision.option.FileManager;
import com.machinevision.option.Help;
import com.machinevision.option.MachineLearning;
import com.machinevision.option.SysSettings;
import com.machinevision.terminal.NetThread.CurrentState;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity
{
	final int REQUEST_CODE_FILE_MANAGER = 1;
	final int REQUEST_CODE_CAMERA_PARAMS = 2;
	final int REQUEST_CODE_SYS_SETTINGS = 3;
	final int REQUEST_CODE_FASTENER_SETTINGS = 4;
	final int REQUEST_CODE_MACHINE_LEARNING = 5;
	final int REQUEST_CODE_HELP = 6;

	static boolean netFlag = false; 						// 网络连接状态标志
	static ProgressDialog dialog;

	TextView temperature_tv;
	ToggleButton net_btn;									// 网络开关按钮
	ToggleButton sci_btn; 									// 串口开关按钮
	ImageView photo_imv1, photo_imv2;						// 图片显示区域

	Button bt_fileManager, bt_cameraParams, bt_sysSettings,
			bt_fasternerSettings, bt_machineLearning, bt_help;
	Button bt_test, bt_pause, bt_stop;

	String sciRevBuf;							// 串口接收数据

	SciThread serialThread;						// 创建串口线程
	NetThread netThread;						// 创建网络线程

	private boolean netHandleFlag = true;

	/*
	 * 与串口子线程通信函数
	 */
	@SuppressLint("HandlerLeak")
	private Handler sciHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 0x55)				// 等于0x55说明串口权限不对
			{
				Toast.makeText(MainActivity.this, "尚未获取串口权限",
						Toast.LENGTH_SHORT).show();
				sci_btn.setChecked(false);
			}
			else if (msg.obj != null)			// 正常接收数据
			{
				Toast.makeText(MainActivity.this,
						getResources().getString(R.string.openSCI_sucssess),
						Toast.LENGTH_SHORT).show();
				sciRevBuf = (String) msg.obj + "back";
				serialThread.send(sciRevBuf.getBytes(), 1);
			}
			else
			{
				Toast.makeText(MainActivity.this, "本地串口不存在", Toast.LENGTH_SHORT)
						.show();
				sci_btn.setChecked(false);
			}
		}
	};

	/**
	 * 与网络子线程通信Handler
	 * 
	 * @author MC
	 */
	@SuppressLint("HandlerLeak")
	private Handler netHandler = new Handler()		    // 接收网络子线程数据并更新UI
	{
		@SuppressLint("ShowToast")
		public void handleMessage(Message msg)
		{
			if (netHandleFlag)
			{
				switch (msg.what)
				{
				case NetThread.CONNECT_SUCCESS: // 网络连接成功
					dialog.dismiss();
					Toast.makeText(MainActivity.this, "网络连接成功",
							Toast.LENGTH_SHORT).show();
					break;
				case NetThread.CONNECT_FAIL:
					dialog.dismiss();
					Toast.makeText(MainActivity.this, "连接失败，请检查网络连接",
							Toast.LENGTH_SHORT).show();
					break;
				case NetUtils.MSG_NET_GET_VIDEO: // 获取图像
					Bitmap bitmap = (Bitmap) msg.obj;
					photo_imv1.setImageBitmap(bitmap);
					break;
				case NetUtils.MSG_NET_STATE:
					temperature_tv.setText("温度：" + msg.arg1 + "." + msg.arg2);
					break;
				default:
					break;
				}
			}
		}
	};

	/**
	 * 初始化控件
	 * 
	 * @author MC
	 */
	private void init_widgit()
	{
		temperature_tv = (TextView) findViewById(R.id.tv_temperature);

		bt_fileManager = (Button) findViewById(R.id.main_bt_file_manager);
		bt_cameraParams = (Button) findViewById(R.id.main_bt_camera_params);
		bt_sysSettings = (Button) findViewById(R.id.main_bt_sys_settings);
		bt_fasternerSettings = (Button) findViewById(R.id.main_bt_fastener_settings);
		bt_machineLearning = (Button) findViewById(R.id.main_bt_machine_learning);
		bt_help = (Button) findViewById(R.id.main_bt_help);
		bt_test = (Button) findViewById(R.id.bt_control_test);
		bt_pause = (Button) findViewById(R.id.bt_control_pause);
		bt_stop = (Button) findViewById(R.id.bt_control_stop);

		ButtonListern buttonListern = new ButtonListern();
		bt_fileManager.setOnClickListener(buttonListern);
		bt_cameraParams.setOnClickListener(buttonListern);
		bt_sysSettings.setOnClickListener(buttonListern);
		bt_fasternerSettings.setOnClickListener(buttonListern);
		bt_machineLearning.setOnClickListener(buttonListern);
		bt_help.setOnClickListener(buttonListern);

		photo_imv1 = (ImageView) findViewById(R.id.main_imv_photo1);
		photo_imv2 = (ImageView) findViewById(R.id.main_imv_photo2);
	}
	public static final String read(InputStream in) throws IOException
	{
		StringBuilder sb = new StringBuilder();

		int ch;

		while (-1 != (ch = in.read()))
			sb.append((char) ch);

		return sb.toString();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setIp(NetUtils.ip);

		setContentView(R.layout.activity_main);
		init_widgit(); // 初始化控件
	}
	@Override
	protected void onRestart()
	{
		netHandleFlag = true;
		if (netThread != null)
		{
			netThread.signalThread();
		}
		super.onResume();
	}
	@Override
	protected void onPause()
	{
		netHandleFlag = false;
		if (netThread != null)
		{
			netThread.setCurrentState(CurrentState.onPause);
		}
		super.onPause();
	}
	@Override
	protected void onDestroy()
	{
		if (netThread != null)
		{
			Log.d("MC", "onDestroy");
			netThread.close();
		}
		super.onDestroy();
	}

	class ButtonListern implements android.view.View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (netThread != null)
			{
				netThread.setCurrentState(CurrentState.onPause);
			}
			netHandler.removeMessages(NetUtils.MSG_NET_GET_VIDEO);
			switch (v.getId())
			{
			case R.id.main_bt_file_manager:
				startActivityForResult(new Intent(MainActivity.this,
						FileManager.class), REQUEST_CODE_FILE_MANAGER);
				break;
			case R.id.main_bt_camera_params:
				startActivityForResult(new Intent(MainActivity.this,
						CameraParams.class), REQUEST_CODE_CAMERA_PARAMS);
				Log.d("ZY", "button2");
				break;
			case R.id.main_bt_sys_settings:
				startActivityForResult(new Intent(MainActivity.this,
						SysSettings.class), REQUEST_CODE_SYS_SETTINGS);
				break;
			case R.id.main_bt_fastener_settings:
				startActivityForResult(new Intent(MainActivity.this,
						FastenerSettings.class), REQUEST_CODE_FASTENER_SETTINGS);
				break;
			case R.id.main_bt_machine_learning:
				startActivityForResult(new Intent(MainActivity.this,
						MachineLearning.class), REQUEST_CODE_MACHINE_LEARNING);
				break;
			case R.id.main_bt_help:
				startActivityForResult(
						new Intent(MainActivity.this, Help.class),
						REQUEST_CODE_HELP);
				break;
			default:
				Toast.makeText(MainActivity.this, "选择错误", Toast.LENGTH_SHORT)
						.show();
			}
			// overridePendingTransition(R.anim.top_in, R.anim.stay_here);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		/*
		 * 设置成功之后处理设置的数据
		 */
		if (resultCode == RESULT_OK)
		{
			// netThread.signalThread();
			Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
			switch (requestCode)
			{
			default:
				break;
			}
		}
	}

	/*
	 * 检查是否出错，并显示出错次数
	 */
	public void onClick_control(View view)
	{
		// bt_check.setText(DataPack.timeoutCount + "");

		switch (view.getId())
		{
		case R.id.bt_control_test:
			bt_test.setBackgroundColor(Color.GREEN);
			bt_pause.setBackgroundResource(R.drawable.bg_control_bt);
			bt_stop.setBackgroundResource(R.drawable.bg_control_bt);
			break;

		case R.id.bt_control_pause:
			bt_test.setBackgroundResource(R.drawable.bg_control_bt);
			bt_pause.setBackgroundColor(Color.GREEN);
			bt_stop.setBackgroundResource(R.drawable.bg_control_bt);
			break;
		case R.id.bt_control_stop:
			bt_test.setBackgroundResource(R.drawable.bg_control_bt);
			bt_pause.setBackgroundResource(R.drawable.bg_control_bt);
			bt_stop.setBackgroundColor(Color.GREEN);
			break;
		}

	}
	/**
	 * 网络连接函数
	 * 
	 * @param view
	 *            连接的按钮对象
	 */
	public void onClick_net(View view)
	{
		dialog = ProgressDialog
				.show(this, null, "正在连接智能相机，请稍候...", true, false); // 进程弹窗

		netThread = new NetThread(netHandler);
		netThread.start();
	}

	public void onClick_close_net(View view)
	{
		String toastText = "连接尚未建立";
		if (netThread != null)
		{
			toastText = "连接已断开";
			netThread.close();
			netThread = null;
		}
		Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
	}

	/*
	 * 串口连接函数
	 */
	public void onClick_Sci(View view)
	{
		if (new File(SciThread.SERIAL_PORT).exists())
		{
			if (sci_btn.isChecked())
			{
				serialThread = new SciThread(sciHandler);
				serialThread.open();
			}
			else
			{
				serialThread.close();
				Toast.makeText(this, getString(R.string.closeSCI_sucssess),
						Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, "本地串口不存在", Toast.LENGTH_SHORT).show();
			sci_btn.setChecked(false);
		}
	}

	/*
	 * 电机控制函数
	 */
	public void onClick_motor(View view)
	{
		// switch (view.getId())
		// {
		// case R.id.motor_off:
		// serialThread.send("21232".getBytes(), 1);
		// break;
		//
		// case R.id.motor_on:
		// // serialThread.send(data, cmd);
		// break;
		// }
	}
}