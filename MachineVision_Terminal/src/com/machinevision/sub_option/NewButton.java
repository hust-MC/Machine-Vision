package com.machinevision.sub_option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.machinevision.common_widget.DialogWindow;
import com.machinevision.common_widget.EToast;
import com.machinevision.net.CmdHandle;
import com.machinevision.net.NetUtils;
import com.machinevision.terminal.FileDirectory;
import com.machinevision.terminal.MainActivity;
import com.machinevision.terminal.NetThread;
import com.machinevision.terminal.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class NewButton extends Activity {
	public static final String FILE_DIR = FileDirectory.getAppDirectory()
			+ "Learning/";

	private ViewPager viewPager;

	private ImageView imageView;
	private LineChart mLineChart;

	private static String ButtonID = null;
	private static String ManufatureID = null;

	public static Bitmap fullImage = null;

	private List<View> lists = new ArrayList<View>();
	private ViewPagerAdapter adapter;
	private int currentItem;

	private TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private TextView textView4;
	private TextView textView5;

	// 保存动态Views
	private View[] viewArr2;
	private View[] viewArr4;
	private View[] viewArr5;

	private String[] value2;
	private String[] value4;
	private String[] value5;

	private String[] menu;
	private String[] type;

	private JSONObject json = null;

	private Handler handler = new Handler() // 接收网络子线程数据并更新UI
	{
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NetUtils.MSG_NET_GET_VIDEO: // 获取图像
				fullImage = (Bitmap) msg.obj;
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

				if (fullImage == null || fullImage.getWidth() != width
						|| fullImage.getHeight() != height) {
					fullImage = Bitmap.createBitmap(width, height,
							Config.ARGB_8888);
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

				fullImage.setPixels(image, 0, width, 0, 0, width, height);
				imageView.setImageBitmap(fullImage);
				new MyChart(mLineChart, hist).show();

				break;
			}
		}
	};

	private View getPage1() {
		View page1 = getLayoutInflater().inflate(R.layout.vpage1_new_button,
				null);
		imageView = (ImageView) page1
				.findViewById(R.id.machine_learning_page1_image);
		mLineChart = (LineChart) page1.findViewById(R.id.LineChart);
		Button bt_get = (Button) page1
				.findViewById(R.id.machine_learning_page1_get);
		bt_get.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					@Override
					public void run() {
						CmdHandle cmdHandle = null;
						if (MainActivity.netThread1 != null)
							cmdHandle = MainActivity.getCmdHandle(1);
						else {
							cmdHandle = MainActivity.getCmdHandle(2);							
						}
						if (cmdHandle == null) {
							Message message = handler.obtainMessage();
							message.what = 0x55;
							message.sendToTarget();
						} else {
							cmdHandle.getVideo(handler);
							
						}
					}
				}).start();
			}
		});

		Button bt_save = (Button) page1
				.findViewById(R.id.machine_learning_page1_save);
		bt_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fullImage == null)
					EToast.makeText(NewButton.this, "请先获取图像",
							Toast.LENGTH_SHORT).show();
				else {
					LayoutInflater inflater = (LayoutInflater) NewButton.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
					final View view = inflater.inflate(R.layout.file_rname,
							null);
					DialogWindow dialog_name = new DialogWindow.Builder(
							NewButton.this)
							.setTitle("配置文件命名")
							.setView(view)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											EditText nameEditText = (EditText) view
													.findViewById(R.id.editText1);
											EditText Manufature = (EditText) view
													.findViewById(R.id.Manufacturer);
											ButtonID = nameEditText.getText()
													.toString();
											ManufatureID = Manufature.getText()
													.toString();

											if (TextUtils.isEmpty(ButtonID)
													|| TextUtils
															.isEmpty(ManufatureID)) {
												EToast.makeText(NewButton.this,
														"输入不能为空",
														Toast.LENGTH_SHORT)
														.show();
												return;
											}
											try {
												json.put("Manufature",
														ManufatureID);
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
											saveMyBitmap(fullImage, ButtonID);
											write2sd(json, ButtonID);
											EToast.makeText(NewButton.this,
													"图像保存成功",
													Toast.LENGTH_SHORT).show();
										}
									}).setNegativeButton("取消", null).create();
					dialog_name.showWrapContent();
				}
			}
		});

		Button bt_exit = (Button) page1
				.findViewById(R.id.machine_learning_page1_exit);
		bt_exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishWithAnim();
			}
		});

		return page1;
	}

	public static String getImgPath(String id) {
		return FILE_DIR + id + "/" + id + ".jpg";
	}

	public static String getIniFile(String id) {
		return FILE_DIR + id + "/" + id + ".ini";
	}

	public static void saveMyBitmap(Bitmap mBitmap, String bitName) {
		File f = new File(getImgPath(bitName));
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void write2sd(JSONObject json, String ButtonID) {
		try {
			FileWriter writer = new FileWriter(getIniFile(ButtonID));
			if (json == null)
				writer.write("");
			else
				writer.write(json.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static LinearLayout getTopView(Context context, String menu,
			String strType, View[] viewsTop) {

		LinearLayout layoutTop = new LinearLayout(context);
		layoutTop.setOrientation(LinearLayout.VERTICAL);

		String[] contents = menu.split(",");
		String[] type = strType.split(",");

		if (viewsTop.length != contents.length) {
			EToast.makeText(context, "长度不等", Toast.LENGTH_SHORT).show();
			return null;
		}

		for (int i = 0; i < contents.length; i++) {
			LinearLayout subLayout = new LinearLayout(context);
			subLayout.setOrientation(LinearLayout.HORIZONTAL);
			subLayout.setGravity(Gravity.CENTER_HORIZONTAL);

			LayoutParams params1 = new LayoutParams(250,
					LayoutParams.WRAP_CONTENT, 0);
			/*
			 * 创建一个文本框并设置参数
			 */
			TextView tv = new TextView(context);
			tv.setLayoutParams(params1);
			tv.setText(contents[i] + "：");
			tv.setTextSize(27F);

			LayoutParams params2 = new LayoutParams(150,
					LayoutParams.WRAP_CONTENT, 0);
			if (type[i].startsWith("0")) {
				viewsTop[i] = new EditText(context);

				((EditText) viewsTop[i]).setTextSize(25F);
				((EditText) viewsTop[i])
						.setBackgroundResource(android.R.drawable.edit_text);
			} else {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context, R.layout.spiner, type[i].substring(1).split(
								"n"));
				viewsTop[i] = new Spinner(context);

				((Spinner) viewsTop[i]).setAdapter(adapter);
			}

			viewsTop[i].setLayoutParams(params2);
			viewsTop[i].setPadding(8, 2, 5, 2);
			subLayout.addView(tv);
			subLayout.addView(viewsTop[i]);

			layoutTop.addView(subLayout);
		}

		return layoutTop;
	}

	void setStyle(Button button, CharSequence text, LayoutParams params) {
		button.setWidth(150);
		button.setHeight(100);
		button.setText(text);
		button.setTextSize(30);
		button.setLayoutParams(params);
		button.setBackgroundResource(R.drawable.bg_control_bt);
	}

	private View getPage2(String Menu, String strType) {
		LinearLayout layoutOuter = new LinearLayout(this);
		layoutOuter.setOrientation(LinearLayout.VERTICAL);
		layoutOuter.setPadding(10, 50, 40, 20);
		LinearLayout layoutTop = getTopView(this, Menu, strType, viewArr2);
		LinearLayout layoutBottom = new LinearLayout(this);
		layoutBottom.setOrientation(LinearLayout.HORIZONTAL);

		/**
		 * 定义下排2个控件
		 */
		View[] viewsBottom = new View[2];
		LayoutParams paramsBt0 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		paramsBt0.setMargins(250, 0, 0, 0);
		viewsBottom[0] = new Button(this);
		setStyle(((Button) viewsBottom[0]), "保存", paramsBt0);

		LayoutParams paramsBt1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		viewsBottom[1] = new Button(this);
		paramsBt1.setMargins(200, 0, 0, 0);
		setStyle(((Button) viewsBottom[1]), "退出", paramsBt1);

		layoutBottom.addView(viewsBottom[0]);
		layoutBottom.addView(viewsBottom[1]);

		((Button) viewsBottom[0])
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (ButtonID == null)
							EToast.makeText(NewButton.this, "样本图片未保存",
									Toast.LENGTH_SHORT).show();
						else {
							saveInfomation(json, menu[0].split(","),
									type[0].split(","), viewArr2, value2);
							write2sd(json, ButtonID);
							EToast.makeText(NewButton.this, "保存成功",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		((Button) viewsBottom[1])
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						EToast.makeText(NewButton.this, "退出成功",
								Toast.LENGTH_SHORT).show();
						finishWithAnim();
					}
				});

		LayoutParams paramTop = new LayoutParams(LayoutParams.MATCH_PARENT, 0,
				4);
		LayoutParams paramBottom = new LayoutParams(LayoutParams.MATCH_PARENT,
				0, 1);
		layoutOuter.addView(layoutTop, paramTop);
		layoutOuter.addView(layoutBottom, paramBottom);
		return layoutOuter;
	}

	private View getPage3() {
		View page3 = getLayoutInflater().inflate(R.layout.vpage3_new_button,
				null);
		return page3;
	}

	private View getPage4(String Menu, String strType) {
		LinearLayout layoutOuter = new LinearLayout(this);
		layoutOuter.setOrientation(LinearLayout.VERTICAL);
		layoutOuter.setPadding(10, 50, 40, 20);

		LinearLayout layoutTop = getTopView(this, Menu, strType, viewArr4);
		LinearLayout layoutBottom = new LinearLayout(this);
		layoutBottom.setOrientation(LinearLayout.HORIZONTAL);

		/**
		 * 定义下排2个控件
		 */
		View[] viewsBottom = new View[2];
		LayoutParams paramsBt0 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		paramsBt0.setMargins(250, 0, 0, 0);
		viewsBottom[0] = new Button(this);
		setStyle(((Button) viewsBottom[0]), "保存", paramsBt0);

		LayoutParams paramsBt1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		viewsBottom[1] = new Button(this);
		paramsBt1.setMargins(200, 0, 0, 0);
		setStyle(((Button) viewsBottom[1]), "退出", paramsBt1);

		layoutBottom.addView(viewsBottom[0]);
		layoutBottom.addView(viewsBottom[1]);

		((Button) viewsBottom[0])
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (ButtonID == null)
							EToast.makeText(NewButton.this, "样本图片未保存",
									Toast.LENGTH_SHORT).show();
						else {
							saveInfomation(json, menu[1].split(","),
									type[1].split(","), viewArr2, value2);
							write2sd(json, ButtonID);
							EToast.makeText(NewButton.this, "保存成功",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		((Button) viewsBottom[1])
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						EToast.makeText(NewButton.this, "退出成功",
								Toast.LENGTH_SHORT).show();
						finishWithAnim();
					}
				});

		LayoutParams paramTop = new LayoutParams(LayoutParams.MATCH_PARENT, 0,
				4);
		LayoutParams paramBottom = new LayoutParams(LayoutParams.MATCH_PARENT,
				0, 1);
		layoutOuter.addView(layoutTop, paramTop);
		layoutOuter.addView(layoutBottom, paramBottom);
		return layoutOuter;
	}

	private View getPage5(String Menu, String strType) {
		LinearLayout layoutOuter = new LinearLayout(this);
		layoutOuter.setOrientation(LinearLayout.VERTICAL);
		layoutOuter.setPadding(10, 50, 40, 20);
		LayoutParams paramOuter = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, 0);
		layoutOuter.setLayoutParams(paramOuter);

		LinearLayout layoutTop = getTopView(this, Menu, strType, viewArr5);
		LinearLayout layoutBottom = new LinearLayout(this);
		layoutBottom.setOrientation(LinearLayout.HORIZONTAL);

		/**
		 * 定义下排2个控件
		 */
		View[] viewsBottom = new View[2];
		LayoutParams paramsBt0 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		paramsBt0.setMargins(250, 0, 0, 0);
		viewsBottom[0] = new Button(this);
		setStyle(((Button) viewsBottom[0]), "保存", paramsBt0);

		LayoutParams paramsBt1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		viewsBottom[1] = new Button(this);
		paramsBt1.setMargins(200, 0, 0, 0);
		setStyle(((Button) viewsBottom[1]), "退出", paramsBt1);

		layoutBottom.addView(viewsBottom[0]);
		layoutBottom.addView(viewsBottom[1]);
		((Button) viewsBottom[0])
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (ButtonID == null)
							EToast.makeText(NewButton.this, "样本图片未保存",
									Toast.LENGTH_SHORT).show();
						else {
							saveInfomation(json, menu[2].split(","),
									type[2].split(","), viewArr5, value5);
							write2sd(json, ButtonID);
							EToast.makeText(NewButton.this, "保存成功",
									Toast.LENGTH_SHORT).show();
						}

					}
				});

		((Button) viewsBottom[1])
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						EToast.makeText(NewButton.this, "退出成功",
								Toast.LENGTH_SHORT).show();
						finishWithAnim();
					}
				});
		LayoutParams paramTop = new LayoutParams(LayoutParams.MATCH_PARENT, 0,
				4);
		LayoutParams paramBottom = new LayoutParams(LayoutParams.MATCH_PARENT,
				0, 1);
		layoutOuter.addView(layoutTop, paramTop);
		layoutOuter.addView(layoutBottom, paramBottom);
		return layoutOuter;

	}

	public static void saveInfomation(JSONObject json, String[] keys,
			String[] type, View[] viewArr, String[] values) {

		for (int i = 0; i < viewArr.length; i++) {
			if (viewArr[i] instanceof EditText) {
				values[i] = ((EditText) viewArr[i]).getText().toString();
			} else if (viewArr[i] instanceof Spinner) {
				values[i] = String.valueOf(((Spinner) viewArr[i])
						.getSelectedItemPosition());
			}
		}

		try {
			for (int i = 0; i < keys.length; i++) {
				if (type[i].equals("0")) {
					if (TextUtils.isEmpty(values[i])) {
						json.put(keys[i], "未设置");
					} else {
						json.put(keys[i], values[i]);
					}
				} else
					json.put(keys[i], type[i].substring(1).split("n")[Integer
							.valueOf(values[i])]);
			}

			SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now = new Date(System.currentTimeMillis());
			json.put("time", myFmt.format(now).toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newbutton);

		menu = getResources().getStringArray(R.array.buttoninfo_menu);
		type = getResources().getStringArray(R.array.buttoninfo_type);

		viewArr2 = new View[menu[0].split(",").length];
		value2 = new String[menu[0].split(",").length];
		viewArr4 = new View[menu[1].split(",").length];
		value4 = new String[menu[1].split(",").length];
		viewArr5 = new View[menu[2].split(",").length];
		value5 = new String[menu[2].split(",").length];

		json = new JSONObject();

		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView4 = (TextView) findViewById(R.id.textView4);
		textView5 = (TextView) findViewById(R.id.textView5);

		lists.add(getPage1());
		lists.add(getPage2(menu[0], type[0]));
		lists.add(getPage3());
		lists.add(getPage4(menu[1], type[1]));
		lists.add(getPage5(menu[2], type[2]));

		textView1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(0);
				textView1.setBackgroundResource(R.drawable.bg_top_bt);
				textView2.setBackgroundResource(R.drawable.bg_title);
				textView3.setBackgroundResource(R.drawable.bg_title);
				textView4.setBackgroundResource(R.drawable.bg_title);
				textView5.setBackgroundResource(R.drawable.bg_title);

			}
		});

		textView2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(1);
				textView2.setBackgroundResource(R.drawable.bg_top_bt);
				textView1.setBackgroundResource(R.drawable.bg_title);
				textView3.setBackgroundResource(R.drawable.bg_title);
				textView4.setBackgroundResource(R.drawable.bg_title);
				textView5.setBackgroundResource(R.drawable.bg_title);

			}
		});

		textView3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(2);
				textView3.setBackgroundResource(R.drawable.bg_top_bt);
				textView1.setBackgroundResource(R.drawable.bg_title);
				textView2.setBackgroundResource(R.drawable.bg_title);
				textView4.setBackgroundResource(R.drawable.bg_title);
				textView5.setBackgroundResource(R.drawable.bg_title);
			}
		});

		textView4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(3);
				textView4.setBackgroundResource(R.drawable.bg_top_bt);
				textView1.setBackgroundResource(R.drawable.bg_title);
				textView2.setBackgroundResource(R.drawable.bg_title);
				textView3.setBackgroundResource(R.drawable.bg_title);
				textView5.setBackgroundResource(R.drawable.bg_title);
			}
		});

		textView5.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(4);
				textView5.setBackgroundResource(R.drawable.bg_top_bt);
				textView1.setBackgroundResource(R.drawable.bg_title);
				textView2.setBackgroundResource(R.drawable.bg_title);
				textView3.setBackgroundResource(R.drawable.bg_title);
				textView4.setBackgroundResource(R.drawable.bg_title);
			}
		});

		textView1.setBackgroundResource(R.drawable.bg_top_bt);
		currentItem = 0;

		adapter = new ViewPagerAdapter(lists);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(adapter);

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			// 当滑动式，顶部的imageView是通过animation缓慢的滑动
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					if (currentItem == 1) {
						textView2.setBackgroundResource(R.drawable.bg_title);
						textView1.setBackgroundResource(R.drawable.bg_top_bt);
					} else if (currentItem == 2) {
						textView3.setBackgroundResource(R.drawable.bg_title);
						textView1.setBackgroundResource(R.drawable.bg_top_bt);
					} else if (currentItem == 3) {
						textView4.setBackgroundResource(R.drawable.bg_title);
						textView1.setBackgroundResource(R.drawable.bg_top_bt);
					} else if (currentItem == 4) {
						textView5.setBackgroundResource(R.drawable.bg_title);
						textView1.setBackgroundResource(R.drawable.bg_top_bt);
					}
					break;

				case 1:
					if (currentItem == 0) {
						textView1.setBackgroundResource(R.drawable.bg_title);
						textView2.setBackgroundResource(R.drawable.bg_top_bt);
					} else if (currentItem == 2) {
						textView3.setBackgroundResource(R.drawable.bg_title);
						textView2.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 3) {
						textView4.setBackgroundResource(R.drawable.bg_title);
						textView2.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 4) {
						textView5.setBackgroundResource(R.drawable.bg_title);
						textView2.setBackgroundResource(R.drawable.bg_top_bt);

					}
					break;
				case 2:
					if (currentItem == 0) {
						textView1.setBackgroundResource(R.drawable.bg_title);
						textView3.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 1) {
						textView2.setBackgroundResource(R.drawable.bg_title);
						textView3.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 3) {
						textView4.setBackgroundResource(R.drawable.bg_title);
						textView3.setBackgroundResource(R.drawable.bg_top_bt);
					} else if (currentItem == 4) {
						textView5.setBackgroundResource(R.drawable.bg_title);
						textView3.setBackgroundResource(R.drawable.bg_top_bt);

					}
					break;

				case 3:
					if (currentItem == 0) {
						textView1.setBackgroundResource(R.drawable.bg_title);
						textView4.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 1) {
						textView2.setBackgroundResource(R.drawable.bg_title);
						textView4.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 2) {
						textView3.setBackgroundResource(R.drawable.bg_title);
						textView4.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 4) {
						textView5.setBackgroundResource(R.drawable.bg_title);
						textView4.setBackgroundResource(R.drawable.bg_top_bt);
					}
					break;
				case 4:
					if (currentItem == 0) {
						textView1.setBackgroundResource(R.drawable.bg_title);
						textView5.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 1) {
						textView2.setBackgroundResource(R.drawable.bg_title);
						textView5.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 2) {
						textView3.setBackgroundResource(R.drawable.bg_title);
						textView5.setBackgroundResource(R.drawable.bg_top_bt);

					} else if (currentItem == 3) {
						textView4.setBackgroundResource(R.drawable.bg_title);
						textView5.setBackgroundResource(R.drawable.bg_top_bt);
					}
					break;
				default:
					break;
				}
				currentItem = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;

		}
		return super.onOptionsItemSelected(item);
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

	private class MyChart {
		private LineChart lineChart;
		private int[] hist;

		public MyChart(LineChart mlineChart, int[] Hist) {
			// TODO Auto-generated constructor stub
			lineChart = mlineChart;
			hist = Hist;
		}

		int imhist() {
			int max = 0;
			for (int i = 0; i < 256; i++) {
				max = (max > hist[i]) ? max : hist[i];
			}
			return max;
		}

		// 设置显示的样式
		private void showChart(LineChart lineChart, LineData lineData, int color) {
			// lineChart.setDrawBorders(false); // 是否在折线图上添加边框

			// no description text
			lineChart.setDescription("");// 数据描述
			// 如果没有数据的时候，会显示这个，类似listview的emtpyview
			lineChart
					.setNoDataTextDescription("You need to provide data for the chart.");

			// enable / disable grid background
			lineChart.setDrawGridBackground(false); // 是否显示表格颜色
			lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

			// enable touch gestures
			lineChart.setTouchEnabled(true); // 设置是否可以触摸

			// enable scaling and dragging
			lineChart.setDragEnabled(true);// 是否可以拖拽
			lineChart.setScaleEnabled(true);// 是否可以缩放

			// if disabled, scaling can be done on x- and y-axis separately
			lineChart.setPinchZoom(false);//

			lineChart.setBackgroundColor(color);// 设置背景

			// add data
			lineChart.setData(lineData); // 设置数据

			// get the legend (only possible after setting data)
			Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的

			// modify the legend ...
			// mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
			mLegend.setForm(LegendForm.CIRCLE);// 样式
			mLegend.setFormSize(6f);// 字体
			mLegend.setTextColor(Color.WHITE);// 颜色
			// mLegend.setTypeface(mTf);// 字体

			lineChart.animateX(2500); // 立即执行的动画,x轴
		}

		/**
		 * 生成一个数据
		 * 
		 * @param count
		 *            表示图表中有多少个坐标点
		 * @param range
		 *            用来生成range以内的随机数
		 * @return
		 */
		private LineData getLineData(int count, float range) {
			ArrayList<String> xValues = new ArrayList<String>();
			for (int i = 0; i < count; i++) {
				// x轴显示的数据，这里默认使用数字下标显示
				xValues.add("" + i);
			}

			// y轴的数据
			ArrayList<Entry> yValues = new ArrayList<Entry>();
			for (int i = 0; i < count; i++) {
				float value = (float) hist[i];
				yValues.add(new Entry(value, i));
			}

			// create a dataset and give it a type
			// y轴的数据集合
			LineDataSet lineDataSet = new LineDataSet(yValues, "直方图" /* 显示在比例图上 */);
			// mLineDataSet.setFillAlpha(110);
			// mLineDataSet.setFillColor(Color.RED);

			// 用y轴的集合来设置参数
			lineDataSet.setLineWidth(1.75f); // 线宽
			lineDataSet.setCircleSize(3f);// 显示的圆形大小
			lineDataSet.setColor(Color.WHITE);// 显示颜色
			lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
			lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色

			/*
			 * lineDataSet.setDrawCircleHole(false);
			 * lineDataSet.setLineWidth(10.5f); // 线宽
			 * lineDataSet.setDrawCubic(false);
			 * lineDataSet.setCubicIntensity(0.1f);
			 * lineDataSet.setDrawFilled(true);
			 * lineDataSet.setFillColor(Color.rgb(255, 0, 0));
			 */
			ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
			lineDataSets.add(lineDataSet); // add the datasets

			// create a data object with the datasets
			LineData lineData = new LineData(xValues, lineDataSets);

			return lineData;
		}

		void show() {
			int max = imhist();
			LineData mLineData = getLineData(256, max);
			showChart(mLineChart, mLineData, Color.rgb(114, 188, 223));
		}
	};

	class ViewPagerAdapter extends PagerAdapter {
		List<View> viewLists;

		public ViewPagerAdapter(List<View> lists) {
			viewLists = lists;
		}

		// 获得size
		@Override
		public int getCount() {
			return viewLists.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		// 销毁Item
		@Override
		public void destroyItem(View view, int position, Object object) {
			((ViewPager) view).removeView(viewLists.get(position));
		}

		// 实例化Item
		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(viewLists.get(position), 0);
			return viewLists.get(position);
		}
	};

}
