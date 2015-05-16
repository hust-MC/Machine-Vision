package com.machineversion.option;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.machineversion.net.CmdHandle;
import com.machineversion.sub_option.FileManager_fileExplorer;
import com.machineversion.terminal.R;

import android.content.Intent;
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
				Bundle bundle = intent.getExtras();
				File picFile = new File(bundle.getString("file path"));
				BufferedImage bi = null;
				try
				{
					bi = ImageIO.read(picFile);
				} catch (IOException e)
				{
					e.printStackTrace();
				}

				FileInputStream in = null;
				ByteArrayOutputStream out = null;
				try
				{
					int n;
					in = new FileInputStream(picFile);
					out = new ByteArrayOutputStream(4096);
					byte[] b = new byte[4096];

					while ((n = in.read(b)) != -1)
					{
						out.write(b, 0, n);
					}
					in.close();
					out.close();
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}

				CmdHandle cmd = CmdHandle.getInstance();
				cmd.sendImage(null, bi.getWidth(), bi.getHeight(),
						(int) picFile.length(), out.toByteArray());
			}
		}
	}
}
