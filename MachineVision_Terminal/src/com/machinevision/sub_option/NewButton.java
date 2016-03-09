package com.machinevision.sub_option;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.mikephil.charting.charts.LineChart;
import com.machinevision.common_widget.DialogWindow;
import com.machinevision.common_widget.EToast;
import com.machinevision.common_widget.MyChart;
import com.machinevision.net.CmdHandle;
import com.machinevision.net.NetUtils;
import com.machinevision.sub_option.ButtonCfgPanel.OnButtonClick;
import com.machinevision.terminal.FileDirectory;
import com.machinevision.terminal.MainActivity;
import com.machinevision.terminal.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;

import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class NewButton extends Activity implements OnButtonClick
{
	public static final String FILE_DIR = FileDirectory.getLeaningDirectory();
	private ImageView imageView;
	private LineChart mLineChart;

	private String buttonId ;
	private Bitmap bitmap;
	private ButtonProfile buttonProfile;
	private JSONObject json = new JSONObject();

	private Handler handler = new Handler() // 接收网络子线程数据并更新UI
	{
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NetUtils.MSG_NET_GET_VIDEO: // 获取图像
				bitmap = (Bitmap) msg.obj;
				imageView.setImageBitmap((Bitmap) msg.obj);
				break;
			case 0x55:
				EToast.makeText(NewButton.this, "网络未连接", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
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
					bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
				}

				int[] image = new int[len];
				int[] hist = new int[256];
				for (int i = 0; i < 256; i++) {
					hist[i] = 0;
				}

				for (int i = 0; i < len / 3; i++) {
					int r = 0, g = 0, b = 0;
					r = packageData[12 + i * 3] & 0xff;
					g = packageData[12 + i * 3 + 1] & 0xff;
					b = packageData[12 + i * 3 + 2] & 0xff;
					image[i] = (0xFF000000 | r << 16 | g << 8 | b);
					int gray = (r * 38 + g * 75 + b * 15) >> 7;
					hist[gray]++;
				}

				bitmap.setPixels(image, 0, width, 0, 0, width, height);
				imageView.setImageBitmap(bitmap);
				new MyChart(mLineChart, hist).show();
				break;
			}
		}
	};
	
	private View getPage1() 
	{
		View page1 = getLayoutInflater().inflate(R.layout.vpage1_new_button, null);
		imageView = (ImageView) page1.findViewById(R.id.machine_learning_page1_image);
		mLineChart = (LineChart) page1.findViewById(R.id.LineChart);
		Button bt_get = (Button) page1.findViewById(R.id.machine_learning_page1_get);
		bt_get.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					@Override
					public void run() 
					{
						CmdHandle cmdHandle = MainActivity.getCmdHandle(1);
						if (cmdHandle == null) 
						{
							Message message = handler.obtainMessage();
							message.what = 0x55;
							message.sendToTarget();
						} 
						else 
							cmdHandle.getVideo(handler);
					}
				}).start();
			}
		});

		Button bt_save = (Button) page1.findViewById(R.id.machine_learning_page1_save);
		bt_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bitmap == null)
					EToast.makeText(NewButton.this, "请先获取图像", Toast.LENGTH_SHORT).show();
				else {
					LayoutInflater inflater = (LayoutInflater) NewButton.this.getSystemService(LAYOUT_INFLATER_SERVICE);
					final View view = inflater.inflate(R.layout.file_rname, null);
					DialogWindow dialog_name = new DialogWindow.Builder(NewButton.this)
					.setTitle("配置文件命名")
					.setView(view)
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						buttonId = (((EditText) view.findViewById(R.id.editText1)).getText()).toString();
						String ManufatureID = ((EditText) view.findViewById(R.id.Manufacturer)).getText().toString();
						if (TextUtils.isEmpty(buttonId)|| TextUtils.isEmpty(ManufatureID)) 
						{
							EToast.makeText(NewButton.this, "输入不能为空", Toast.LENGTH_SHORT).show();
							return;
						}						
						try {json.put("Manufature", ManufatureID);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						ButtonProfile.saveInfo(buttonId, json, bitmap);
						buttonProfile = new ButtonProfile(buttonId);
						EToast.makeText(NewButton.this,"图像保存成功", Toast.LENGTH_SHORT).show();
					}
					}).setNegativeButton("取消", null).create();
				}
			}
		});

		Button bt_exit = (Button) page1.findViewById(R.id.machine_learning_page1_exit);
		bt_exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishWithAnim();
			}
		});
		return page1;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = new ButtonCfgPanel(NewButton.this, getResources().getStringArray(R.array.newbuttoninfo_config),
				getResources().getStringArray(R.array.newbuttoninfo_menu), 
				getResources().getStringArray(R.array.newbuttoninfo_type), getPage1()).getView();
		setContentView(view);
	}

	/**
	 * 关闭时触发切换动画
	 * 
	 * @author MC
	 */
	private void finishWithAnim() {
		finish();
		overridePendingTransition(0, R.anim.top_out);
	}

	@Override
	public void onPositiveButtonClicked(String[] key, String[] value) 
	{
		// TODO Auto-generated method stub
		for (int  i = 0; i < key.length;i++)
		{
			Log.d("HY", key[i]);
			Log.d("HY", value[i]);			
			try {
				json.put(key[i], value[i]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		buttonProfile.write2sd(json);
	}

	@Override
	public void onCannelButtonClicked() {
		// TODO Auto-generated method stub
		finishWithAnim();
	};

}
