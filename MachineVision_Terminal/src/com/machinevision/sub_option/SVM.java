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

	private Button button1, button2;

	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_svm);
		button1 = (Button) findViewById(R.id.bt_svm_use);
		button2 = (Button) findViewById(R.id.bt_svm_exit);

		button1.setOnClickListener(new View.OnClickListener()
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

		button2.setOnClickListener(new View.OnClickListener()
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
