package com.machinevision.option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import com.machinevision.terminal.MainActivity;
import com.machinevision.terminal.NetThread;
import com.machinevision.terminal.R;
import com.machinevision.net.CmdHandle;
import com.machinevision.net.NetUtils;
import com.machinevision.sub_option.ButtonCfgLlist;
import com.machinevision.sub_option.DebugMode;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;
import com.machinevision.sub_option.NewButton;
import com.machinevision.terminal.FileDirectory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MachineLearning extends ControlPannelActivity implements
		OnDialogClicked
{
	public static final String FILE_DIR = FileDirectory.getAppDirectory()
			+ "Learning/";
	
	private final int NEW_FILE = 100;
	private final int QUERY_FILE = 101;

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

	@Override
	protected void onSpecialItemClicked(int position)
	{	
		Intent intent;
		switch (position) {
		case 0:
			intent = new Intent(this, NewButton.class);
			startActivityForResult(intent, NEW_FILE);
			super.onSpecialItemClicked(position);
			break;
		case 1:
			intent = new Intent(this, ButtonCfgLlist.class);
			intent.putExtra("firstDir", FILE_DIR);
			startActivityForResult(intent, QUERY_FILE);
			super.onSpecialItemClicked(position);
			break;
		case 2:
			break;
		default:
			break;
		}
	}


	@Override
	public void onPositiveButtonClicked(String[] value, int position) {
		// TODO Auto-generated method stub
		
	}
}
