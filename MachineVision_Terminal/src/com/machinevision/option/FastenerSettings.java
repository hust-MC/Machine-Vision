package com.machinevision.option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.machinevision.terminal.EToast;
import com.machinevision.terminal.R;
import com.machinevision.net.CmdHandle;
import com.machinevision.sub_option.DialogBuilder;
import com.machinevision.sub_option.FileManager_fileExplorer;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;
import com.machinevision.terminal.FileDirectory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FastenerSettings extends ControlPannelActivity implements
		OnDialogClicked
{
	public static final String FILE_DIR = FileDirectory.getAppDirectory()
			+ "sample/";

	private final int REQUEST_FILE = 100;

	int settings = R.array.option_fastener_settings;
	int settings_sub = R.array.option_fastener_settings_sub;
	int settings_type = R.array.option_fastener_settings_type;
	int settings_ini = R.array.option_fastener_settings_ini;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		wholeMenu = new MenuWithSubMenu(settings, settings_sub, settings_type,
				settings_ini);
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
				String fileDir = data.getExtras().getString(
						FileManager_fileExplorer.RESULT_FILE_PATH);
				if (fileDir.startsWith(FILE_DIR))
				{
					DialogBuilder dialogBuilder = new DialogBuilder(this);
					dialogBuilder.build(
							(getResources().getStringArray(settings))[1],
							(getResources().getStringArray(settings_sub))[1],
							(getResources().getStringArray(settings_type))[1],
							(getResources().getStringArray(settings_ini))[1],
							fileDir);
				}

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
		String[] strs = getResources().getStringArray(
				R.array.option_fastener_settings_ini);
		String[] keys = strs[1].split(",");

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
			EToast.makeText(this, "请检查网络连接", Toast.LENGTH_SHORT).show();
		}
		else
		{

			cmdHandle.normal(Integer.parseInt(value[0]));
		}
	}
}
