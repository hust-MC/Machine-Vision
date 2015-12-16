package com.machinevision.option;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.machinevision.terminal.R;
import com.machinevision.sub_option.DialogBuilder;
import com.machinevision.sub_option.FileManager_fileExplorer;

/**
 * 用于封装控制面板功能 。继承此类之后，必须在setContentView方法之后调用setListData()来设置面板左侧的内容
 * 
 * @author MC
 */
abstract class ControlPannelActivity extends Activity
{
	/**
	 * 记录当前显示的Listview内容，用于按键切换
	 * 
	 * @author MC
	 */
	private int position;

	protected ListView listview;

	protected MenuWithSubMenu wholeMenu;

	/**
	 * 如果当前菜单没有子菜单时触发此函数
	 * 
	 * @param position
	 *            当前点击菜单的位置
	 */
	protected void onSpecialItemClicked(int position)
	{
	}
	protected String[] getCurrentValue(int position)
	{
		return null;
	}
	/**
	 * 初始化listview控件
	 * 
	 * @author MC
	 */
	protected void init_widget()
	{
		listview = (ListView) findViewById(R.id.listview);
		listview.setAdapter(new ArrayAdapter<String>(this,
				R.layout.menu_list_item, wholeMenu.menu));

	}
	/**
	 * 设置统一的菜单点击事件
	 */
	protected void setListViewClicked()
	{
		listview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if ((position < wholeMenu.subMenu.length)
						&& !wholeMenu.subMenu[position].equals(""))
				{
					String[] currentValue = getCurrentValue(position);
					DialogBuilder builder = new DialogBuilder(
							ControlPannelActivity.this, position);
					builder.build(wholeMenu.menu[position],
							wholeMenu.subMenu[position],
							wholeMenu.subMenuType[position], currentValue);
				}
				else
				{
					onSpecialItemClicked(position);
				}
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			finishWithAnim();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 方向键的按键触发事件
	 * 
	 * @param resId
	 *            按键ID
	 * @author MC
	 */
	public void onDirectionClicked(int resId)
	{

		switch (resId)
		{
		case R.id.bt_control_pannel_top:
			position = listview.getFirstVisiblePosition() - 2;
			if (position < 0)
			{
				position = 0;
			}
			break;
		case R.id.bt_control_pannel_bottom:
			position = listview.getLastVisiblePosition() + 2;
			if (position >= wholeMenu.menu.length)
			{
				position = wholeMenu.menu.length - 1;
			}
			break;
		default:
			break;
		}
		listview.smoothScrollToPosition(position);

	}

	/**
	 * 确认、取消按键的触发事件
	 * 
	 * @param resId
	 *            按键的ID
	 * @author MC
	 */
	public void onButtonClicked(int resId)
	{
		switch (resId)
		{
		case R.id.bt_control_pannel_confirm:
			setResult(RESULT_OK);
			break;
		case R.id.bt_control_pannel_cancel:
			setResult(RESULT_CANCELED);
		}
		finishWithAnim();
	}

	/**
	 * 完整菜单类。menu表示界面下的所有菜单，subMenu表示相应菜单下的所有子菜单，subMenuType表示对应子菜单的所有类型和内容
	 * 
	 * @author M
	 * 
	 */
	class MenuWithSubMenu
	{
		protected String[] menu;
		protected String[] subMenu;
		protected String[] subMenuType;
		protected String[] subMenuIni;

		/**
		 * 生成整个菜单项
		 * 
		 * @param menuId
		 *            主菜单项的资源ID
		 * @param subMenuId
		 *            子菜单项的资源ID，0则为无子菜单
		 */
		public MenuWithSubMenu(int menuId, int subMenuId)
		{
			menu = menuId != 0 ? getResources().getStringArray(menuId) : null;
			subMenu = subMenuId != 0 ? getResources().getStringArray(subMenuId)
					: null;
			subMenuType = new String[]
			{ "0" };
		}

		/**
		 * 生成整个菜单项
		 * 
		 * @param menuId
		 *            主菜单资源ID
		 * @param subMenuId
		 *            子菜单资源ID，0则为无子菜单
		 * @param typeId
		 *            子菜单的类型。0为输入框，1为选项框
		 * @param subMenuIniId
		 *            预读取的数据资源
		 */
		public MenuWithSubMenu(int menuId, int subMenuId, int typeId,
				int subMenuIniId)
		{
			menu = menuId != 0 ? getResources().getStringArray(menuId) : null;
			subMenu = subMenuId != 0 ? getResources().getStringArray(subMenuId)
					: null;
			subMenuType = typeId != 0 ? getResources().getStringArray(typeId)
					: null;
			subMenuIni = subMenuIniId != 0 ? getResources().getStringArray(
					subMenuIniId) : null;
		}
	}
}
