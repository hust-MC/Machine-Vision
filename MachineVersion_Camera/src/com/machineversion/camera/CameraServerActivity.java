package com.machineversion.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CameraServerActivity extends Activity implements OnClickListener
{

	int mPort = 6666;
	Button mBtn;
	EditText mEdittext;
	boolean mIsStart = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBtn = (Button) findViewById(R.id.startListen);
		mBtn.setOnClickListener(this);

		mEdittext = (EditText) findViewById(R.id.listenPort);
		mEdittext.setText(Integer.toString(mPort));

		setLocalIp();
	}

	public void onDestroy()
	{
		if (mIsStart)
			Stop();
		super.onDestroy();
	}

	private String intToIp(int i)
	{
		return (i & 0xFF) + "." +

		((i >> 8) & 0xFF) + "." +

		((i >> 16) & 0xFF) + "." +

		(i >> 24 & 0xFF);
	}

	public String getLocalIpAddress()
	{
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (!wifiManager.isWifiEnabled())
		{
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		int ipAddress = wifiInfo.getIpAddress();

		return intToIp(ipAddress);
	}

	public void setLocalIp()
	{
		try
		{
			String ip = getLocalIpAddress();
			if (ip == null)
			{
				ip = "0.0.0.0";
			}
			setTitle("IP: " + ip);
		} catch (Exception e)
		{
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 0, 0, "Exit");
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case 0:
			if (mIsStart)
				Stop();
			finish();
			return true;
		default:
			break;
		}
		return false;
	}

	void Start()
	{
		Intent serviceIntent = new Intent(CameraServerActivity.this,
				CameraServerService.class);
		serviceIntent.putExtra("PORT_VALUE", mPort);
		startService(serviceIntent);
	}

	void Stop()
	{
		Intent serviceIntent = new Intent(CameraServerActivity.this,
				CameraServerService.class);
		stopService(serviceIntent);
	}

	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}

	void ClickButton()
	{
		if (mIsStart)
		{
			mBtn.setText(getResources().getString(R.string.listen));
			mEdittext.setEnabled(true);
			Stop();
			mIsStart = false;
		}
		else
		{
			mPort = Integer.parseInt(mEdittext.getText().toString());
			if (0 < mPort && mPort < 65536)
			{
				mBtn.setText(getResources().getString(R.string.stop));
				setLocalIp();
				mEdittext.setEnabled(false);
				Start();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Listen Failed",
						Toast.LENGTH_SHORT).show();
			}
			mIsStart = true;
		}
	}

	@Override
	public void onClick(View arg0)
	{
		switch (arg0.getId())
		{
		case R.id.startListen:
			ClickButton();
			break;
		default:
			break;
		}
	}
}
