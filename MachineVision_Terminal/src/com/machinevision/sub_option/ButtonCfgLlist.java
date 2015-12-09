package com.machinevision.sub_option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.machinevision.terminal.R;
import com.machinevision.option.MachineLearning;
import com.machinevision.terminal.FileDirectory;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class ButtonCfgLlist extends ListActivity
{

	public static final int SHOW_INFO = 102;

	private Button button;
	private Spinner spiner1, spiner2, spiner3, spiner4, spiner5;

	private ArrayList<HashMap<String, Object>> listItems; // 存放文字、图片信息

	private SimpleAdapter listItemAdapter; // 适配器
	File[] files;
	String[] vals =
	{ null, null, null, null, null };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		System.out.println("--------buttoncfglist->");
		setContentView(R.layout.button_cfg_list);

		button = (Button) findViewById(R.id.bt_query);
		button.setText("查询");

		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click
				String[] strs = getResources().getStringArray(
						R.array.option_machine_learning_ini);
				String[] keys = strs[0].split(",");
				queryButton(null, keys, vals);
				listItemAdapter.notifyDataSetChanged();
			}
		});

		spiner1 = (Spinner) findViewById(R.id.spinnerId1);
		spiner2 = (Spinner) findViewById(R.id.spinnerId2);
		spiner3 = (Spinner) findViewById(R.id.spinnerId3);
		spiner4 = (Spinner) findViewById(R.id.spinnerId4);
		spiner5 = (Spinner) findViewById(R.id.spinnerId5);

		spiner1.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
		spiner2.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
		spiner3.setOnItemSelectedListener(new SpinnerOnSelectedListener3());
		spiner4.setOnItemSelectedListener(new SpinnerOnSelectedListener4());
		spiner5.setOnItemSelectedListener(new SpinnerOnSelectedListener5());

		initSpiner(getResources().getStringArray(
				R.array.option_machine_learning_type));
		initListView();
		ButtonCfgLlist.this.setListAdapter(listItemAdapter);
	}

	void myprintf(String[] val)
	{
		for (int i = 0; i < val.length; i++)
		{
			System.out.println("---->" + val[i]);
		}
	}

	class SpinnerOnSelectedListener1 implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id)
		{
			String selected = adapterView.getItemAtPosition(position)
					.toString();
			System.out.println("---->" + selected);
			vals[0] = selected;
			myprintf(vals);
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView)
		{
			// TODO Auto-generated method stub
			System.out.println("nothingSelected");
		}
	};

	class SpinnerOnSelectedListener2 implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id)
		{
			String selected = adapterView.getItemAtPosition(position)
					.toString();
			vals[1] = selected;
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView)
		{
			// TODO Auto-generated method stub
			System.out.println("nothingSelected");
		}
	};

	class SpinnerOnSelectedListener3 implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id)
		{
			String selected = adapterView.getItemAtPosition(position)
					.toString();
			System.out.println("---->" + selected);
			vals[2] = selected;
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView)
		{
			// TODO Auto-generated method stub
			System.out.println("nothingSelected");
		}
	};

	class SpinnerOnSelectedListener4 implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id)
		{
			String selected = adapterView.getItemAtPosition(position)
					.toString();
			System.out.println("---->" + selected);
			vals[3] = selected;
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView)
		{
			// TODO Auto-generated method stub
			System.out.println("nothingSelected");
		}
	};

	class SpinnerOnSelectedListener5 implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id)
		{
			String selected = adapterView.getItemAtPosition(position)
					.toString();
			System.out.println("---->" + selected);
			vals[4] = selected;
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView)
		{
			// TODO Auto-generated method stub
			System.out.println("nothingSelected");
		}
	}

	/**
	 * 点击事件
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Toast.makeText(this, "你点击了第" + position + "行", Toast.LENGTH_SHORT)
				.show();

		Intent intent = new Intent(this, ButtonInfo.class);
		String string = (String) listItems.get(position).get("ItemTitle");
		intent.putExtra("file", string);
		startActivityForResult(intent, SHOW_INFO);
	}

	void initSpiner(String[] strType)
	{
		String[] type = strType[0].split(",");

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				R.layout.spinner1, type[0].substring(1).split("n"));
		spiner1.setAdapter(adapter1);

		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				R.layout.spinner1, type[1].substring(1).split("n"));
		spiner2.setAdapter(adapter2);

		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				R.layout.spinner1, type[2].substring(1).split("n"));
		spiner3.setAdapter(adapter3);

		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
				R.layout.spinner1, type[3].substring(1).split("n"));
		spiner4.setAdapter(adapter4);

		ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this,
				R.layout.spinner1, type[4].substring(1).split("n"));
		spiner5.setAdapter(adapter5);
	}

	/**
	 * 设置适配器内容
	 */
	private void initListView()
	{
		listItems = new ArrayList<HashMap<String, Object>>();
		File[] files = new File(MachineLearning.FILE_DIR).listFiles();
		for (int i = 0; i < files.length; i++)
		{
			listItems.add(getData(files[i]));
		}

		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
				R.layout.list_item, // ListItem的XML布局实现
				new String[]
				{ "ItemTitle", "ItemImage" }, // 动态数组与ImageItem对应的子项
				new int[]
				{ R.id.ItemTitle, R.id.ItemImage } // list_item.xml布局文件里面的一个ImageView的ID,一个TextView
													// 的ID
		);
		listItemAdapter.setViewBinder(new SimpleAdapter.ViewBinder()
		{
			public boolean setViewValue(View view, Object data,
					String textRepresentation)
			{
				// 判断是否为我们要处理的对象
				if (view instanceof ImageView && data instanceof Bitmap)
				{
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap) data);
					return true;
				}
				else
					return false;
			}
		});
	}

	HashMap<String, Object> getData(File file)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (file.isDirectory())
		{
			File[] f = file.listFiles();
			for (int j = 0; j < f.length; j++)
			{
				if (f[j].getName().endsWith("jpg")
						|| f[j].getName().endsWith("png")
						|| f[j].getName().endsWith("bmp"))
				{
					try
					{
						InputStream inputStream = new FileInputStream(f[j]);
						Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						map.put("ItemImage", bitmap); // 图片

						String name = f[j].getName();
						map.put("ItemTitle",
								name.substring(0, name.length() - 4)); // 文字
					} catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					break;
				}
			}
		}
		return map;
	}

	Boolean check_condition(JSONObject json, String[] keys, String[] values)
	{
		if (json == null)
			return false;
		for (int i = 0; i < values.length; i++)
		{
			if (json.has(keys[i]))
			{
				try
				{
					if (!json.getString(keys[i]).equals(values[i]))
					{
						return false;
					}
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	JSONObject getJsonStr(File file)
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

	void queryButton(String fileDir, String[] keys, String[] values)
	{
		JSONObject json = null;
		if (files.length == 0)
			return;

		listItems.clear();

		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			json = getJsonStr(file);
			if (check_condition(json, keys, values))
			{
				listItems.add(getData(file));
			}
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
			initListView();
			ButtonCfgLlist.this.setListAdapter(listItemAdapter);
		}
	}
}
