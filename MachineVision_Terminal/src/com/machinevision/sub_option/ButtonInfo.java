package com.machinevision.sub_option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.machinevision.sub_option.SystemSetting_devicePacket.Parameters;
import com.machinevision.sub_option.SystemSetting_devicePacket.Trigger;
import com.machinevision.terminal.R;
import com.machinevision.common_widget.DialogWindow;
import com.machinevision.common_widget.EToast;
import com.machinevision.option.MachineLearning;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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

	private View layout;
	private ViewPager vPager;
	private JSONObject json = null;

	public static Bitmap fullImage = null;

	String[] menu;
	String[] subMenu;
	String[] subMenuType;
	private int currentItem;

	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_info);

		listview1 = (ListView) this.findViewById(R.id.MyListView1);
		listview2 = (ListView) this.findViewById(R.id.MyListView2);
		listview3 = (ListView) this.findViewById(R.id.MyListView3);

		Button bt_modify = (Button) this.findViewById(R.id.bt_modify);
		Button bt_delete = (Button) this.findViewById(R.id.bt_delete);
		Button bt_exit = (Button) this.findViewById(R.id.exit);
		Button bt_save_as = (Button) this.findViewById(R.id.bt_save_as);
		Button bt_save = (Button) this.findViewById(R.id.bt_overwrite);
		image = (ImageView) findViewById(R.id.button_pic);

		MyButtonListener buttonListener = new MyButtonListener();
		bt_modify.setOnClickListener(buttonListener);
		bt_delete.setOnClickListener(buttonListener);
		bt_exit.setOnClickListener(buttonListener);
		bt_save_as.setOnClickListener(buttonListener);
		bt_save.setOnClickListener(buttonListener);

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

		currentItem = 0;
		json = ButtonCfgLlist.getJsonStr(file);
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
			case R.id.bt_modify:
				modifyInformation();
				break;
			case R.id.bt_save_as:
				saveAs();
				break;
			case R.id.bt_overwrite:
				DialogWindow dialog_overwrite = new DialogWindow.Builder(
						ButtonInfo.this)
						.setTitle("你将覆盖原有的配置文件,是否继续？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface arg0,
											int arg1)
									{
										SimpleDateFormat myFmt = new SimpleDateFormat(
												"yyyy-MM-dd HH:mm:ss");
										Date now = new Date(System
												.currentTimeMillis());
										try
										{
											json.put("time", myFmt.format(now)
													.toString());
										} catch (JSONException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										NewButton.write2sd(json, file.getName());
										EToast.makeText(ButtonInfo.this,
												"保存成功", Toast.LENGTH_SHORT)
												.show();
										onButtonClicked(RESULT_OK);
									}

								}).setNegativeButton("取消", null).create();
				dialog_overwrite.showWrapContent();
				break;
			case R.id.bt_delete:
				DialogWindow dialog_delete = new DialogWindow.Builder(
						ButtonInfo.this)
						.setTitle("确定删除？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface arg0,
											int arg1)
									{
										FileManager_fileExplorer
												.deleteDirectory(file);
										EToast.makeText(ButtonInfo.this,
												"删除成功", Toast.LENGTH_SHORT)
												.show();
										onButtonClicked(RESULT_OK);
									}
								}).setNegativeButton("取消", null).create();
				dialog_delete.showWrapContent();
				break;
			case R.id.exit:
				EToast.makeText(ButtonInfo.this, "退出成功", Toast.LENGTH_SHORT)
						.show();
				onButtonClicked(RESULT_OK);
				break;
			default:
				break;
			}

		}
	}

	public static String getImgPath(String id)
	{
		return MachineLearning.FILE_DIR + id + "/" + id + ".jpg";
	}

	void saveAs()
	{
		LayoutInflater inflater = (LayoutInflater) ButtonInfo.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.edit_text, null);

		DialogWindow dialog_saveAs = new DialogWindow.Builder(ButtonInfo.this)
				.setTitle("配置文件命名").setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						EditText nameEditText = (EditText) view
								.findViewById(R.id.editText1);
						String saveName = nameEditText.getText().toString();

						/*
						 * 存入配置文件中
						 */
						try
						{
							File file = new File(NewButton.getIniFile(saveName));
							if (!file.getParentFile().exists())
							{
								file.getParentFile().mkdirs();
							}
							FileWriter writer = new FileWriter(file);
							writer.write(json.toString());
							writer.close();
						} catch (IOException e)
						{
							e.printStackTrace();
						}

						fullImage = getBitmap(file);
						NewButton.saveMyBitmap(fullImage, saveName);
						EToast.makeText(ButtonInfo.this, "另存为成功",
								Toast.LENGTH_SHORT).show();
					}

				}).setNegativeButton("取消", null).create();
		dialog_saveAs.showWrapContent();
	}

	void modifyInformation()
	{
		layout = LayoutInflater.from(this).inflate(R.layout.button_configure,
				null);
		initViewPager();
		AlertDialog dialog = new AlertDialog.Builder(ButtonInfo.this)
				.setTitle("配置文件修改").setView(layout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						// TODO Auto-generated method stub
						switch (currentItem)
						{
						case 0:
							NewButton.saveInfomation(json,
									subMenu[0].split(","),
									subMenuType[0].split(","), viewArr1, value1);
							EToast.makeText(ButtonInfo.this, "修改成功",
									Toast.LENGTH_SHORT).show();
							mAdapter1.notifyDataSetChanged();
							break;
						case 1:

							NewButton.saveInfomation(json,
									subMenu[1].split(","),
									subMenuType[1].split(","), viewArr2, value2);
							EToast.makeText(ButtonInfo.this, "修改成功",
									Toast.LENGTH_SHORT).show();

							mAdapter2.notifyDataSetChanged();
							break;
						case 2:
							NewButton.saveInfomation(json,
									subMenu[2].split(","),
									subMenuType[2].split(","), viewArr3, value3);
							EToast.makeText(ButtonInfo.this, "修改成功",
									Toast.LENGTH_SHORT).show();

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
				.setLayout((int) (size.x * 0.8), (int) (size.y * 0.9));
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
		return NewButton.getTopView(this, subMenu[0], subMenuType[0], viewArr1);
	}

	private View getPage2()
	{
		return NewButton.getTopView(this, subMenu[1], subMenuType[1], viewArr2);
	}

	private View getPage3()
	{
		return NewButton.getTopView(this, subMenu[2], subMenuType[2], viewArr3);
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

		final TextView text1 = (TextView) layout.findViewById(R.id.textConfig1);
		final TextView text2 = (TextView) layout.findViewById(R.id.textConfig2);
		final TextView text3 = (TextView) layout.findViewById(R.id.textConfig3);

		vPager = (ViewPager) layout.findViewById(R.id.device_setting_vpager);
		vPager.setOffscreenPageLimit(2);
		vPager.setAdapter(new MyPagerAdapter(vPager, list));

		vPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int arg0)
			{
				currentItem = arg0;
				switch (currentItem)
				{
				case 0:
					text1.setBackgroundResource(R.drawable.bg_top_bt);
					text2.setBackgroundResource(R.drawable.bg_title);
					text3.setBackgroundResource(R.drawable.bg_title);

					break;
				case 1:
					text1.setBackgroundResource(R.drawable.bg_title);
					text2.setBackgroundResource(R.drawable.bg_top_bt);
					text3.setBackgroundResource(R.drawable.bg_title);

					break;
				case 2:
					text1.setBackgroundResource(R.drawable.bg_title);
					text2.setBackgroundResource(R.drawable.bg_title);
					text3.setBackgroundResource(R.drawable.bg_top_bt);
					break;
				default:
					break;
				}
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
