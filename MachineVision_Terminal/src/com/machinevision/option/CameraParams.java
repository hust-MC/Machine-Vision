package com.machinevision.option;

import com.emercy.dropdownlist.DropDownList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.machinevision.terminal.R;
import com.machinevision.common_widget.DialogWindow;
import com.machinevision.common_widget.EToast;
import com.machinevision.common_widget.NumberSettingLayout;
import com.machinevision.common_widget.SeekBarEditLayout;
import com.machinevision.sub_option.DialogBuilder.OnDialogClicked;
import com.machinevision.sub_option.SystemSetting_devicePacket.HECC;
import com.machinevision.sub_option.SystemSetting_devicePacket.Mode;
import com.machinevision.sub_option.SystemSetting_devicePacket.Net;
import com.machinevision.sub_option.SystemSetting_devicePacket.Parameters;
import com.machinevision.sub_option.SystemSetting_devicePacket.Sensor;
import com.machinevision.sub_option.SystemSetting_devicePacket.UART;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

public class CameraParams extends ControlPannelActivity implements
		OnDialogClicked
{
	public static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		wholeMenu = new MenuWithSubMenu(R.array.option_camera_params,
				R.array.option_camera_params_sub,
				R.array.option_camera_params_type, 0);
		activity = this;
		init_widget();
		setListViewClicked();
	}
	/**
	 * 根据选择的菜单项获取当前数据，当前数据是指从相机端获取的数据
	 * 
	 * @param position
	 *            用户点击的菜单项
	 * @return 相机端传来的数据
	 */
	@Override
	protected String[] getCurrentValue(int position)
	{
		String[] currentValue = new String[getResources().getStringArray(
				R.array.option_camera_params_sub).length];
		int count = 0;
		switch (position)
		{

		// NET选项
		case 2:
		{
			Net net = Parameters.getInstance().net;
			currentValue[count++] = net.ip_address[0] + "." + net.ip_address[1]
					+ "." + net.ip_address[2] + "." + net.ip_address[3];
			currentValue[count++] = net.remote_ip[0] + "." + net.remote_ip[1]
					+ "." + net.remote_ip[2] + "." + net.remote_ip[3];
			currentValue[count++] = net.mac_address[0] + ":"
					+ net.mac_address[1] + ":" + net.mac_address[2] + ":"
					+ net.mac_address[3] + ":" + net.mac_address[4] + ":"
					+ net.mac_address[5];
			currentValue[count++] = net.port + "";
			break;
		}
		case 3:
		{
			HECC hecc = Parameters.getInstance().hecc;
			currentValue[count++] = hecc.baudRate + "";
			break;
		}
		case 4:
		{
			UART uart = Parameters.getInstance().uart;
			currentValue[count++] = uart.baudRate + "";
			currentValue[count++] = (uart.work_mode & 0x03) + "";
			break;
		}

		}
		return currentValue;
	}
	@Override
	public void onPositiveButtonClicked(String[] value, int position)
	{
		Gson gson = new Gson();
		JsonObject json = new JsonObject();
		switch (position)
		{
		// 常规设置
		case 2:
		{
			Net net = Parameters.getInstance().net;
			int i = 0;
			for (String str : value[0].split("\\."))
			{
				net.ip_address[i++] = Integer.parseInt(str);
			}
			i = 0;
			for (String str : value[1].split("\\."))
			{
				net.remote_ip[i++] = Integer.parseInt(str);

			}
			i = 0;
			for (String str : value[2].split("\\:"))
			{
				net.mac_address[i++] = Integer.parseInt(str);

			}

			net.port = Integer.parseInt(value[3]);

			json.add("net", gson.toJsonTree(net));

			break;
		}
		// HECC设置
		case 3:
		{
			HECC hecc = Parameters.getInstance().hecc;
			hecc.baudRate = Integer.parseInt(value[0]);
			json.add("hecc", gson.toJsonTree(hecc));
			break;
		}
		// 串口设置
		case 4:
		{
			UART uart = Parameters.getInstance().uart;
			uart.baudRate = Integer.parseInt(value[0]);
			uart.work_mode |= (Integer.parseInt(value[1]) & 0x03);
			json.add("uart", gson.toJsonTree(uart));

			break;
		}
		default:
			break;
		}
		sendJson(json);
	}
	@Override
	protected void onSpecialItemClicked(int position)
	{
		final JsonObject json = new JsonObject();
		final Gson gson = new Gson();
		switch (position)
		{
		case 1:
			final View viewGeneral = getLayoutInflater().inflate(
					R.layout.vpager_device_general, null);
			Sensor sensor = Parameters.getInstance().sensor;
			Mode mode = Parameters.getInstance().mode;

			// 以下两句设置下拉菜单的内容
			((DropDownList) viewGeneral
					.findViewById(R.id.device_setting_input_type))
					.setItem(R.array.device_setting_input_type);
			((DropDownList) viewGeneral
					.findViewById(R.id.device_setting_output_type))
					.setItem(R.array.device_setting_output_type);
			((SeekBarEditLayout) viewGeneral
					.findViewById(R.id.device_setting_exposure)).setMax(65536);
			((NumberSettingLayout) viewGeneral
					.findViewById(R.id.device_setting_start_x))
					.setValue(sensor.startPixel_width);
			((NumberSettingLayout) viewGeneral
					.findViewById(R.id.device_setting_start_y))
					.setValue(sensor.startPixel_height);
			((NumberSettingLayout) viewGeneral
					.findViewById(R.id.device_setting_input_w))
					.setValue(sensor.width_input);
			((NumberSettingLayout) viewGeneral
					.findViewById(R.id.device_setting_input_h))
					.setValue(sensor.height_input);

			((SeekBarEditLayout) viewGeneral
					.findViewById(R.id.device_setting_exposure))
					.setValue(mode.expoTime);

			if (mode.bitType == 8)
				((RadioButton) viewGeneral
						.findViewById(R.id.device_setting_bit_radio0))
						.setSelected(true);
			else
				((RadioButton) viewGeneral
						.findViewById(R.id.device_setting_bit_radio1))
						.setSelected(true);

			if (mode.trigger == 0)
				((CheckBox) viewGeneral
						.findViewById(R.id.device_setting_mode_checkbox0))
						.setChecked(true);
			else if (mode.trigger == 1)
				((CheckBox) viewGeneral
						.findViewById(R.id.device_setting_mode_checkbox1))
						.setChecked(true);
			else
				((CheckBox) viewGeneral
						.findViewById(R.id.device_setting_mode_checkbox2))
						.setChecked(true);
			DialogWindow dialog = new DialogWindow.Builder(this)
					.setTitle(
							getResources().getStringArray(
									R.array.option_camera_params)[position])
					.setView(viewGeneral)
					.setPositiveButton("应用", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Sensor sensor = Parameters.getInstance().sensor;
							Mode mode = Parameters.getInstance().mode;

							sensor.startPixel_width = ((NumberSettingLayout) viewGeneral
									.findViewById(R.id.device_setting_start_x))
									.getValue();
							sensor.startPixel_height = ((NumberSettingLayout) viewGeneral
									.findViewById(R.id.device_setting_start_y))
									.getValue();
							sensor.width_input = ((NumberSettingLayout) viewGeneral
									.findViewById(R.id.device_setting_input_w))
									.getValue();
							sensor.height_input = ((NumberSettingLayout) viewGeneral
									.findViewById(R.id.device_setting_input_h))
									.getValue();

							mode.expoTime = ((SeekBarEditLayout) viewGeneral
									.findViewById(R.id.device_setting_exposure))
									.getValue();
							mode.bitType = ((RadioButton) viewGeneral
									.findViewById(R.id.device_setting_bit_radio0))
									.isChecked() ? 8 : 16;

							if (((CheckBox) viewGeneral
									.findViewById(R.id.device_setting_mode_checkbox0))
									.isChecked())
							{
								mode.trigger = 0;
							}
							else if (((CheckBox) viewGeneral
									.findViewById(R.id.device_setting_mode_checkbox1))
									.isChecked())
							{
								mode.trigger = 1;
							}
							else
							{
								mode.trigger = 2;
							}

							json.add("mode", gson.toJsonTree(mode));
							JsonObject jsonSensor = new JsonObject();
							jsonSensor.add("sensor", gson.toJsonTree(sensor));
							sendJson(json);
							sendJson(jsonSensor);
						}
					}).setNegativeButton("取消", null).create();
			dialog.show();
			dialog.setWindowWidth(800);
			break;
		case 5:
			EToast.makeText(this, "设置文件已保存", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
}
