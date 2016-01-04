package com.machinevision.sub_option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.machinevision.terminal.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
	private int position;
	private Context context;

	private int height;
	private int width;

	/**
	 * 对话框中的输入控件
	 */
	private View[] views;

	public DialogBuilder(Context context, int position)
	{
		this.context = context;
		this.position = position;
	}

	public void build(String title, String menu, String strType)
	{
		build(title, menu, strType, null);
	}
	public void build(String title, String menu, String strType, String[] values)
	{
		build(title, menu, strType, null, null, values);
	}
	public void build(String title, String menu, String strType, String strIni,
			String fileDir)
	{
		build(title, menu, strType, strIni, fileDir, null);
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
			String strIni, String fileDir, String[] values)
	{
		String[] contents = menu.split(",");
		String[] type = strType.split(",");
		IniReader iniReader = null;
		if (strIni != null)
		{
			iniReader = new IniReader(strIni, fileDir);
		}
		views = new View[contents.length];					// 输入框

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
				try
				{
					((EditText) views[i]).setText(values[i]);
				} catch (Exception e)
				{
					Log.e("error", "dialogBuilder: " + e.getMessage());
				}
				((EditText) views[i])
						.setBackgroundResource(android.R.drawable.edit_text);
				// 输入框有效性检查
				if (type[i].length() > 1 && type[i].charAt(1) == '0')
				{
				}
				if (strIni != null)
				{
					((EditText) views[i]).setText(iniReader.next());
				}
				try
				{
					// ((EditText) views[i]).setText(ini[i]);
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

				((Spinner) views[i]).setAdapter(adapter);

				try
				{
					((Spinner) views[i]).setSelection(Integer
							.parseInt(values[i]));
				} catch (Exception e)
				{
					Log.e("error", "dialogBuilder: " + e.getMessage());
				}
				if (strIni != null)
				{
					((Spinner) views[i]).setSelection(Integer
							.parseInt(iniReader.next()));
				}
			}
			// et.setHint("请输入" + contents[i]);
			views[i].setLayoutParams(params);
			views[i].setPadding(8, 2, 5, 2);

			subLayout.addView(tv);
			subLayout.addView(views[i]);

			layout.addView(subLayout);
		}
		scrollView.addView(layout);
		View titleLayout = LayoutInflater.from(context).inflate(
				R.layout.alert_dialog_tv, null);
		((TextView) titleLayout.findViewById(R.id.alert_dialog_tv))
				.setText(title);
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setCustomTitle(titleLayout).setView(scrollView)
				.setPositiveButton("确定", new ConfirmButton(position))
				.setNegativeButton("取消", new CancelButton()).create();
		dialog.show();

		((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
				.setTextSize(27F);
		((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE)
				.setTextSize(27F);

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width == 0 ? LayoutParams.WRAP_CONTENT : width;
		params.height = height == 0 ? LayoutParams.WRAP_CONTENT : height;
		dialog.getWindow().setAttributes(params);
		return true;
	}

	/**
	 * 设置弹窗大小
	 * 
	 * @param width
	 *            弹窗的宽度
	 * @param height
	 *            弹窗的高度
	 */
	public void setSize(int width, int height)
	{
		this.height = height;
		this.width = width;
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

		public IniReader(String configStr, String fileDir)
		{
			keys = configStr.split(",");
			File file = new File(fileDir);
			if (file.exists())
			{
				try
				{
					String str = null;
					StringBuffer strBuf = new StringBuffer();
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					while (!TextUtils.isEmpty(str = reader.readLine()))
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

		String next()
		{
			try
			{
				if (pos < keys.length)
				{
					return json.getString(keys[pos++]);
				}
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
		int position;

		public ConfirmButton(int position)
		{
			this.position = position;
		}

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
				((OnDialogClicked) context).onPositiveButtonClicked(value,
						position);
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
		void onPositiveButtonClicked(String[] value, int position);
	}

}