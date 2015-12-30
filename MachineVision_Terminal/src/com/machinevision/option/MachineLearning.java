package com.machinevision.option;

import com.machinevision.terminal.R;
import com.machinevision.sub_option.ButtonCfgLlist;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;
import com.machinevision.sub_option.NewButton;
import com.machinevision.sub_option.SVM;
import com.machinevision.terminal.FileDirectory;

import android.content.Intent;
import android.os.Bundle;

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
			startActivityForResult(intent, QUERY_FILE);
			super.onSpecialItemClicked(position);
			break;
		case 2:
			intent = new Intent(this, SVM.class);
			startActivityForResult(intent, QUERY_FILE);
			super.onSpecialItemClicked(position);
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
