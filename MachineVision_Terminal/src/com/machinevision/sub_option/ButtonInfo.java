package com.machinevision.sub_option;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.machinevision.sub_option.ButtonCfgPanel.OnButtonClick;
import com.machinevision.terminal.R;
import com.machinevision.common_widget.DialogWindow;
import com.machinevision.common_widget.EToast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class ButtonInfo extends Activity implements OnButtonClick
{
	private ButtonProfile buttonProfile;
	private JSONObject json;

	DialogWindow dialogWindow;
	
	protected ListView listview1;
	protected ListView listview2;
	protected ListView listview3;
	
	private ImageView image;	
	private Button bt_modify;
	private Button bt_delete;
	private Button bt_exit;
	private Button bt_save_as;
	private Button bt_save;
	
	private CustomAdapter mAdapter1;
	private CustomAdapter mAdapter2;
	private CustomAdapter mAdapter3;
	
	public static void actionStart(Context context, String data) {
		Intent intent = new Intent(context, ButtonInfo.class);
		intent.putExtra("File", data);
		((Activity)context).startActivityForResult(intent, 1);
	};

	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_info);
		initWidget();
		
		Intent intent = getIntent();
		String filename = intent.getStringExtra("File");
		
		buttonProfile = new ButtonProfile(filename);
		json = buttonProfile.getJsonStr();
		image.setImageBitmap(buttonProfile.getBitmap());
	}

	void initWidget()
	{
		listview1 = (ListView) this.findViewById(R.id.MyListView1);
		listview2 = (ListView) this.findViewById(R.id.MyListView2);
		listview3 = (ListView) this.findViewById(R.id.MyListView3);
		bt_modify = (Button) this.findViewById(R.id.bt_modify);
		bt_delete = (Button) this.findViewById(R.id.bt_delete);
		bt_exit = (Button) this.findViewById(R.id.exit);
		bt_save_as = (Button) this.findViewById(R.id.bt_save_as);
		bt_save = (Button) this.findViewById(R.id.bt_overwrite);
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
				View view1 = new ButtonCfgPanel(ButtonInfo.this, getResources().getStringArray(R.array.buttoninfo_config),
						getResources().getStringArray(R.array.buttoninfo_menu), 
						getResources().getStringArray(R.array.buttoninfo_type), null).getView();	
				
				dialogWindow = new DialogWindow.Builder(ButtonInfo.this)
				.setTitle("钮扣配置").setView(view1).create();
				dialogWindow.show();
				break;
			case R.id.bt_save_as:
				LayoutInflater inflater = (LayoutInflater) ButtonInfo.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View view2 = inflater.inflate(R.layout.edit_text, null);
				DialogWindow dialog_saveAs = new DialogWindow.Builder(ButtonInfo.this)
				.setTitle("配置文件命名").setView(view2)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						String saveName = ((EditText) view2.findViewById(R.id.editText1)).getText().toString();
						ButtonProfile.saveInfo(saveName, json, buttonProfile.getBitmap());
						EToast.makeText(ButtonInfo.this, "另存为成功", Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton("取消", null).create();
				dialog_saveAs.showWrapContent();
				break;
			case R.id.bt_overwrite:
				DialogWindow dialog_overwrite = new DialogWindow.Builder(ButtonInfo.this)
				.setTitle("你将覆盖原有的配置文件,是否继续？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0,
							int arg1)
					{
						buttonProfile.write2sd(json);
						EToast.makeText(ButtonInfo.this, "保存成功", Toast.LENGTH_SHORT).show();
						onButtonClicked(RESULT_OK);
					}

				}).setNegativeButton("取消", null).create();
				dialog_overwrite.showWrapContent();
				break;
			case R.id.bt_delete:
				DialogWindow dialog_delete = new DialogWindow.Builder(ButtonInfo.this)
				.setTitle("确定删除？")
				.setPositiveButton("确定",new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0,
							int arg1)
					{
						buttonProfile.Delete();
						EToast.makeText(ButtonInfo.this, "删除成功", Toast.LENGTH_SHORT).show();
						onButtonClicked(RESULT_OK);
					}
				}).setNegativeButton("取消", null).create();
				dialog_delete.showWrapContent();
				break;
			case R.id.exit:
				EToast.makeText(ButtonInfo.this, "退出成功", Toast.LENGTH_SHORT).show();
				onButtonClicked(RESULT_OK);
				break;
			default:
				break;
			}

		}
	}
	
	private void refreshView()
	{
		mAdapter1.notifyDataSetChanged();
		mAdapter2.notifyDataSetChanged();
		mAdapter3.notifyDataSetChanged();
	}
	
	private final class CustomAdapter extends BaseAdapter
	{
		private String[] names = null;
		private int images = R.drawable.switch_btn_slipper;

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
				view = mInflater.inflate(R.layout.list_buttoninfo, parent, false);
			}

			ImageView iv_header = (ImageView) view.findViewById(R.id.ItemImage1);
			TextView tv_name = (TextView) view.findViewById(R.id.name);
			TextView tv_content = (TextView) view.findViewById(R.id.content);

			iv_header.setImageResource(images);
			tv_name.setText(names[position] + ":");
			if (json.has(names[position]))
			{
				try
				{
					tv_content.setText(json.getString(names[position]).toString());
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

	/**
	 * @param resId
	 */
	public void onButtonClicked(int resId)
	{
		setResult(resId);
		finish();
		overridePendingTransition(0, R.anim.top_out);
	}

	@Override
	public void onPositiveButtonClicked(String[] key, String[] value) {
		for (int i = 0; i < key.length; i++) 
		{		
			try 
			{
				json.put(key[i], value[i]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dialogWindow.dismiss();
		refreshView();
	}
	
	@Override
	public void onCannelButtonClicked() {
		// TODO Auto-generated method stub
//		onButtonClicked(RESULT_OK);
		dialogWindow.dismiss();
	}
}
