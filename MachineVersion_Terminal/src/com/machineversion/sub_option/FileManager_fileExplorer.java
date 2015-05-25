package com.machineversion.sub_option;

import android.os.Bundle;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.util.Log;
import android.content.Intent;
import android.app.ListActivity;
import android.view.WindowManager;
import android.view.Display;
import android.view.WindowManager.LayoutParams;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

import com.machineversion.terminal.R;

public class FileManager_fileExplorer extends ListActivity
{
	private List<Map<String, Object>> mData;
	private String mDir = "/sdcard";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay();
		LayoutParams p = getWindow().getAttributes();
		p.height = (int) (d.getHeight() * 0.8);
		p.width = (int) (d.getWidth() * 0.95);
		getWindow().setAttributes(p);
	}
	private List<Map<String, Object>> getData()
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		File f = new File(mDir);
		File[] files = f.listFiles();

		if (!mDir.equals("/sdcard"))
		{
			map = new HashMap<String, Object>();
			map.put("title", "..");
			map.put("info", f.getParent());
			map.put("img", R.drawable.ex_folder);
			list.add(map);
		}
		if (files != null)
		{
			for (int i = 0; i < files.length; i++)
			{
				map = new HashMap<String, Object>();
				map.put("title", files[i].getName());
				map.put("info", files[i].getPath());
				if (files[i].isDirectory())
					map.put("img", R.drawable.ex_folder);
				else
					map.put("img", R.drawable.ex_doc);
				list.add(map);
			}
		}
		return list;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		Log.d("MyListView4-click", (String) mData.get(position).get("info"));
		if ((Integer) mData.get(position).get("img") == R.drawable.ex_folder)				// Object对象只能转为Integer
		{
			mDir = (String) mData.get(position).get("info");
			mData = getData();
			MyAdapter adapter = new MyAdapter(this);
			setListAdapter(adapter);
		}
		else
		{
			finishWithResult((String) mData.get(position).get("info"));
		}
	}

	public class MyAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;

		public MyAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount()
		{
			return mData.size();
		}

		public Object getItem(int arg0)
		{
			return null;
		}

		public long getItemId(int arg0)
		{
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listview, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) mData.get(position).get(
					"img"));
			holder.title.setText((String) mData.get(position).get("title"));
			holder.info.setText((String) mData.get(position).get("info"));
			return convertView;
		}
	}

	public final class ViewHolder
	{
		public ImageView img;
		public TextView title;
		public TextView info;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		int menuOrder = Menu.FIRST;
		menu.setHeaderTitle("文件操作");
		menu.add(0, 1, menuOrder++, "新建文件夹");
		menu.add(0, 2, menuOrder++, "重命名");
		menu.add(0, 3, menuOrder, "删除");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId())
		{
		case 1:
			Log.d("ZY", "id = " + info.id);
			Log.d("ZY", "position = " + info.position);
			Log.d("ZY", "view = " + info.targetView);

			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	private void finishWithResult(String path)
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("file path", path);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}
};
