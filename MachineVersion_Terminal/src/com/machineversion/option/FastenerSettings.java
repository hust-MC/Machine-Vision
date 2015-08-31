package com.machineversion.option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.machineversion.net.CmdHandle;
import com.machineversion.sub_option.DialogBuilder.OnDialogClicked;
import com.machineversion.terminal.R;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

public class FastenerSettings extends ControlPannelActivity implements
		OnDialogClicked
{
	private final String FILE_DIR = Environment.getExternalStorageDirectory()
			+ "/sample/";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		wholeMenu = new MenuWithSubMenu(R.array.option_fastener_settings,
				R.array.option_fastener_settings_sub,
				R.array.option_fastener_settings_type);
		init_widget();
		setListViewClicked();
	}

	@Override
	public void onPositiveButtonClicked(String[] value)
	{
		/*
		 * 存入配置文件中
		 */
		String id = value[0];
		String defactType = value[1];
		String threshold = value[2];

		JSONObject json = new JSONObject();
		try
		{
			json.put("id", id);
			json.put("defactType", defactType);
			json.put("threshold", threshold);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		try
		{
			File fileDir = new File(FILE_DIR + id);
			if (!fileDir.exists())
			{
				fileDir.mkdirs();
			}
			FileWriter writer = new FileWriter(new File(fileDir, id + ".ini"));
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
