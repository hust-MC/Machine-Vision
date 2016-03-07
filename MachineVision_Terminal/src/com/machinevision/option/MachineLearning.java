package com.machinevision.option;

import com.machinevision.terminal.R;
import com.machinevision.sub_option.ButtonCfgLlist;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;
import com.machinevision.sub_option.NewButton;
import com.machinevision.sub_option.SVM;
import com.machinevision.terminal.FileDirectory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MachineLearning extends ControlPannelActivity implements
		OnDialogClicked
{
	public static final String FILE_DIR = FileDirectory.getLeaningDirectory();	
	private final int NEW_FILE = 1;
	private final int QUERY_FILE = 2;
	private final int SVM_USE = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		wholeMenu = new MenuWithSubMenu(R.array.option_machine_learning, 0);				
		init_widget();
	}

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
				Intent intent;
				switch (arg2) {	
				case 0:
					intent = new Intent(MachineLearning.this, NewButton.class);
					startActivityForResult(intent, NEW_FILE);
					break;
				case 1:
					intent = new Intent(MachineLearning.this, ButtonCfgLlist.class);
					startActivityForResult(intent, QUERY_FILE);
					break;
				case 2:
					intent = new Intent(MachineLearning.this, SVM.class);
					startActivityForResult(intent, SVM_USE);
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public void onPositiveButtonClicked(String[] value, int position) {
		// TODO Auto-generated method stub
		
	}
}
