package com.machinevision.sub_option;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.machinevision.terminal.MainActivity;
import com.machinevision.terminal.R;
import com.machinevision.common_widget.EToast;
import com.machinevision.net.CmdHandle;
import com.machinevision.net.NetUtils;

public class DebugMode extends Activity
{
	private Handler handler = new DebugHandler(this);
	private ImageView iv_fullImage;
	private boolean stopFlag = false;

	private void init_widget()
	{
		iv_fullImage = (ImageView) findViewById(R.id.iv_debug_mode);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug_mode);

		init_widget();

		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay();
		LayoutParams p = getWindow().getAttributes();
		p.height = (int) (d.getHeight() * 0.8);
		p.width = (int) (d.getWidth() * 0.95);
		getWindow().setAttributes(p);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				CmdHandle cmdHandle = CmdHandle.getInstance();
				if (cmdHandle == null)
				{
					Message message = handler.obtainMessage();
					message.what = MainActivity.ERROR_MESSAGE;
					message.sendToTarget();
					finish();
				}
				else
				{
					while (!stopFlag)
					{
						cmdHandle.getVideo(handler);
					}
				}
			}
		}).start();
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		stopFlag = true;
	}

	static class DebugHandler extends Handler
	{
		private WeakReference<Activity> mActivity;

		public DebugHandler(Activity activity)
		{
			mActivity = new WeakReference<Activity>(activity);
		}
		@Override
		public void handleMessage(Message msg)
		{
			if (mActivity.get() == null)
			{
				return;
			}
			if (msg.what == NetUtils.MSG_NET_GET_VIDEO)
			{
				((DebugMode) mActivity.get()).iv_fullImage
						.setImageBitmap((Bitmap) msg.obj);
			}
			else if (msg.what == MainActivity.ERROR_MESSAGE)
			{
				EToast.makeText(mActivity.get(), "网络未连接", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.debug_mode, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
