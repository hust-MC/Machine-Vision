package com.machinevision.sub_option;

import java.io.IOException;

import com.machinevision.common_widget.EToast;
import com.machinevision.common_widget.ProgressBox;
import com.machinevision.svm.LibSVMTest;
import com.machinevision.terminal.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;

public class SVM extends Activity {
	private Button bt_getSample, bt_stop, bt_exit, bt_train;	
	private EditText editText;
	ProgressDialog dialog;		

	private Handler handler = new Handler()		
	{
		public void handleMessage(Message msg) {
			double accuracy = (Double) msg.obj;
			dialog.dismiss();							
			editText.setText(("" + accuracy * 100).subSequence(0, 5));
			EToast.makeText(SVM.this, "SVM Classification is done", Toast.LENGTH_SHORT).show();		
		};
	};

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_svm);
		bt_getSample = (Button) findViewById(R.id.machine_learning_svm_get);
		bt_stop = (Button) findViewById(R.id.machine_learning_svm_stop);
		bt_train = (Button) findViewById(R.id.machine_learning_svm_train);
		bt_exit = (Button) findViewById(R.id.machine_learning_svm_exit);
		editText = (EditText) findViewById(R.id.accuracy);

		bt_train.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog = ProgressBox.show(SVM.this, "正在训练，请稍候..."); // 进程弹窗
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							double accuracy = LibSVMTest.run();
							Message msg =  Message.obtain();
							msg.obj = accuracy;
							handler.sendMessage(msg);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}).start();				
			}
		});


		bt_exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
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
	private void finishWithAnim() {
		finish();
		overridePendingTransition(0, R.anim.top_out);
	}

}
