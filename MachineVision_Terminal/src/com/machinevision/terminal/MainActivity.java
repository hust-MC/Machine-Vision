package com.machinevision.terminal;

import java.io.IOException;
import java.text.DecimalFormat;

import com.machinevision.terminal.R;
import com.machinevision.common_widget.EToast;
import com.machinevision.common_widget.ProgressBox;
import com.machinevision.net.CmdHandle;
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
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	final int REQUEST_CODE_FILE_MANAGER = 1;
	final int REQUEST_CODE_CAMERA_PARAMS = 2;
	final int REQUEST_CODE_SYS_SETTINGS = 3;
	final int REQUEST_CODE_FASTENER_SETTINGS = 4;
	final int REQUEST_CODE_MACHINE_LEARNING = 5;
	final int REQUEST_CODE_HELP = 6;

	public static final int ERROR_MESSAGE = 0x5555;

	private boolean netFlag1 = false; // 网络连接状态标志
	private boolean netFlag2 = false; // 网络连接状态标志
	ToggleButton net_btn1; // 网络开关按钮
	ToggleButton net_btn2; // 网络开关按钮
	public static NetThread netThread1; // 创建网络线程
	public static NetThread netThread2;

	ProgressDialog dialog;

	TextView temperature_tv, qualified_tv, disqualified_tv, qualifiedRate_tv;
	ImageView photo_imv1, photo_imv2, result_imv1, result_imv2; // 图片显示区域

	Button bt_fileManager, bt_cameraParams, bt_sysSettings,
			bt_fasternerSettings, bt_machineLearning, bt_help;
	Button bt_test, bt_pause, bt_stop;

