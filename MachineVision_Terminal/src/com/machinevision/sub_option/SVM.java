package com.machinevision.sub_option;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.machinevision.common_widget.EToast;
import com.machinevision.common_widget.ProgressBox;
import com.machinevision.net.CmdHandle;
import com.machinevision.option.FileManager;
import com.machinevision.svm.LibSVMTest;
import com.machinevision.terminal.MainActivity;
import com.machinevision.terminal.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

public class SVM extends Activity {
	final int REQUEST_FILE = 1;

	private Button bt_get, bt_stop, bt_exit, bt_train;
	private EditText editText;
	ProgressDialog dialog;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			double accuracy = (Double) msg.obj;
			dialog.dismiss();
			editText.setText(("" + accuracy * 100).subSequence(0, 5));
			EToast.makeText(SVM.this, "SVM训练成功",
					Toast.LENGTH_SHORT).show();
		};
	};

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_svm);
		bt_get = (Button) findViewById(R.id.machine_learning_svm_get);
		bt_stop = (Button) findViewById(R.id.machine_learning_svm_stop);
		bt_train = (Button) findViewById(R.id.machine_learning_svm_train);
		bt_exit = (Button) findViewById(R.id.machine_learning_svm_exit);
		editText = (EditText) findViewById(R.id.accuracy);

//		bt_get.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				startActivityForResult(new Intent(SVM.this,
//						FileManager_fileExplorer.class), REQUEST_FILE);
//			}
//		});

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
							Message msg = Message.obtain();
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

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent i) {
//		if (resultCode == RESULT_OK) {
//			if (requestCode == REQUEST_FILE) {
//				try {
//					String temp = null;
//					StringBuffer content = new StringBuffer();
//					BufferedReader reader = new BufferedReader(
//							new FileReader(
//									i.getStringExtra(FileManager_fileExplorer.RESULT_FILE_PATH)));
//
//					while (!TextUtils.isEmpty(temp = reader.readLine())) {
//						content.append(temp);
//					}
//					reader.close();
//					
//					String name= i.getStringExtra(FileManager_fileExplorer.RESULT_FILE_NAME);
//					String path= i.getStringExtra(FileManager_fileExplorer.RESULT_FILE_PATH);
//					int len = content.length();
//					
//					System.out.println("----content->" + content);
//					System.out.println("----name-->" + name);
//					System.out.println("----namelen-->" + name.length());
//					System.out.println("----path-->" + path);
//					System.out.println("----pathlen-->" + path.length());
//					
////					content.insert(0, (int)content.length());
////					System.out.println("----len-->" + content.length());s
//					
//					char[] SeqPath = new char[256];
//					char[] SeqLen  = new char[4];
//							
//					if ((path.length() > 256))
//					{
//						EToast.makeText(this, "文件名过长，超过256个字节", EToast.LENGTH_SHORT)
//						.show();
//						return;
//					}
//															
//					for (int j = 0; j < 256; j++)
//					{
//						if (j < path.length())
//						{
//							SeqPath[j] = path.charAt(j);
//						}
//						else
//						{
//							SeqPath[j] = '0';
//						}
//					}
//					
//					for (int j = 0; j < 4; j++)
//					{
//						SeqLen[j] = (char) (len & 0xff);
//						len = len>>8;
//					}
//																				
//					content.insert(0, SeqLen, 0, SeqLen.length);
//					content.insert(0, SeqPath, 0, SeqPath.length);
//					
//					System.out.println("----content->" + content);
//					System.out.println("----len-->" + content.length());					
//							
//					CmdHandle cmdHandle = MainActivity.getCmdHandle(1);
//					if (cmdHandle == null) {
//						EToast.makeText(this, "网络未连接", EToast.LENGTH_SHORT)
//								.show();
//					} else {
//						String c = content.toString();			
//						cmdHandle.sendBinary(c.getBytes());
//						EToast.makeText(this, "发送成功", EToast.LENGTH_SHORT)
//						.show();
//					}
//					
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//		}
//	}
//
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
