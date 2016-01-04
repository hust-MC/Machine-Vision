package com.machinevision.sub_option;

import java.io.IOException;

import com.machinevision.common_widget.EToast;
import com.machinevision.svm.LibSVMTest;
import com.machinevision.terminal.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;

public class SVM extends Activity
{

	private Button bt_getSample, bt_stop, bt_exit, bt_train;

	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_svm);
		bt_getSample = (Button) findViewById(R.id.machine_learning_svm_get);
		bt_stop = (Button) findViewById(R.id.machine_learning_svm_stop);
		bt_train = (Button) findViewById(R.id.machine_learning_svm_train);
		bt_exit = (Button) findViewById(R.id.machine_learning_svm_exit);

		bt_getSample.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				try
				{
					double accuracy = LibSVMTest.run();
					EToast.makeText(
							SVM.this,
							"SVM Classification is done! The accuracy is "
									+ accuracy, Toast.LENGTH_SHORT).show();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		bt_exit.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				EToast.makeText(SVM.this, "退出成功", Toast.LENGTH_SHORT).show();
				finishWithAnim();
			}
		});

	}

	/**
	 * 关闭时触发切换动画
	 * 
	 * @author MC
	 */
	private void finishWithAnim()
	{
		finish();
		overridePendingTransition(0, R.anim.top_out);
	}

}
