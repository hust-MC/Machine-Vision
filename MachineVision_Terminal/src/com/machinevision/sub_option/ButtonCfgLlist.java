package com.machinevision.sub_option;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.machinevision.terminal.FileDirectory;
import com.machinevision.terminal.R;
import com.machinevision.common_widget.EToast;

import android.R.integer;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class ButtonCfgLlist extends Activity{
	
	private static final int CONDITION_NUM = 5; // 查询条件数
	
	private Spinner[] spiners = new Spinner[CONDITION_NUM];
	private StringBuilder[] stringBuilders = new StringBuilder[CONDITION_NUM];

	private CheckBox checkBox1, checkBox2;
	private EditText Button_ID;
	private Button bt_qurey, bt_exit;
	private ImageButton bt_top, bt_bottom;
	private ListView listView;	

	private List<ButtonProfileInfo> ButtonProfileList = new ArrayList<ButtonProfileInfo>();
	private ButtonProfileInfoAdapter adapter;
	File[] files;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_cfglist);
		initWidget();
		initSpiner(getResources().getString(R.string.option_machine_query_item));
		try {
			initListView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		adapter = new ButtonProfileInfoAdapter(this, R.layout.list_item, ButtonProfileList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String data = ButtonProfileList.get(position).getName();
				ButtonInfo.actionStart(ButtonCfgLlist.this, data);
			}
		});
	}
	
	void initWidget()
	{
		bt_qurey = (Button) findViewById(R.id.bt_query);
		bt_exit = (Button) findViewById(R.id.bt_exit);
		bt_top = (ImageButton) findViewById(R.id.bt_control_up);
		bt_bottom = (ImageButton) findViewById(R.id.bt_control_down);
		
		ButtonListerner ButtonListerner = new ButtonListerner();
		bt_qurey.setOnClickListener(ButtonListerner);
		bt_exit.setOnClickListener(ButtonListerner);
		bt_top.setOnClickListener(ButtonListerner);
		bt_bottom.setOnClickListener(ButtonListerner);
		
		spiners[0] = (Spinner) findViewById(R.id.spinnerId1);
		spiners[1] = (Spinner) findViewById(R.id.spinnerId2);
		spiners[2] = (Spinner) findViewById(R.id.spinnerId3);
		spiners[3] = (Spinner) findViewById(R.id.spinnerId4);
		spiners[4] = (Spinner) findViewById(R.id.spinnerId5);

		checkBox1 = (CheckBox) findViewById(R.id.machine_learing_query_option1);
		checkBox2 = (CheckBox) findViewById(R.id.machine_learing_query_option2);
		Button_ID = (EditText) findViewById(R.id.machine_learning_button_id);
		listView = (ListView) findViewById(R.id.list_view);
	}
	

	class ButtonListerner implements android.view.View.OnClickListener 
	{
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
			case R.id.bt_query:
				// Perform action on click
				if (!checkBox1.isChecked() && !checkBox2.isChecked()) 
					EToast.makeText(ButtonCfgLlist.this, "请选择查询方式", Toast.LENGTH_SHORT).show();
				else if (checkBox1.isChecked()) 
				{
					String name = Button_ID.getText().toString();
					if (TextUtils.isEmpty(name))
						EToast.makeText(ButtonCfgLlist.this, "请输入钮扣ID号", Toast.LENGTH_SHORT).show();
					else 
					{
						ButtonProfileList.clear();
						try {
							CheckID(name);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						adapter.notifyDataSetChanged();
					}
				} 
				else 
				{
					String condition = getResources().getString(R.string.option_machine_check_condition);					
					QueryButton(condition.split(","), stringBuilders);
					adapter.notifyDataSetChanged();
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

	class SpinnerOnSelectedListener 
	{		
		private Spinner[] spinners;
		private StringBuilder[] stringBuilders;
		private String[] types;
		private Context context;
		
		SpinnerOnSelectedListener(Context _contexts, Spinner[] _spinners, StringBuilder[] _stringBuilders, String strType)
		{
			context = _contexts; 
			types = strType.split(",");
			spinners = _spinners;
			stringBuilders = _stringBuilders;
		}

		void bind() 
		{
			for (int i = 0; i < spinners.length; i++)
			{
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner1, types[i].substring(1).split("n"));				
				ActionListener actionListener = new ActionListener(i);		
				spinners[i].setOnItemSelectedListener(actionListener);
				spinners[i].setAdapter(adapter);
			}
		}

		class ActionListener implements OnItemSelectedListener 
		{
			private int num;
			public  ActionListener(int _num) {
				num = _num;
			}
			
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				String selected = adapterView.getItemAtPosition(position).toString();
				stringBuilders[num] = new StringBuilder(selected);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// TODO Auto-generated method stub
				System.out.println("未选择");
			}
		}
	}

	public void onDirectionClicked(int resId) {
		int position = 0;
		switch (resId) {
		case R.id.bt_control_up:
			position = listView.getFirstVisiblePosition() - 2;
			if (position < 0) {
				position = 0;
			}
			break;
		case R.id.bt_control_down:
			position = listView.getLastVisiblePosition() + 2;
			if (position >= listView.getCount()) {
				position = listView.getCount()-1;
			}
			break;
		default:
			break;
		}
		listView.smoothScrollToPosition(position);
	}

	private void initSpiner(String strType) 
	{		
		new SpinnerOnSelectedListener(this, spiners, stringBuilders, strType).bind();
	}
	
	/**
	 * 钮扣信息结构体
	 * @author hanyu
	 */
	private class ButtonProfileInfo
	{
		private String name;
		private Bitmap Image;
		private String manufature;
		private String time;	
		
		ButtonProfileInfo (File file) throws JSONException
		{
			ButtonProfile buttonProfile = new ButtonProfile(file);
			name = buttonProfile.getID();
			Image = buttonProfile.getBitmap();
			
			JSONObject jsonObject = buttonProfile.getJsonStr();
			if (jsonObject.has("Manufature"))
				manufature = "厂家:" + jsonObject.getString("Manufature");
			else 
				manufature = "厂家:未设置";
			
			if (jsonObject.has("time"))
				time = jsonObject.getString("time");
			else 
				time = "未设置";
		}
		
		public Bitmap getImage() {
			return Image;
		}
		public String getName() {
			return name;
		}
		public String getManufature() {
			return manufature;
		}
		public String getTime() {
			return time;
		}
	}
	
	/**
	 * 设置适配器内容
	 * @throws JSONException 
	 */
	private void initListView() throws JSONException  
	{
		File parentFile = new File(FileDirectory.getLeaningDirectory());
		if (!parentFile.exists())
			parentFile.mkdirs();
		files = parentFile.listFiles();
		
		ButtonProfileList.clear();
		for (int i = 0; i < files.length; i++) 
		{
			ButtonProfileInfo buttonProfileInfo = new ButtonProfileInfo(files[i]);
			ButtonProfileList.add(buttonProfileInfo);
		}
	}

	/**
	 * 适配器
	 * @author hanyu
	 *
	 */
	public class ButtonProfileInfoAdapter extends ArrayAdapter<ButtonProfileInfo> {
		private int resourceId;	

		public ButtonProfileInfoAdapter(Context context, int textViewResourceId,
				List<ButtonProfileInfo> objects) 
		{
			super(context, textViewResourceId, objects);
			resourceId = textViewResourceId;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ButtonProfileInfo buttonProfileInfo = getItem(position);
			
			View view = LayoutInflater.from(getContext()).inflate(resourceId, null); 
			ImageView ItemImage = (ImageView) view.findViewById(R.id.ItemImage);
			TextView ItemTitle = (TextView) view.findViewById(R.id.ItemTitle);
			TextView ItemTime = (TextView) view.findViewById(R.id.ItemTime);
			TextView ItemManufature = (TextView) view.findViewById(R.id.ItemManufature);
			
			ItemTitle.setText(buttonProfileInfo.getName());
			ItemManufature.setText(buttonProfileInfo.getManufature());
			ItemTime.setText(buttonProfileInfo.getTime());
			ItemImage.setImageBitmap(buttonProfileInfo.getImage());

			return view;
		}
	}
	
	HashMap<String, Object> getData(File file) throws JSONException 
	{
		HashMap<String, Object> map = new HashMap<String, Object>();		
		if (file.isDirectory()) 
		{
			ButtonProfile buttonProfile = new ButtonProfile(file);
			Bitmap bitmap = buttonProfile.getBitmap();
			map.put("ItemImage", bitmap); // 图片
			map.put("ItemTitle", "钮扣:" + buttonProfile.getID());
			JSONObject JsonFile = buttonProfile.getJsonStr();			
			if (JsonFile.has("time")) 
				map.put("EditTime", JsonFile.getString("time"));
			else
				map.put("EditTime", "未设置");

			if (JsonFile.has("Manufature")) 
				map.put("ItemManufature","厂家:" + JsonFile.getString("Manufature"));
			else
				map.put("Manufature", "未设置");
		}
		return map;
	}


	void CheckID(String id) throws JSONException 
	{
		for (int i = 0; i < files.length; i++)
		{
			ButtonProfile buttonProfile = new ButtonProfile(files[i]);
			if (buttonProfile.hasName(id))
			{
				ButtonProfileInfo buttonProfileInfo = new ButtonProfileInfo(files[i]);
				ButtonProfileList.add(buttonProfileInfo);
			}
		}
	}

	void QueryButton(String[] keys, StringBuilder[] values) 
	{
		if (files.length == 0) 
		{
			return;
		}
		ButtonProfileList.clear();
		for (int i = 0; i < files.length; i++) 
		{
			ButtonProfile buttonProfile = new ButtonProfile(files[i]);
			try 
			{	
				if (buttonProfile.checkCondition(keys, values)) 
				{
					ButtonProfileInfo buttonProfileInfo = new ButtonProfileInfo(files[i]);
					ButtonProfileList.add(buttonProfileInfo);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * 设置成功之后处理设置的数据
		 */
		if (resultCode == RESULT_OK) 
		{
			try {
				initListView();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter.notifyDataSetChanged();
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
