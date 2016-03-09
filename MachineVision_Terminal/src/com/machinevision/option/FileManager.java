package com.machinevision.option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.machinevision.terminal.FileDirectory;
import com.machinevision.terminal.MainActivity;
import com.machinevision.terminal.R;
import com.machinevision.common_widget.EToast;
import com.machinevision.net.CmdHandle;
import com.machinevision.sub_option.FileManager_fileExplorer;
import com.machinevision.sub_option.SVM;

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
	public static final int SEND_FILE = 3; // 从U盘拷贝文件
	public static final int MOVE_FILE = 4; // 从U盘拷贝文件

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
				case 0:
					intent.putExtra("firstDir", FileDirectory.getAppDirectory());
					startActivityForResult(intent, SEND_FILE);
					break;
				case 1:
					intent.putExtra("firstDir", FileDirectory.getUsbDirectory());
					startActivityForResult(intent, TRANSFER_FILE);
					break;
				case 2:
					intent.putExtra("firstDir", FileDirectory.getAppDirectory());
					startActivityForResult(intent, MOVE_FILE);
					break;
				case 3:
					ROMandSDcardInfo romSDcardInfo = new ROMandSDcardInfo(
							FileManager.this);
					TextView textView = ((TextView) findViewById(R.id.file_manager_tv));
					textView.setText("内存总量：" + romSDcardInfo.getRomTotalSize()
							+ "\n" + "内存剩余："
							+ romSDcardInfo.getRomAvailableSize() + "\n"
							+ "SD卡总量：" + romSDcardInfo.getSDTotalSize() + "\n"
							+ "SD卡剩余：" + romSDcardInfo.getSDAvailableSize());
					textView.setVisibility(View.VISIBLE);
					break;
				case 4:
					Intent intent1 = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("ShutDown", "y");
					intent1.putExtras(bundle);
					setResult(RESULT_OK, intent1);
					finish();
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

			if (requestCode == REQUEST_FILE) {
				TextView textView = ((TextView) findViewById(R.id.file_manager_tv));
				textView.setText(content.toString());
				textView.setVisibility(View.VISIBLE);
			}

			/*
			 * 从U盘读取文件到本地
			 */
			if (requestCode == TRANSFER_FILE) {
				String name = i
						.getStringExtra(FileManager_fileExplorer.RESULT_FILE_NAME);
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
				EToast.makeText(FileManager.this, "单个导入成功", Toast.LENGTH_SHORT)
						.show();
			}

			
			if (requestCode == MOVE_FILE) {
				String name = i.getStringExtra(FileManager_fileExplorer.RESULT_FILE_NAME);
				File file = new File(FileDirectory.getAlgUSBDirectory() + name);
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
				EToast.makeText(FileManager.this, "写入U盘成功", Toast.LENGTH_SHORT)
						.show();
			}
			
			if (requestCode == SEND_FILE) {
				String name = i
						.getStringExtra(FileManager_fileExplorer.RESULT_FILE_NAME);
				String path = i
						.getStringExtra(FileManager_fileExplorer.RESULT_FILE_PATH);
				int len = content.length();

				System.out.println("----content->" + content);
				System.out.println("----name-->" + name);
				System.out.println("----namelen-->" + name.length());
				System.out.println("----path-->" + path);
				System.out.println("----pathlen-->" + path.length());

				// content.insert(0, (int)content.length());
				// System.out.println("----len-->" + content.length());s

				char[] SeqPath = new char[256];
				char[] SeqLen = new char[4];

				if ((path.length() > 256)) {
					EToast.makeText(this, "文件名过长，超过256个字节", EToast.LENGTH_SHORT)
							.show();
					return;
				}

				for (int j = 0; j < 256; j++) {
					if (j < path.length()) {
						SeqPath[j] = path.charAt(j);
					} else {
						SeqPath[j] = '0';
					}
				}

				for (int j = 0; j < 4; j++) {
					SeqLen[j] = (char) (len & 0xff);
					len = len >> 8;
				}

				content.insert(0, SeqLen, 0, SeqLen.length);
				content.insert(0, SeqPath, 0, SeqPath.length);

				System.out.println("----content->" + content);
				System.out.println("----len-->" + content.length());

				CmdHandle cmdHandle = MainActivity.getCmdHandle(1);
				if (cmdHandle == null) {
					EToast.makeText(this, "网络未连接", EToast.LENGTH_SHORT).show();
				} else {
					String c = content.toString();
					cmdHandle.sendBinary(c.getBytes());
					EToast.makeText(this, "发送成功", EToast.LENGTH_SHORT).show();
				}

			}
		}
	}

	class ROMandSDcardInfo {
		Context context;

		ROMandSDcardInfo(Context _context) {
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
			return Formatter.formatFileSize(context, blockSize * totalBlocks);
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
			return Formatter.formatFileSize(context, blockSize * totalBlocks);
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
