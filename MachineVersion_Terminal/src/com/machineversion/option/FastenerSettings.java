package com.machineversion.option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.machineversion.net.CmdHandle;
import com.machineversion.sub_option.DialogBuilder.OnDialogClicked;
import com.machineversion.sub_option.FileManager_fileExplorer;
import com.machineversion.terminal.FileDirectory;
import com.machineversion.terminal.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FastenerSettings extends ControlPannelActivity implements
		OnDialogClicked
{
	public static final String FILE_DIR = FileDirectory.getAppDirectory()
			+ "/sample/";

	private final int REQUEST_FILE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		wholeMenu = new MenuWithSubMenu(R.array.option_fastener_settings,
				R.array.option_fastener_settings_sub,
				R.array.option_fastener_settings_type,
				R.array.option_fastener_settings_ini);
		init_widget();
		setListViewClicked();
	}

	@Override
	protected void onSpecialItemClicked(int position)
	{
		Intent intent = new Intent(this, FileManager_fileExplorer.class);
		intent.putExtra("firstDir", FILE_DIR);
		startActivityForResult(intent, REQUEST_FILE);
		super.onSpecialItemClicked(position);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == REQUEST_FILE)
			{
				String filePath = data.getExtras().getString("filePath");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取钮扣的配置文件路径
	 * 
	 * @param id
	 *            对应钮扣的ID
	 * @return 配置文件路径
	 */
	public static String getFilePath(String id)
	{
		return FILE_DIR + id + "/" + id + ".ini";
	}
	@Override
	public void onPositiveButtonClicked(String[] value)
	{
		/*
		 * 存入配置文件中
		 */
		String[] keys = getResources().getStringArray(
				R.array.option_fastener_settings_ini);

		JSONObject json = new JSONObject();
		try
		{
			for (int i = 0; i < keys.length; i++)
			{
				json.put(keys[i], value[i]);
			}

		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		try
		{
			File file = new File(getFilePath(value[0]));
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(json.toString());
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		/*
		 * 发送配置文件到SC280
		 */
		CmdHandle cmdHandle = CmdHandle.getInstance();
		if (cmdHandle == null)
		{
			Toast.makeText(this, "请检查网络连接", Toast.LENGTH_SHORT).show();
		}
		else
		{

			cmdHandle.normal(Integer.parseInt(value[0]));
		}
	}
}
