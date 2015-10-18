package com.machineversion.sub_option;

import com.machineversion.terminal.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentDevice_trigger extends Fragment
{
	Activity activity;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		activity = getActivity();
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{

		return inflater.inflate(R.layout.fragment_device_general, container,
				false);
	}
}
