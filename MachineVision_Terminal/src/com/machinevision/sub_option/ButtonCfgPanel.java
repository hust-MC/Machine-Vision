package com.machinevision.sub_option;

import java.util.ArrayList;
import java.util.List;

import com.machinevision.terminal.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ButtonCfgPanel extends LinearLayout{	
	
	private String[] title;
	private String[] keys;
	private String[] vals;
	
	private Context mContext;
	private View view;
	private List<TextView> textViews = new ArrayList<TextView>();
	private ViewPager vPager;		
	private List<View> lists = new ArrayList<View>();
	private ViewPagerAdapter adapter;

	public ButtonCfgPanel(Context context, String[] _title, String[] _keys, String[] _vals, View addtionView) 
	{
		super(context);
		mContext = context;
		title = _title;		
		keys = _keys;
		vals = _vals;
		view = addtionView;
	} 
	
	private LinearLayout getTopView()
	{		
		LinearLayout layoutTop = new LinearLayout(mContext);
		layoutTop.setOrientation(LinearLayout.HORIZONTAL);
		
		for (int i = 0; i < title.length; i++)	
		{
			TextView textView = new TextView(mContext);
			textView.setText(title[i]);
			textView.setTextSize(27F);
			textView.setGravity(Gravity.CENTER);
			LayoutParams params = new LayoutParams(0, 100, 1);
			textView.setLayoutParams(params);
			layoutTop.addView(textView);
			textViews.add(textView);		
		}
		return layoutTop;
	}
	
	private View getMidView()
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.viewpager, null);
		vPager = (ViewPager) view.findViewById(R.id.viewPagerInstance);
		getContentViews();
		return view;
	}
	
	void setStyle(Button button, CharSequence text, LayoutParams params) {
		button.setWidth(150);
		button.setHeight(100);
		button.setText(text);
		button.setTextSize(30);
		button.setLayoutParams(params);
		button.setBackgroundResource(R.drawable.bg_control_bt);
	}
		
	private LinearLayout getSingleView(String[] key, String[] val)
	{
		LinearLayout layout = new LinearLayout(mContext);
		LinearLayout layoutTop = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layoutTop.setOrientation(LinearLayout.VERTICAL);
		
		View[] views = new View[key.length];
		layoutTop.setGravity(Gravity.CENTER);
		for (int i = 0; i < key.length; i++) 
		{
			LinearLayout subLayout = new LinearLayout(mContext);
			subLayout.setOrientation(LinearLayout.HORIZONTAL);
			subLayout.setGravity(Gravity.CENTER_HORIZONTAL);

			LayoutParams params1 = new LayoutParams(250,
					LayoutParams.WRAP_CONTENT, 0);
			/*
			 * 创建一个文本框并设置参数
			 */
			TextView tv = new TextView(mContext);
			tv.setLayoutParams(params1);
			tv.setText(key[i] + "：");
			tv.setTextSize(27F);

			LayoutParams params2 = new LayoutParams(150, 50, 0);
			if (val[i].startsWith("0")) 
			{
				views[i] = new EditText(mContext);
				((EditText) views[i]).setTextSize(27F);
				((EditText) views[i]).setBackgroundResource(android.R.drawable.edit_text);
			} 
			else 
			{
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						mContext, R.layout.spiner, val[i].substring(1).split("n"));
				views[i] = new Spinner(mContext);
				((Spinner) views[i]).setAdapter(adapter);
			}

			views[i].setLayoutParams(params2);
			views[i].setPadding(8, 2, 5, 2);
			subLayout.addView(tv);
			subLayout.addView(views[i]);
			layoutTop.addView(subLayout);			
		}
		
		// 底部按键布局
		LinearLayout layoutBottom = new LinearLayout(mContext);
		layoutBottom.setOrientation(LinearLayout.HORIZONTAL);
		View[] viewsBottom = new View[2];
		LayoutParams paramsBt0 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		paramsBt0.setMargins(200, 0, 0, 0);
		viewsBottom[0] = new Button(mContext);
		setStyle(((Button) viewsBottom[0]), "保存", paramsBt0);

		LayoutParams paramsBt1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
		LayoutParams.WRAP_CONTENT);
		viewsBottom[1] = new Button(mContext);
		paramsBt1.setMargins(200, 0, 0, 0);
		setStyle(((Button) viewsBottom[1]), "退出", paramsBt1);

		layoutBottom.addView(viewsBottom[0]);
		layoutBottom.addView(viewsBottom[1]);
		
		((Button) viewsBottom[0]).setOnClickListener(new ConfirmButton(views, key, val));
		((Button) viewsBottom[1]).setOnClickListener(new CannelButton());	
		layoutBottom.setGravity(Gravity.CENTER_HORIZONTAL);

		LayoutParams paramTop = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 4);
		LayoutParams paramBottom = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		layout.addView(layoutTop, paramTop);
		layout.addView(layoutBottom, paramBottom);
		return layout;
	}

	private class ConfirmButton implements View.OnClickListener
	{
		private View[] views; 
		private String[] keys;
		private String[] options;
		private String[] values;

		public ConfirmButton(View[] _views, String[] _keys, String[] _options)
		{
			options = _options;
			views = _views;
			keys = _keys;
			values = new String[views.length];
		}

		@Override
		public void onClick(View v) 
		{
			for (int i = 0; i < values.length; i++)
			{
				if (views[i] instanceof EditText)
				{
					values[i] = ((EditText) views[i]).getText().toString();
				}
				else if (views[i] instanceof Spinner)
				{
					values[i] = options[i].substring(1).split("n")[((Spinner) views[i]).getSelectedItemPosition()];
				}
			}
			
			// activity实现了OnDialogClicked接口
			if (mContext instanceof OnButtonClick)
			{
				((OnButtonClick) mContext).onPositiveButtonClicked(keys, values);
			}			
		}
	}
	
	private class CannelButton implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// activity实现了OnDialogClicked接口
			if (mContext instanceof OnButtonClick)
			{
				((OnButtonClick) mContext).onCannelButtonClicked();
			}						
		}	
	}
	
	public interface OnButtonClick
	{
		/**
		 * 处理对话框的确定按钮点击事件
		 * @param value
		 *            对话框接收的数据
		 */
		void onPositiveButtonClicked(String[] key, String[] value);
		void onCannelButtonClicked();
	}

	private void getContentViews() 
	{
		for (int i = 0; i < keys.length; i++)
		{
			if (TextUtils.isEmpty(keys[i]))
			{
				lists.add(view);
			}
			else
			{				
				String[] key = keys[i].split(",");
				String[] val = vals[i].split(",");
				lists.add(getSingleView(key, val));
			}
		}
	}
	
	public View getView()
	{
		LinearLayout layoutOuter = new LinearLayout(mContext);
		layoutOuter.setOrientation(LinearLayout.VERTICAL);
		
		LayoutParams paramTop = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		LayoutParams paramBottom = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 4);

		layoutOuter.addView(getTopView(), paramTop);
		layoutOuter.addView(getMidView(), paramBottom);
		
		adapter = new ViewPagerAdapter(lists);
		vPager.setAdapter(adapter);
		
		bind();
		
		return layoutOuter;
	}
	
	
	/**
	 * 绑定响应关系
	 */
	void bind()
	{
		for(int i = 0; i < title.length; i++)
		{
			if (i == 0)
				textViews.get(0).setBackgroundResource(R.drawable.bg_top_bt);
			else 
				textViews.get(i).setBackgroundResource(R.drawable.bg_title);				
			textViews.get(i).setOnClickListener(new TextViewListener(i));
		}
		
		vPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < title.length; i++)
				{
					if (i == arg0)
						textViews.get(i).setBackgroundResource(R.drawable.bg_top_bt);
					else
						textViews.get(i).setBackgroundResource(R.drawable.bg_title);						
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	class TextViewListener implements View.OnClickListener 
	{
		private int CurrentNum;
		public TextViewListener(int Num)
		{
			CurrentNum = Num;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			vPager.setCurrentItem(CurrentNum);
			for(int j = 0; j < title.length; j++)
			{
				if (j == CurrentNum)
					textViews.get(j).setBackgroundResource(R.drawable.bg_top_bt);
				else 
					textViews.get(j).setBackgroundResource(R.drawable.bg_title);
			}
		}
	}
	
	class ViewPagerAdapter extends PagerAdapter {
		List<View> viewLists;

		public ViewPagerAdapter(List<View> lists) {
			viewLists = lists;
		}

		// 获得size
		@Override
		public int getCount() {
			return viewLists.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		// 销毁Item
		@Override
		public void destroyItem(View view, int position, Object object) {
			((ViewPager) view).removeView(viewLists.get(position));
		}

		// 实例化Item
		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(viewLists.get(position), 0);
			return viewLists.get(position);
		}
	};
}
