package com.machinevision.sub_option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.machinevision.common_widget.EToast;
import com.machinevision.net.CmdHandle;
import com.machinevision.net.NetUtils;
import com.machinevision.option.MachineLearning;
import com.machinevision.terminal.FileDirectory;
import com.machinevision.terminal.MainActivity;
import com.machinevision.terminal.R;
import com.machinevision.terminal.R.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class NewButton extends Activity
{
	public static final String FILE_DIR = FileDirectory.getAppDirectory()
			+ "Learning/";

	private boolean stopFlag = false;
	private ViewPager viewPager;

	private ImageView imageView;
	private String ButtonID;
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
	JSONObject json = null;

	private Handler handler = new Handler() // 接收网络子线程数据并更新UI
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case NetUtils.MSG_NET_GET_VIDEO: // 获取图像
				fullImage = (Bitmap) msg.obj;
				imageView.setImageBitmap((Bitmap) msg.obj);
				break;
			case MainActivity.ERROR_MESSAGE:
				EToast.makeText(NewButton.this, "网络未连接", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				EToast.makeText(NewButton.this, "网络异常", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	private View getPage1()
	{

		View page1 = getLayoutInflater().inflate(R.layout.vpage1_new_button,
				null);
		imageView = (ImageView) page1
				.findViewById(R.id.machine_learning_page1_image);
		Button bt_get = (Button) page1
				.findViewById(R.id.machine_learning_page1_get);

		bt_get.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						CmdHandle cmdHandle = CmdHandle.getInstance();
						if (cmdHandle == null)
						{
							Message message = handler.obtainMessage();
							message.what = MainActivity.ERROR_MESSAGE;
							message.sendToTarget();
						}
						else
						{
							while (!stopFlag)
							{
								cmdHandle.getVideo(handler);
							}
						}

					}
				}).start();
			}
		});

		Button bt_save = (Button) page1
				.findViewById(R.id.machine_learning_page1_save);
		bt_save.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				stopFlag = true;
				LayoutInflater inflater = (LayoutInflater) NewButton.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View view = inflater.inflate(R.layout.file_rname, null);

				new AlertDialog.Builder(NewButton.this)
						.setTitle("配置文件命名")
						.setView(view)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog,
											int which)
									{
										// TODO Auto-generated method stub
										EditText nameEditText = (EditText) view
												.findViewById(R.id.editText1);
										ButtonID = nameEditText.getText()
												.toString();
										if (fullImage != null)
										{
											saveMyBitmap(fullImage, ButtonID);
											EToast.makeText(NewButton.this,
													"图像保存成功",
													Toast.LENGTH_SHORT).show();
										}
										else
											EToast.makeText(NewButton.this,
													"图像未获取", Toast.LENGTH_SHORT)
													.show();
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		Button bt_exit = (Button) page1
				.findViewById(R.id.machine_learning_page1_exit);
		bt_exit.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finishWithAnim();
			}
		});

		return page1;
	}

	public static String getImgPath(String id)
	{
		return FILE_DIR + id + "/" + id + ".jpg";
	}

	String getIniFile(String id)
	{
		return FILE_DIR + id + "/" + id + ".ini";
	}

	private void saveMyBitmap(Bitmap mBitmap, String bitName)
	{
		File f = new File(getImgPath(bitName));
		if (!f.getParentFile().exists())
		{
			f.getParentFile().mkdirs();
		}

		FileOutputStream fOut = null;
		try
		{
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try
		{
			fOut.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			fOut.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private LinearLayout getTopView(String menu, String strType, View[] viewsTop)
	{

		LinearLayout layoutTop = new LinearLayout(this);
		layoutTop.setOrientation(LinearLayout.VERTICAL);

		String[] contents = menu.split(",");
		String[] type = strType.split(",");

		if (viewsTop.length != contents.length)
		{
			EToast.makeText(NewButton.this, "长度不等", Toast.LENGTH_SHORT).show();
			return null;
		}

		for (int i = 0; i < contents.length; i++)
		{
			LinearLayout subLayout = new LinearLayout(this);
			subLayout.setOrientation(LinearLayout.HORIZONTAL);
			subLayout.setGravity(Gravity.CENTER_HORIZONTAL);

			LayoutParams params = new LayoutParams(250,
					LayoutParams.WRAP_CONTENT, 0);
			/*
			 * 创建一个文本框并设置参数
			 */
			TextView tv = new TextView(this);
			tv.setLayoutParams(params);
			tv.setText(contents[i] + "：");
			tv.setTextSize(27F);

			params.width = 150;
			if (type[i].startsWith("0"))
			{
				viewsTop[i] = new EditText(this);

				((EditText) viewsTop[i]).setTextSize(25F);
				((EditText) viewsTop[i])
						.setBackgroundResource(android.R.drawable.edit_text);
			}
			else
			{
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						R.layout.spiner, type[i].substring(1).split("n"));
				viewsTop[i] = new Spinner(this);

				((Spinner) viewsTop[i]).setAdapter(adapter);
			}

			viewsTop[i].setLayoutParams(params);
			viewsTop[i].setPadding(8, 2, 5, 2);
			subLayout.addView(tv);
			subLayout.addView(viewsTop[i]);

			layoutTop.addView(subLayout);
		}

		return layoutTop;
	}

	void setStyle(Button button, CharSequence text, LayoutParams params)
	{
		button.setWidth(150);
		button.setHeight(100);
		button.setText(text);
		button.setTextSize(30);
		button.setLayoutParams(params);
		button.setBackgroundResource(R.drawable.bg_control_bt);
	}

	private View getPage2(String Menu, String strType)
	{

		LinearLayout layoutOuter = new LinearLayout(this);
		layoutOuter.setOrientation(LinearLayout.VERTICAL);
		layoutOuter.setPadding(10, 50, 40, 20);
		LinearLayout layoutTop = getTopView(Menu, strType, viewArr2);
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

		((Button) viewsBottom[0]).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				saveInfomation(json, menu[0].split(","), type[0].split(","),
						viewArr2, value2);
				EToast.makeText(NewButton.this, "保存成功", Toast.LENGTH_SHORT)
						.show();
			}
		});

		((Button) viewsBottom[1]).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				EToast.makeText(NewButton.this, "退出成功", Toast.LENGTH_SHORT)
						.show();
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

	private View getPage3()
	{
		View page3 = getLayoutInflater().inflate(R.layout.vpage3_new_button,
				null);
		return page3;
	}

	private View getPage4(String Menu, String strType)
	{
		LinearLayout layoutOuter = new LinearLayout(this);
		layoutOuter.setOrientation(LinearLayout.VERTICAL);
		layoutOuter.setPadding(10, 50, 40, 20);

		LinearLayout layoutTop = getTopView(Menu, strType, viewArr4);
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

		((Button) viewsBottom[0]).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				saveInfomation(json, menu[1].split(","), type[1].split(","),
						viewArr2, value2);
				EToast.makeText(NewButton.this, "保存成功", Toast.LENGTH_SHORT)
						.show();
			}
		});

		((Button) viewsBottom[1]).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				EToast.makeText(NewButton.this, "退出成功", Toast.LENGTH_SHORT)
						.show();
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

	private View getPage5(String Menu, String strType)
	{
		LinearLayout layoutOuter = new LinearLayout(this);
		layoutOuter.setOrientation(LinearLayout.VERTICAL);
		layoutOuter.setPadding(10, 50, 40, 20);

		LinearLayout layoutTop = getTopView(Menu, strType, viewArr5);
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
		((Button) viewsBottom[0]).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				saveInfomation(json, menu[2].split(","), type[2].split(","),
						viewArr5, value5);
				EToast.makeText(NewButton.this, "保存成功", Toast.LENGTH_SHORT)
						.show();
			}
		});

		((Button) viewsBottom[1]).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				System.out.println("-------" + json.toString());
				EToast.makeText(NewButton.this, "退出成功", Toast.LENGTH_SHORT)
						.show();
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

	private void saveInfomation(JSONObject json, String[] keys, String[] type,
			View[] viewArr, String[] values)
	{

		for (int i = 0; i < viewArr.length; i++)
		{
			if (viewArr[i] instanceof EditText)
			{
				values[i] = ((EditText) viewArr[i]).getText().toString();
			}
			else if (viewArr[i] instanceof Spinner)
			{
				values[i] = String.valueOf(((Spinner) viewArr[i])
						.getSelectedItemPosition());
			}
		}

		try
		{
			for (int i = 0; i < keys.length; i++)
			{
				if (type[i].equals("0"))
				{
					if (TextUtils.isEmpty(values[i]))
						json.put(keys[i], null);
					else
						json.put(keys[i], Integer.valueOf(values[i]));
				}
				else
					json.put(keys[i], type[i].substring(1).split("n")[Integer
							.valueOf(values[i])]);
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}

		try
		{
			FileWriter writer = new FileWriter(getIniFile(ButtonID));
			writer.write(json.toString());
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
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

		textView1.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(0);
				textView1.setBackgroundResource(R.drawable.bg_top_bt);
				textView2.setBackgroundResource(R.drawable.bg_title);
				textView3.setBackgroundResource(R.drawable.bg_title);
				textView4.setBackgroundResource(R.drawable.bg_title);
				textView5.setBackgroundResource(R.drawable.bg_title);

			}
		});

		textView2.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(1);
				textView2.setBackgroundResource(R.drawable.bg_top_bt);
				textView1.setBackgroundResource(R.drawable.bg_title);
				textView3.setBackgroundResource(R.drawable.bg_title);
				textView4.setBackgroundResource(R.drawable.bg_title);
				textView5.setBackgroundResource(R.drawable.bg_title);

			}
		});

		textView3.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(2);
				textView3.setBackgroundResource(R.drawable.bg_top_bt);
				textView1.setBackgroundResource(R.drawable.bg_title);
				textView2.setBackgroundResource(R.drawable.bg_title);
				textView4.setBackgroundResource(R.drawable.bg_title);
				textView5.setBackgroundResource(R.drawable.bg_title);
			}
		});

		textView4.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(3);
				textView4.setBackgroundResource(R.drawable.bg_top_bt);
				textView1.setBackgroundResource(R.drawable.bg_title);
				textView2.setBackgroundResource(R.drawable.bg_title);
				textView3.setBackgroundResource(R.drawable.bg_title);
				textView5.setBackgroundResource(R.drawable.bg_title);
			}
		});

		textView5.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
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

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			// 当滑动式，顶部的imageView是通过animation缓慢的滑动
			@Override
			public void onPageSelected(int arg0)
			{
				switch (arg0)
				{
				case 0:
					if (currentItem == 1)
					{
						textView2.setBackgroundResource(R.drawable.bg_title);
						textView1.setBackgroundResource(R.drawable.bg_top_bt);
					}
					else if (currentItem == 2)
					{
						textView3.setBackgroundResource(R.drawable.bg_title);
						textView1.setBackgroundResource(R.drawable.bg_top_bt);
					}
					else if (currentItem == 3)
					{
						textView4.setBackgroundResource(R.drawable.bg_title);
						textView1.setBackgroundResource(R.drawable.bg_top_bt);
					}
					else if (currentItem == 4)
					{
						textView5.setBackgroundResource(R.drawable.bg_title);
						textView1.setBackgroundResource(R.drawable.bg_top_bt);
					}
					break;

				case 1:
					if (currentItem == 0)
					{
						textView1.setBackgroundResource(R.drawable.bg_title);
						textView2.setBackgroundResource(R.drawable.bg_top_bt);
					}
					else if (currentItem == 2)
					{
						textView3.setBackgroundResource(R.drawable.bg_title);
						textView2.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 3)
					{
						textView4.setBackgroundResource(R.drawable.bg_title);
						textView2.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 4)
					{
						textView5.setBackgroundResource(R.drawable.bg_title);
						textView2.setBackgroundResource(R.drawable.bg_top_bt);

					}
					break;
				case 2:
					if (currentItem == 0)
					{
						textView1.setBackgroundResource(R.drawable.bg_title);
						textView3.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 1)
					{
						textView2.setBackgroundResource(R.drawable.bg_title);
						textView3.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 3)
					{
						textView4.setBackgroundResource(R.drawable.bg_title);
						textView3.setBackgroundResource(R.drawable.bg_top_bt);
					}
					else if (currentItem == 4)
					{
						textView5.setBackgroundResource(R.drawable.bg_title);
						textView3.setBackgroundResource(R.drawable.bg_top_bt);

					}
					break;

				case 3:
					if (currentItem == 0)
					{
						textView1.setBackgroundResource(R.drawable.bg_title);
						textView4.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 1)
					{
						textView2.setBackgroundResource(R.drawable.bg_title);
						textView4.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 2)
					{
						textView3.setBackgroundResource(R.drawable.bg_title);
						textView4.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 4)
					{
						textView5.setBackgroundResource(R.drawable.bg_title);
						textView4.setBackgroundResource(R.drawable.bg_top_bt);
					}
					break;
				case 4:
					if (currentItem == 0)
					{
						textView1.setBackgroundResource(R.drawable.bg_title);
						textView5.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 1)
					{
						textView2.setBackgroundResource(R.drawable.bg_title);
						textView5.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 2)
					{
						textView3.setBackgroundResource(R.drawable.bg_title);
						textView5.setBackgroundResource(R.drawable.bg_top_bt);

					}
					else if (currentItem == 3)
					{
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
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 关闭时触发切换动画
	 * 
	 * @author MC
	 */
	private void finishWithAnim()
	{
		finish();
		overridePendingTransition(0, R.anim.top_out);
	}
}
