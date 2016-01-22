package com.machinevision.option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.machinevision.terminal.FileDirectory;
import com.machinevision.terminal.R;
import com.machinevision.common_widget.EToast;
import com.machinevision.sub_option.FileManager_fileExplorer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FileManager extends ControlPannelActivity {
	public static final int REQUEST_FILE = 1; //
	public static final int TRANSFER_FILE = 2; // 从U盘拷贝文件

	static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
	};

	@Override
	protected void init_widget() {
		super.init_widget();
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(FileManager.this,
						FileManager_fileExplorer.class);
				switch (arg2) {
				case 2:
					intent.putExtra("firstDir", FileDirectory.getUsbDirectory());
					startActivityForResult(intent, TRANSFER_FILE);
					break;
				case 4:
					ROMandSDcardInfo romSDcardInfo = new ROMandSDcardInfo(FileManager.this);
					TextView textView = ((TextView) findViewById(R.id.file_manager_tv));
					textView.setText("内存总量：" + romSDcardInfo.getRomTotalSize() + "\n" +
							"内存剩余：" + romSDcardInfo.getRomAvailableSize() + "\n"+
							"SD卡总量：" + romSDcardInfo.getSDTotalSize() + "\n"+
							"SD卡剩余：" + romSDcardInfo.getSDAvailableSize());
					textView.setVisibility(View.VISIBLE);
					break;
				default:
					startActivityForResult(intent, REQUEST_FILE);
					break;
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		wholeMenu = new MenuWithSubMenu(R.array.option_file_manager, 0);
		init_widget();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent i) {
		if (resultCode == RESULT_OK) {
			
			String temp = null;
			StringBuffer content = new StringBuffer();
			try {
				BufferedReader reader = new BufferedReader(
						new FileReader(
								i.getStringExtra(FileManager_fileExplorer.RESULT_FILE_PATH)));

				while (!TextUtils.isEmpty(temp = reader.readLine())) {
					content.append(temp);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (requestCode == REQUEST_FILE) 
			{
				TextView textView = ((TextView) findViewById(R.id.file_manager_tv));
				textView.setText(content.toString());
				textView.setVisibility(View.VISIBLE);
			}
			
			
			/*
			 * 从U盘读取文件到本地
			 */
		    if (requestCode == TRANSFER_FILE) 
		    {				
				String name = i.getStringExtra(FileManager_fileExplorer.RESULT_FILE_NAME);
				File file = new File(FileDirectory.getAlgDirectory() + name);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				FileWriter writer;
				try {
					writer = new FileWriter(file);
					writer.write(content.toString());
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				EToast.makeText(FileManager.this, "单个导入成功",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class ROMandSDcardInfo
	{
		Context context;
		ROMandSDcardInfo(Context _context)
		{
			context = _context;
		}
		
		/**
		 * 获得SD卡总大小
		 * 
		 * @return
		 */
		public String getSDTotalSize() {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return Formatter.formatFileSize(context, blockSize
					* totalBlocks);
		}

		/**
		 * 获得sd卡剩余容量，即可用大小
		 * 
		 * @return
		 */
		public String getSDAvailableSize() {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return Formatter.formatFileSize(context, blockSize
					* availableBlocks);
		}

		/**
		 * 获得机身内存总大小
		 * 
		 * @return
		 */
		public String getRomTotalSize() {
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return Formatter.formatFileSize(context, blockSize
					* totalBlocks);
		}

		/**
		 * 获得机身可用内存
		 * 
		 * @return
		 */
		public String getRomAvailableSize() {
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return Formatter.formatFileSize(context, blockSize
					* availableBlocks);
		}
	};
}
