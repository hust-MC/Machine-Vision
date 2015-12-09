package com.machinevision.option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.machinevision.terminal.R;
import com.machinevision.sub_option.ButtonCfgLlist;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;
import com.machinevision.terminal.FileDirectory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class MachineLearning extends ControlPannelActivity implements
		OnDialogClicked
{

	public static final String FILE_DIR = FileDirectory.getAppDirectory()
			+ "Learning/";
	private final int REQUEST_FILE = 101;

	String saveName;
	String[] value1;;
	public static Bitmap fullImage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		wholeMenu = new MenuWithSubMenu(R.array.option_machine_learning,
				R.array.option_machine_learning_sub,
				R.array.option_machine_learning_type,
				R.array.option_machine_learning_ini);
		init_widget();
		setListViewClicked();
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

	public static String getImgPath(String id)
	{
		return FILE_DIR + id + "/" + id + ".jpg";
	}

	/**
	 * 保存钮扣的配置文件
	 */
	@Override
	public void onPositiveButtonClicked(String[] value)
	{
		value1 = value;
		LayoutInflater inflater = (LayoutInflater) MachineLearning.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.edit_text, null);

		new AlertDialog.Builder(MachineLearning.this).setTitle("配置文件命名")
				.setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						EditText nameEditText = (EditText) view
								.findViewById(R.id.editText1);
						saveName = nameEditText.getText().toString();

						/*
						 * 存入配置文件中
						 */
						String[] strs = getResources().getStringArray(
								R.array.option_machine_learning_ini);
						String[] keys = strs[0].split(",");

						String[] strType = getResources().getStringArray(
								R.array.option_machine_learning_type);
						String[] type = strType[0].split(",");

						JSONObject json = new JSONObject();
						try
						{
							for (int i = 0; i < keys.length; i++)
							{
								json.put(keys[i],
										type[i].substring(1).split("n")[Integer
												.valueOf(value1[i])]);
							}
						} catch (JSONException e)
						{
							e.printStackTrace();
						}
						try
						{
							File file = new File(getFilePath(saveName));
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

						saveMyBitmap(fullImage, saveName);
					}

				}).setNegativeButton("取消", null).show();
	}

	public void saveMyBitmap(Bitmap mBitmap, String bitName)
	{
		File f = new File(getImgPath(bitName));
		FileOutputStream fOut = null;
		try
		{
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try
		{
			fOut.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			fOut.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onSpecialItemClicked(int position)
	{
		Intent intent = new Intent(this, ButtonCfgLlist.class);
		intent.putExtra("firstDir", FILE_DIR);
		System.out.println("------------111");
		startActivityForResult(intent, REQUEST_FILE);
		System.out.println("------------222");
		super.onSpecialItemClicked(position);
	}

}