//	String sciRevBuf; // 串口接收数据
//	SciThread serialThread; // 创建串口线程

	private boolean buttonPicture = false; // 钮扣图片开关
	private boolean netHandleFlag = true;

	private static CmdHandle cmdHandle1;
	private static CmdHandle cmdHandle2;

	public static CmdHandle getCmdHandle(int num) {
		if (num == 1)
			return cmdHandle1;
		else {
			return cmdHandle2;
		}
	}

	public static void setCmdHandle(CmdHandle cmdHandle, int num) {
		if (num == 1)
			cmdHandle1 = cmdHandle;
		else 
			cmdHandle2 = cmdHandle;
	}

	/**
	 * 与网络子线程通信Handle 处理
	 * 
	 * @author MC
	 */
	@SuppressLint("HandlerLeak")
	private Handler netHandler = new Handler() // 接收网络子线程数据并更新UI
	{
		public void handleMessage(Message msg) {
			Bitmap bitmap = null;
			if (netHandleFlag) 
			{
				switch (msg.what) {
				case NetThread.CONNECT_SUCCESS: // 网络连接成功
					dialog.dismiss();
					if (msg.arg1 == 1) 
					{
						EToast.makeText(MainActivity.this, "相机1连接成功",
								Toast.LENGTH_SHORT).show();
						netFlag1 = true;
						try {
							setCmdHandle(new CmdHandle(netThread1.getSocket()), 1);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} 
					else 
					{
						EToast.makeText(MainActivity.this, "相机2连接成功",
								Toast.LENGTH_SHORT).show();
						netFlag2 = true;
							try {
								setCmdHandle(new CmdHandle(netThread2.getSocket()), 2);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
					break;
				case NetThread.CONNECT_FAIL:
					dialog.dismiss();
					if (msg.arg1 == 1) 
					{
						EToast.makeText(MainActivity.this, "相机1连接失败",
								Toast.LENGTH_SHORT).show();
						netFlag1 = false;
						net_btn1.setChecked(false);
						cmdHandle1 = null;
					} 
					else 
					{
						EToast.makeText(MainActivity.this, "相机2连接失败",
								Toast.LENGTH_SHORT).show();
						netFlag2 = false;
						net_btn2.setChecked(false);
						cmdHandle2 = null;
					}
					break;
				case NetUtils.MSG_NET_GET_VIDEO: // 获取图像
					bitmap = (Bitmap) msg.obj;
					photo_imv1.setImageBitmap(bitmap);
					break;
				case NetUtils.MSG_NET_STATE:
					String tempFloat = "." + msg.arg2 + "0";
					temperature_tv.setText("温度："
							+ msg.arg1
							+ (tempFloat.length() > 3 ? tempFloat.substring(0,
									3) : tempFloat));
					break;
				// 接收分拣结果
				case NetUtils.MSG_NET_RESULT:

					if (buttonPicture) {
						byte[] packageData = (byte[]) msg.obj;
						int[] data = new int[12];
						for (int i = 0; i < 12; i++) {
							data[i] = packageData[i] & 0xFF;
						}

						int len = data[0] | data[1] << 8 | data[2] << 16
								| data[3] << 24;
						int width = data[4] | data[5] << 8 | data[6] << 16
								| data[7] << 24;
						int height = data[8] | data[9] << 8 | data[10] << 16
								| data[11] << 24;

						if (bitmap == null || bitmap.getWidth() != width
								|| bitmap.getHeight() != height) {
							bitmap = Bitmap.createBitmap(width, height,
									Config.ARGB_8888);
						}

						int[] image = new int[len];

						for (int i = 0; i < len / 3; i++) {
							int r = 0, g = 0, b = 0;
							r = packageData[12 + i * 3] & 0xff;
							g = packageData[12 + i * 3 + 1] & 0xff;
							b = packageData[12 + i * 3 + 2] & 0xff;

							image[i] = (0xFF000000 | r << 16 | g << 8 | b);
						}
						bitmap.setPixels(image, 0, width, 0, 0, width, height);
						photo_imv1.setImageBitmap(bitmap);
					}
					Bundle bundle = msg.getData();
					boolean result = bundle.getBoolean("result");
					int qualified = bundle.getInt("qualified"); // 获取合格数
					int disQualified = bundle.getInt("disqualified"); // 获取不合格数
					DecimalFormat df = new DecimalFormat("#0.00");

					result_imv1.setImageResource(result ? R.drawable.correct
							: R.drawable.wrong);
					qualified_tv.setText("合格：" + qualified);
					disqualified_tv.setText("不合格：" + disQualified);
					qualifiedRate_tv.setText("合格率："
							+ df.format(qualified
									/ (float) (disQualified + qualified) * 100)
							+ "%");
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
	private void init_widget() {
		temperature_tv = (TextView) findViewById(R.id.tv_temperature);
		qualified_tv = (TextView) findViewById(R.id.tv_qualified);
		disqualified_tv = (TextView) findViewById(R.id.tv_disqualified);
		qualifiedRate_tv = (TextView) findViewById(R.id.tv_qualified_rate);

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
		result_imv1 = (ImageView) findViewById(R.id.main_imv_result1);
		result_imv2 = (ImageView) findViewById(R.id.main_imv_result2);

		net_btn1 = (ToggleButton) findViewById(R.id.net_link1); // 获取到控件
		net_btn1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) 
				{
					dialog = ProgressBox.show(MainActivity.this,
							"正在连接智能相机1，请稍候..."); // 进程弹窗
					netThread1 = new NetThread(netHandler, 1);
					netThread1.setName("tcp-link1");
					netThread1.start();
				} 
				else 
				{
					String toastText = "连接尚未建立";
					if (netFlag1) 
					{
						if (netThread1 != null) 
						{
							toastText = "连接已断开";
							netThread1.close();
							netThread1 = null;
						}
						netFlag1 = false;
					}
					EToast.makeText(MainActivity.this, toastText,
							Toast.LENGTH_SHORT).show();
				}
			}
		});// 添加监听事件

		net_btn2 = (ToggleButton) findViewById(R.id.net_link2); // 获取到控件
		net_btn2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 选中
					dialog = ProgressBox.show(MainActivity.this,
							"正在连接智能相机2，请稍候..."); // 进程弹窗
					netThread2 = new NetThread(netHandler, 2);
					netThread2.setName("tcp-link2");
					netThread2.start();
				} 
				else 
				{
					String toastText = "连接尚未建立";
					if (netFlag2) 
					{
						if (netThread2 != null) 
						{
							toastText = "连接已断开";
							netThread2.close();
							netThread2 = null;
						}
						netFlag2 = false;
					}
					EToast.makeText(MainActivity.this, toastText,
							Toast.LENGTH_SHORT).show();
				}
			}
		});// 添加监听事件
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.d("SYSTEM", "send Json : " + "{\"net\":{\"port\":6030}}");
		NetUtils.setIp();
		setContentView(R.layout.activity_main);
		init_widget(); // 初始化控件
	}

	@Override
	protected void onRestart() {
		netHandleFlag = true;
		if (netThread1 != null) {
			netThread1.signalThread();
		}
		if (netThread2 != null) {
			netThread2.signalThread();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		netHandleFlag = false;
		if (netThread1 != null) {
			netThread1.setCurrentState(CurrentState.onPause);
		}
		if (netThread2 != null) {
			netThread2.setCurrentState(CurrentState.onPause);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (netThread1 != null) {
			Log.d("MC", "onDestroy");
			netThread1.close();
		}
		if (netThread2 != null) {
			Log.d("MC", "onDestroy");
			netThread2.close();
		}
		super.onDestroy();
	}

	class ButtonListern implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_bt_file_manager:
				startActivityForResult(new Intent(MainActivity.this,
						FileManager.class), REQUEST_CODE_FILE_MANAGER);
				break;
			case R.id.main_bt_camera_params:
				startActivityForResult(new Intent(MainActivity.this,
						CameraParams.class), REQUEST_CODE_CAMERA_PARAMS);
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
				EToast.makeText(MainActivity.this, "选择错误", Toast.LENGTH_SHORT)
						.show();
			}
			// overridePendingTransition(R.anim.top_in, R.anim.stay_here);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * 设置成功之后处理设置的数据
		 */
		if (resultCode == RESULT_OK) {
			// netThread.signalThread();
			EToast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
			switch (requestCode) {
			default:
				break;
			}
		}
	}

	public void onClick_buttonPicture(View view) {
		buttonPicture = buttonPicture ? false : true;
	}

	/*
	 * 检查是否出错，并显示出错次数
	 */
	public void onClick_control(View view) {
		NetUtils.setIp(); // 重置IP
		switch (view.getId()) {
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

}