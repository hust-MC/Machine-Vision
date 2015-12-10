package com.machinevision.sub_option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.emercy.dropdownlist.DropDownList;
import com.emercy.dropdownlist.DropDownList.OnDropListClickListener;
import com.machinevision.terminal.EToast;
import com.machinevision.terminal.R;
import com.machinevision.option.MachineLearning;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;
import com.machinevision.terminal.FileDirectory;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class ButtonInfo extends Activity
{
	private File file;
	protected ListView listview1;
	protected ListView listview2;
	protected ListView listview3;

	private View[] viewArr1;
	private View[] viewArr2;
	private View[] viewArr3;

	String[] value1;
	String[] value2;
	String[] value3;

	ImageView image;

	private CustomAdapter mAdapter1;
	private CustomAdapter mAdapter2;
	private CustomAdapter mAdapter3;

	View layout;
	ViewPager vPager;
	DropDownList dropList;
	JSONObject json = null;
	Bitmap pic = null;

	public static Bitmap fullImage = null;

	String[] menu;
	String[] subMenu;
	String[] subMenuType;

	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_info);

		listview1 = (ListView) this.findViewById(R.id.MyListView1);
		listview2 = (ListView) this.findViewById(R.id.MyListView2);
		listview3 = (ListView) this.findViewById(R.id.MyListView3);

		Button button1 = (Button) this.findViewById(R.id.bt_saveAs);
		Button button2 = (Button) this.findViewById(R.id.bt_modify);
		Button button3 = (Button) this.findViewById(R.id.bt_delete);
		image = (ImageView) findViewById(R.id.button_pic);

		MyButtonListener buttonListener = new MyButtonListener();
		button1.setOnClickListener(buttonListener);
		button2.setOnClickListener(buttonListener);
		button3.setOnClickListener(buttonListener);

		mAdapter1 = new CustomAdapter(R.string.option_machine_learning_info);
		mAdapter2 = new CustomAdapter(R.string.option_machine_alg_config);
		mAdapter3 = new CustomAdapter(R.string.option_machine_criteria_config);

		listview1.setAdapter(mAdapter1);
		listview2.setAdapter(mAdapter2);
		listview3.setAdapter(mAdapter3);

		layout = LayoutInflater.from(this).inflate(R.layout.button_configure,
				null);

		Intent intent = this.getIntent();
		String filename = intent.getStringExtra("file");

		File f = new File(MachineLearning.FILE_DIR);
		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++)
		{
			if (files[i].getName().equals(filename))
			{
				file = files[i];
				break;
			}
		}

		json = getJsonStr(file);
		image.setImageBitmap(getBitmap(file));
		menu = getResources().getStringArray(R.array.buttoninfo_config);
		subMenu = getResources().getStringArray(R.array.buttoninfo_menu);
		subMenuType = getResources().getStringArray(R.array.buttoninfo_type);

		viewArr1 = new View[subMenu[0].split(",").length];
		value1 = new String[subMenu[0].split(",").length];
		viewArr2 = new View[subMenu[1].split(",").length];
		value2 = new String[subMenu[1].split(",").length];
		viewArr3 = new View[subMenu[2].split(",").length];
		value3 = new String[subMenu[2].split(",").length];
	}

	class MyButtonListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			switch (arg0.getId())
			{
			case R.id.bt_saveAs:
				break;
			case R.id.bt_modify:
				modifyInformation();
				break;
			case R.id.bt_delete:
				deleteDirectory(file);
				EToast.makeText(ButtonInfo.this, "删除成功", Toast.LENGTH_SHORT)
						.show();
				onButtonClicked(R.id.bt_delete);
				break;
			default:
				break;
			}

		}
	}

	void myprintf(String[] val)
	{
		for (int i = 0; i < val.length; i++)
		{
			System.out.println("---->" + val[i]);
		}
	}

	String getIniFile(File file)
	{
		return file.getAbsolutePath() + "/" + file.getName() + ".ini";
	}

	void saveInfomation(String[] keys, String[] type, String[] values)
	{
		try
		{
			for (int i = 0; i < keys.length; i++)
			{
				if (type[i].equals("0"))
					json.put(keys[i], Integer.valueOf(values[i]));
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
			FileWriter writer = new FileWriter(getIniFile(file));
			writer.write(json.toString());
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	void modifyInformation()
	{
		layout = LayoutInflater.from(this).inflate(R.layout.button_configure,
				null);

		initViewPager();
		initDropDownList();

		AlertDialog dialog = new AlertDialog.Builder(ButtonInfo.this)
				.setTitle("钮扣文件配置").setView(layout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						// TODO Auto-generated method stub
						switch (dropList.getCurrentIndex())
						{
						case 0:
							for (int i = 0; i < viewArr1.length; i++)
							{
								if (viewArr1[i] instanceof EditText)
								{
									value1[i] = ((EditText) viewArr1[i])
											.getText().toString();
								}
								else if (viewArr1[i] instanceof Spinner)
								{
									value1[i] = String
											.valueOf(((Spinner) viewArr1[i])
													.getSelectedItemPosition());
								}
							}
							saveInfomation(subMenu[0].split(","),
									subMenuType[0].split(","), value1);
							mAdapter1.notifyDataSetChanged();
							break;
						case 1:
							for (int i = 0; i < viewArr2.length; i++)
							{
								if (viewArr2[i] instanceof EditText)
								{
									value2[i] = ((EditText) viewArr2[i])
											.getText().toString();
								}
								else if (viewArr2[i] instanceof Spinner)
								{
									value2[i] = String
											.valueOf(((Spinner) viewArr2[i])
													.getSelectedItemPosition());
								}
							}
							saveInfomation(subMenu[1].split(","),
									subMenuType[1].split(","), value2);
							mAdapter2.notifyDataSetChanged();
							break;
						case 2:
							for (int i = 0; i < viewArr3.length; i++)
							{
								if (viewArr3[i] instanceof EditText)
								{
									value3[i] = ((EditText) viewArr3[i])
											.getText().toString();
								}
								else if (viewArr2[i] instanceof Spinner)
								{
									value3[i] = String
											.valueOf(((Spinner) viewArr3[i])
													.getSelectedItemPosition());
								}
							}
							saveInfomation(subMenu[2].split(","),
									subMenuType[2].split(","), value3);
							mAdapter3.notifyDataSetChanged();
							break;
						default:
							break;
						}
					}
				}).setNegativeButton("取消", null).create();
		dialog.show();
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		dialog.getWindow()
				.setLayout((int) (size.x * 0.8), (int) (size.y * 0.7));
		// 让对话框能够弹出输入法
		dialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
				.setTextSize(27F);
		((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE)
				.setTextSize(27F);
	}

	private View getPage1()
	{
		return getView(menu[0], subMenu[0], subMenuType[0], viewArr1);
	}

	private View getPage2()
	{
		return getView(menu[1], subMenu[1], subMenuType[1], viewArr2);
	}

	private View getPage3()
	{
		return getView(menu[2], subMenu[2], subMenuType[2], viewArr3);
	}

	private JSONObject getJsonStr(File file)
	{
		JSONObject json = null;
		File[] f = file.listFiles();
		for (int j = 0; j < f.length; j++)
		{
			if (f[j].getName().endsWith("ini"))
			{
				try
				{
					String str = null;
					StringBuffer strBuf = new StringBuffer();
					BufferedReader reader = new BufferedReader(new FileReader(
							f[j]));
					while (!TextUtils.isEmpty(str = reader.readLine()))
					{
						strBuf.append(str);
					}
					json = new JSONObject(strBuf.toString());
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
				break;
			}
		}
		return json;
	}

	private Bitmap getBitmap(File file)
	{
		Bitmap bm = null;
		File[] f = file.listFiles();
		for (int j = 0; j < f.length; j++)
		{
			if (f[j].getName().endsWith("jpg"))
			{
				bm = BitmapFactory.decodeFile(f[j].getAbsolutePath());
				break;
			}
		}
		return bm;
	}

	public View getView(String title, String menu, String strType, View[] views)
	{
		String[] contents = menu.split(",");
		String[] type = strType.split(",");

		ScrollView scrollView = new ScrollView(this);
		scrollView.setPadding(0, 0, 0, 10);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(10, 20, 40, 20);
		for (int i = 0; i < contents.length; i++)
		{
			LinearLayout subLayout = new LinearLayout(this);
			subLayout.setOrientation(LinearLayout.HORIZONTAL);

			LayoutParams params = new LayoutParams(400,
					LayoutParams.WRAP_CONTENT, 1);

			/*
			 * 创建一个文本框并设置参数
			 */
			TextView tv = new TextView(this);
			tv.setLayoutParams(params);
			tv.setText(contents[i] + "：");
			tv.setTextSize(27F);

			/*
			 * 解析R.array.option_camera_params_sub并创建对应的输入框， 设置参数，用以接收输入的数据
			 */
			params.weight = 1.5F;
			if (type[i].startsWith("0"))
			{
				views[i] = new EditText(this);

				((EditText) views[i]).setTextSize(25F);
				((EditText) views[i])
						.setBackgroundResource(android.R.drawable.edit_text);
			}
			else
			{
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						R.layout.spiner, type[i].substring(1).split("n"));
				views[i] = new Spinner(this);
				((Spinner) views[i]).setAdapter(adapter);
			}

			// et.setHint("请输入" + contents[i]);
			views[i].setLayoutParams(params);
			views[i].setPadding(8, 2, 5, 2);

			subLayout.addView(tv);
			subLayout.addView(views[i]);

			layout.addView(subLayout);
		}
		scrollView.addView(layout);
		return scrollView;
	}

	/**
	 * 设置ViewPager的内容
	 */
	private void initViewPager()
	{
		List<View> list = new ArrayList<View>();

		View page1 = getPage1();
		View page2 = getPage2();
		View page3 = getPage3();

		list.add(page1);
		list.add(page2);
		list.add(page3);

		vPager = (ViewPager) layout.findViewById(R.id.device_setting_vpager);
		vPager.setOffscreenPageLimit(2);
		vPager.setAdapter(new MyPagerAdapter(vPager, list));

		vPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int arg0)
			{
				dropList.setSelection(arg0);
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

	private void initDropDownList()
	{
		dropList = (DropDownList) layout
				.findViewById(R.id.device_setting_droplist);
		dropList.setItem(getResources().getStringArray(
				R.array.buttoninfo_config));

		dropList.setOnListClickListener(new OnDropListClickListener()
		{
			@Override
			public void onListItemClick(DropDownList dropDownList, int which)
			{
				if (vPager != null)
				{
					vPager.setCurrentItem(which);
				}
			}
		});
	}

	private void deleteDirectory(File file)
	{
		if (file.isFile())
		{
			file.delete();
		}
		else if (file.isDirectory())
		{
			File[] files = file.listFiles();
			if (files == null || files.length == 0)
			{
				file.delete();
			}
			else
			{
				for (File subFile : files)
				{
					deleteDirectory(subFile);
				}
			}
			file.delete();
		}
	}

	private final class CustomAdapter extends BaseAdapter
	{

		private String[] names = null;
		private int images = R.drawable.correct;

		private LayoutInflater mInflater;

		public CustomAdapter(int resid)
		{
			names = getResources().getString(resid).split(",");
			mInflater = getLayoutInflater();
		}

		// 描述adpter的大小（确定了listView的条目）
		@Override
		public int getCount()
		{
			return names.length;
		}

		// Adapter对于的position的数据
		@Override
		public Object getItem(int position)
		{
			return names[position];
		}

		// 得到item 在adapter所对应的位置
		@Override
		public long getItemId(int position)
		{
			return position;
		}

		// 创建listview的item条目，把数据绑定给item int position, Adapter的下标 View
		// convertView, 缓存的第一屏item的布局文件 ViewGroup parent ListView
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = null;
			if (convertView != null)
			{
				view = convertView;
			}
			else
			{
				view = mInflater.inflate(R.layout.list_buttoninfo, parent,
						false);
			}

			ImageView iv_header = (ImageView) view
					.findViewById(R.id.ItemImage1);
			TextView tv_name = (TextView) view.findViewById(R.id.name);
			TextView tv_content = (TextView) view.findViewById(R.id.content);

			iv_header.setImageResource(images);
			tv_name.setText(names[position] + ":");
			if (json.has(names[position]))
			{
				try
				{
					tv_content.setText(json.getString(names[position])
							.toString());
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				tv_content.setText("未设置");
			}

			return view;
		}
	}

	private class MyPagerAdapter extends PagerAdapter
	{
		private List<View> list;

		public MyPagerAdapter(ViewPager vPager, List<View> list)
		{
			Log.d("CJ", "MyPager");
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			Log.d("CJ", "remove");
			((ViewPager) container).removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position)
		{
			Log.d("CJ", "instantiate");
			((ViewPager) container).addView(list.get(position));
			return list.get(position);
		}

		@Override
		public int getCount()
		{
			Log.d("CJ", "getCount");
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			Log.d("CJ", "from");
			return arg0 == arg1;
		}
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

	/**
	 * 确认、取消按键的触发事件
	 * 
	 * @param resId
	 *            按键的ID
	 * @author MC
	 */
	public void onButtonClicked(int resId)
	{
		setResult(resId);
		finishWithAnim();
	}
}
