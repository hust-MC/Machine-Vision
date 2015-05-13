package com.machineversion.option;

import java.io.File;

import com.machineversion.sub_option.FileManager_fileExplorer;
import com.machineversion.terminal.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FileManager extends ControlPannelActivity
{
	final int REQUEST_FILE = 1;

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

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == REQUEST_FILE)
			{
				Uri uri = intent.getData();
				File file = new File(uri);
			}
		}
	}
}
