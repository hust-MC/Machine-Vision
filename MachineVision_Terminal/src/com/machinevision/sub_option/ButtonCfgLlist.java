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
import com.machinevision.common_widget.EToast;
import com.machinevision.option.MachineLearning;

import android.R.integer;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class ButtonCfgLlist extends ListActivity {
	public static final int SHOW_INFO = 1;

	private Spinner spiner1, spiner2, spiner3, spiner4, spiner5;
	private String[] vals = { null, null, null, null, null };

	private CheckBox checkBox1, checkBox2;
	private EditText Button_ID;
	private Button Button_Query, Button_Exit;
	private ImageButton bt_top, bt_bottom;

	private ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>(); // 存放文字、图片信息
	private SimpleAdapter listItemAdapter; // 适配器
	File[] files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_cfglist);

		Button_Query = (Button) findViewById(R.id.bt_query);
		Button_Exit = (Button) findViewById(R.id.bt_exit);
		bt_top = (ImageButton) findViewById(R.id.bt_control_up);
		bt_bottom = (ImageButton) findViewById(R.id.bt_control_down);
		
		ButtonListern buttonListern = new ButtonListern();
		Button_Query.setOnClickListener(buttonListern);
		Button_Exit.setOnClickListener(buttonListern);
		bt_top.setOnClickListener(buttonListern);
		bt_bottom.setOnClickListener(buttonListern);

		spiner1 = (Spinner) findViewById(R.id.spinnerId1);
		spiner2 = (Spinner) findViewById(R.id.spinnerId2);
		spiner3 = (Spinner) findViewById(R.id.spinnerId3);
		spiner4 = (Spinner) findViewById(R.id.spinnerId4);
		spiner5 = (Spinner) findViewById(R.id.spinnerId5);

		new SpinnerOnSelectedListener(spiner1, 0).bind();
		new SpinnerOnSelectedListener(spiner2, 1).bind();
		new SpinnerOnSelectedListener(spiner3, 2).bind();
		new SpinnerOnSelectedListener(spiner4, 3).bind();
		new SpinnerOnSelectedListener(spiner5, 4).bind();

		checkBox1 = (CheckBox) findViewById(R.id.machine_learing_query_option1);
		checkBox2 = (CheckBox) findViewById(R.id.machine_learing_query_option2);
		Button_ID = (EditText) findViewById(R.id.machine_learning_button_id);

		initSpiner(getResources().getString(R.string.option_machine_query_item));
		initListView();
		ButtonCfgLlist.this.setListAdapter(listItemAdapter);
	}

	class ButtonListern implements android.view.View.OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_query:
				// Perform action on click
				if (!checkBox1.isChecked() && !checkBox2.isChecked()) {
					EToast.makeText(ButtonCfgLlist.this, "请选择查询方式",
							Toast.LENGTH_SHORT).show();
				} else if (checkBox1.isChecked()) {
					String str1 = Button_ID.getText().toString();
					if (TextUtils.isEmpty(str1))
						EToast.makeText(ButtonCfgLlist.this, "请输入钮扣ID号",
								Toast.LENGTH_SHORT).show();
					else {
						CheckID(str1);
						listItemAdapter.notifyDataSetChanged();
					}
				} else {
					String str2 = getResources().getString(
							R.string.option_machine_check_condition);
					String[] keys = str2.split(",");
					QueryButton(keys, vals);
					listItemAdapter.notifyDataSetChanged();
				}
				break;
			case R.id.bt_exit:
				finishWithAnim();
				break;
			case R.id.bt_control_up:
			case R.id.bt_control_down:
				onDirectionClicked(v.getId());
				break;
			}

		}
	}

	class SpinnerOnSelectedListener {
		private Spinner spinner;
		private int num;

		SpinnerOnSelectedListener(Spinner _spinner, int _num) {
			spinner = _spinner;
			num = _num;
		}

		void bind() {
			ActionListener actionListener = new ActionListener();
			spinner.setOnItemSelectedListener(actionListener);
		}

		class ActionListener implements OnItemSelectedListener {
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				String selected = adapterView.getItemAtPosition(position)
						.toString();
				vals[num] = selected;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// TODO Auto-generated method stub
				System.out.println("nothingSelected");
			}
		}
	}

	public void onDirectionClicked(int resId) {
		int position = 0;
		switch (resId) {
		case R.id.bt_control_up:
			position = getListView().getFirstVisiblePosition() - 2;
			if (position < 0) {
				position = 0;
			}
			break;
		case R.id.bt_control_down:
			position = getListView().getLastVisiblePosition() + 2;
			if (position >= getListView().getCount()) {
				position = getListView().getCount()-1;
			}
			break;
		default:
			break;
		}
		getListView().smoothScrollToPosition(position);
	}

	/**
	 * 点击事件
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, ButtonInfo.class);
		String name = ((String) listItems.get(position).get("ItemTitle"))
				.split(":")[1];
		intent.putExtra("file", name);
		startActivityForResult(intent, SHOW_INFO);
	}

	private void initSpiner(String strType) {
		String[] type = strType.split(",");
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
	private void initListView() {
		File parentFile = new File(MachineLearning.FILE_DIR);
		if (!parentFile.exists())
			parentFile.mkdirs();

		files = new File(MachineLearning.FILE_DIR).listFiles();

		listItems.clear();
		for (int i = 0; i < files.length; i++) {
			listItems.add(getData(files[i]));
		}

		listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
				R.layout.list_item, // ListItem的XML布局实现
				new String[] { "ItemTitle", "ItemManufature", "ItemImage",
						"EditTime" }, // 动态数组与ImageItem对应的子项
				// 的ID
				new int[] { R.id.ItemTitle, R.id.ItemManufature,
						R.id.ItemImage, R.id.EditTime }

		);

		listItemAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// 判断是否为我们要处理的对象
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap) data);
					return true;
				} else
					return false;
			}
		});
	}

	HashMap<String, Object> getData(File file) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		if (file.isDirectory()) {
			File[] f = file.listFiles();
			for (int j = 0; j < f.length; j++) {
				if (f[j].getName().endsWith("jpg")
						|| f[j].getName().endsWith("png")
						|| f[j].getName().endsWith("bmp")) {
					try {
						InputStream inputStream = new FileInputStream(f[j]);
						Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						map.put("ItemImage", bitmap); // 图片

						String name = f[j].getName();
						map.put("ItemTitle",
								"钮扣:" + name.substring(0, name.length() - 4)); // 文字
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					break;
				}
			}

			JSONObject JsonFile = getJsonStr(file);
			if (JsonFile.has("time")) {
				try {
					map.put("EditTime", JsonFile.getString("time"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				map.put("EditTime", "未设置");

			if (JsonFile.has("Manufature")) {
				try {
					map.put("ItemManufature",
							"厂家:" + JsonFile.getString("Manufature"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				map.put("Manufature", "未设置");
		}
		return map;
	}

	Boolean check_condition(JSONObject json, String[] keys, String[] values) {
		if (json == null)
			return false;
		for (int i = 0; i < values.length; i++) {
			if (json.has(keys[i])) {
				try {
					if (!json.getString(keys[i]).equals(values[i])) {
						return false;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public static JSONObject getJsonStr(File file) {

		JSONObject json = null;

		File[] f = file.listFiles();
		for (int j = 0; j < f.length; j++) {
			if (f[j].getName().endsWith("ini")) {
				try {
					String str = null;
					StringBuffer strBuf = new StringBuffer();
					BufferedReader reader = new BufferedReader(new FileReader(
							f[j]));
					while (!TextUtils.isEmpty(str = reader.readLine())) {
						strBuf.append(str);
					}
					json = new JSONObject(strBuf.toString());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return json;
	}

	void CheckID(String id) {
		listItems.clear();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().contains(id))
				listItems.add(getData(file));
		}
	}

	void QueryButton(String[] keys, String[] values) {
		JSONObject json = null;
		if (files.length == 0) {
			return;
		}

		listItems.clear();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			json = getJsonStr(file);
			if (check_condition(json, keys, values)) {
				listItems.add(getData(file));
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * 设置成功之后处理设置的数据
		 */
		if (resultCode == RESULT_OK) {
			initListView();
			ButtonCfgLlist.this.setListAdapter(listItemAdapter);
		}
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
}
