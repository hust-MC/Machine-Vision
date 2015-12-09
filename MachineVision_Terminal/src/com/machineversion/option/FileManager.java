package com.machineversion.option;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.machineversion.net.CmdHandle;
import com.machineversion.sub_option.FileManager_fileExplorer;
import com.machineversion.terminal.R;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent i)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == REQUEST_FILE)
			{
				try
				{
					String temp = null;
					StringBuffer content = new StringBuffer();
					BufferedReader reader = new BufferedReader(
							new FileReader(
									i.getStringExtra(FileManager_fileExplorer.RESULT_FILE_PATH)));

					while (!TextUtils.isEmpty(temp = reader.readLine()))
					{
						content.append(temp);
					}
					reader.close();

					TextView textView = ((TextView) findViewById(R.id.file_manager_tv));
					textView.setText(content.toString());
					textView.setVisibility(View.VISIBLE);
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		}
	}
}
