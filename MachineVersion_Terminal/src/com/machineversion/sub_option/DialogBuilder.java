package com.machineversion.sub_option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.machineversion.option.FastenerSettings;
import com.machineversion.terminal.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.content.DialogInterface.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 建立一个对话框
 * 
 * @author MC
 * 
 */
public class DialogBuilder
{
	private Context context;
	private View[] views;

	public DialogBuilder(Context context)
	{
		this.context = context;
	}

	/**
	 * 用来根据参数生成一个对话框
	 * 
	 * @param title
	 *            对话框的标题
	 * @param menu
	 *            对话框的内容，每一个元素用”，”分离
	 * @param strType
	 *            对话框中每一行的类型，每个元素之间用“，”分离。第一个字符表示类型：0表示EditText，1表示Spiner。接下来是内容
	 *            ，其中Spiner的内容用n隔开。
	 * 
	 * @return 生成成功与否
	 * 
	 * @author MC
	 */
	public boolean build(String title, String menu, String strType,
			String strIni)
	{
		String[] contents = menu.split(",");
		String[] type = strType.split(",");
		String[] ini = strIni.split(",");

		IniReader iniReader = new IniReader();

		views = new View[contents.length];

		ScrollView scrollView = new ScrollView(context);
		scrollView.setPadding(0, 0, 0, 10);

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(10, 20, 40, 20);
		for (int i = 0; i < contents.length; i++)
		{
			LinearLayout subLayout = new LinearLayout(context);
			subLayout.setOrientation(LinearLayout.HORIZONTAL);

			LayoutParams params = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1);

			/*
			 * 创建一个文本框并设置参数
			 */
			TextView tv = new TextView(context);
			tv.setLayoutParams(params);
			tv.setText(contents[i] + "：");
			tv.setTextSize(27F);

			/*
			 * 解析R.array.option_camera_params_sub并创建对应的输入框， 设置参数，用以接收输入的数据
			 */
			params.weight = 1.5F;
			if (type[i].startsWith("0"))
			{
				views[i] = new EditText(context);

				((EditText) views[i]).setTextSize(25F);
				((EditText) views[i])
						.setBackgroundResource(android.R.drawable.edit_text);
				try
				{
					((EditText) views[i]).setText(ini[i]);
				} catch (IndexOutOfBoundsException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context, R.layout.spiner, type[i].substring(1).split(
								"n"));
				views[i] = new Spinner(context);

				adapter.setDropDownViewResource(R.layout.spiner);
				((Spinner) views[i]).setAdapter((adapter));
			}

			// et.setHint("请输入" + contents[i]);
			views[i].setLayoutParams(params);
			views[i].setPadding(8, 2, 5, 2);

			subLayout.addView(tv);
			subLayout.addView(views[i]);

			layout.addView(subLayout);
		}
		scrollView.addView(layout);

		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title)
				.setView(scrollView)
				.setPositiveButton("确定", new ConfirmButton())
				.setNegativeButton("取消", new CancelButton()).create();

		dialog.show();

		((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
				.setTextSize(27F);
		((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE)
				.setTextSize(27F);

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		return true;
	}

	/**
	 * 配置文件解析类
	 * 
	 * @author M
	 * 
	 */
	private class IniReader
	{
		private int pos;
		private String[] keys;
		private JSONObject json;

		public IniReader()
		{
			if (context instanceof FastenerSettings)
			{
				keys = context.getResources().getStringArray(
						R.array.option_fastener_settings_ini);
				File file = new File(FastenerSettings.getFilePath(keys[0]));
				if (file.exists())
				{
					try
					{
						String str = null;
						StringBuffer strBuf = new StringBuffer();
						BufferedReader reader = new BufferedReader(
								new FileReader(file));
						while (TextUtils.isEmpty(str = reader.readLine()))
						{
							strBuf.append(str);
						}
						json = new JSONObject(strBuf.toString());
					} catch (FileNotFoundException e)
					{
						e.printStackTrace();
					} catch (IOException e)
					{
						e.printStackTrace();
					} catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		String nextString()
		{
			try
			{
				return json.getString(keys[pos++]);
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			return null;
		}
	}

	private class ConfirmButton implements OnClickListener
	{
		String[] value = new String[views.length];

		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			for (int i = 0; i < value.length; i++)
			{
				if (views[i] instanceof EditText)
				{
					value[i] = ((EditText) views[i]).getText().toString();
				}
				else if (views[i] instanceof Spinner)
				{
					value[i] = String.valueOf(((Spinner) views[i])
							.getSelectedItemPosition());
				}
			}
			if (context instanceof OnDialogClicked)
			{
				((OnDialogClicked) context).onPositiveButtonClicked(value);
			}
		}
	}

	private class CancelButton implements OnClickListener
	{
		@Override
		public void onClick(DialogInterface dialog, int which)
		{

		}
	}

	public interface OnDialogClicked
	{
		/**
		 * 处理对话框的确定按钮点击事件
		 * 
		 * @param value
		 *            对话框接收的数据
		 * 
		 */
		void onPositiveButtonClicked(String[] value);
	}

}