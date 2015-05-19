package com.machineversion.option;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.machineversion.net.CmdHandle;
import com.machineversion.sub_option.FileManager_fileExplorer;
import com.machineversion.terminal.R;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FileManager extends ControlPannelActivity
{
	final int REQUEST_FILE = 1;

	static Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
		}

	};

	@Override
	protected void init_widget()
	{
		super.init_widget();

		listview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				startActivityForResult(new Intent(FileManager.this,
						FileManager_fileExplorer.class), REQUEST_FILE);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		wholeMenu = new MenuWithSubMenu(R.array.option_file_manager, 0);
		init_widget();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent i)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == REQUEST_FILE)
			{
				final Intent intent = i;

				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						int count = 0;
						byte[] buffer = new byte[1024];

						String path = intent.getExtras().getString("file path");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						FileInputStream fis;
						try
						{
							fis = new FileInputStream(path);
							while ((count = fis.read(buffer)) != -1)
							{
								baos.write(buffer, 0, count);
							}
						} catch (FileNotFoundException e)
						{
							e.printStackTrace();
						} catch (IOException e)
						{
							e.printStackTrace();
						}

						BitmapFactory.Options option = new BitmapFactory.Options();
						option.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(path, option);

						CmdHandle cmd = CmdHandle.getInstance();
						cmd.sendImage(null, option.outWidth, option.outHeight,
								(int) new File(path).length(),
								baos.toByteArray());
					}
				}).start();
			}
		}
	}
}
