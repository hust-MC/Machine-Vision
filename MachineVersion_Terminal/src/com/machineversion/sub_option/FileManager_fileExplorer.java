package com.machineversion.sub_option;

import android.os.Bundle;
import android.os.Environment;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.app.AlertDialog;
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
	private final String ROOT_DIRECTORY = Environment
			.getExternalStorageDirectory().getPath();
	private String mDir = ROOT_DIRECTORY;

	private final Context CONTEXT = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		refreshListView();
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

		if (!mDir.equals(ROOT_DIRECTORY))
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

	private void refreshListView()
	{
		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		if ((Integer) mData.get(position).get("img") == R.drawable.ex_folder)		// Object对象只能转为Integer
		{
			mDir = (String) mData.get(position).get("info");
			refreshListView();
		}
		else
		{
			// finishWithResult((String) mData.get(position).get("info"));
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
		if (((AdapterContextMenuInfo) menuInfo).position == 0
				&& mDir != ROOT_DIRECTORY)
		{
			return;
		}
		menu.add(0, 2, menuOrder++, "重命名");
		menu.add(0, 3, menuOrder, "删除");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		final File file = new File((String) mData.get(info.position)
				.get("info"));
		switch (item.getItemId())
		{
		case 1:
			/*
			 * 新建文件夹
			 */
			File newFile = new File(mDir, "新建文件夹");
			newFile.mkdir();
			refreshListView();
			break;

		case 2:
			/*
			 * 重命名
			 */
			LinearLayout layout = (LinearLayout) LayoutInflater.from(CONTEXT)
					.inflate(R.layout.filemanager_rename, null);
			final EditText editText = (EditText) layout
					.findViewById(R.id.filemanager_rename);
			editText.setHint(file.getName());
			new AlertDialog.Builder(CONTEXT).setTitle("文件重命名").setView(layout)
					.setPositiveButton("确定", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							File newFile = new File(file.getParentFile(),
									editText.getText().toString());
							file.renameTo(newFile);
							refreshListView();
						}
					}).setNegativeButton("取消", null).show();
			break;

		case 3:
			/*
			 * 刪除文件
			 */
			new AlertDialog.Builder(CONTEXT).setTitle("是否删除文件？")
					.setPositiveButton("确定", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							deleteDirectory(file);
							refreshListView();
							Toast.makeText(CONTEXT, "文件已删除", Toast.LENGTH_SHORT)
									.show();
						}
					}).setNegativeButton("取消", null).show();
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
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
		}
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
}
