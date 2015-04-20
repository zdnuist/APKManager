package com.zdnuist.apkmanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class MyHorizontalScrollView extends HorizontalScrollView{
	
	public MyHorizontalScrollView(Context context){
		this(context,null);
	}
	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private int mScreenWidth;
	private boolean once;
	private ViewGroup mWapper;
	private ViewGroup mMenu;
	private int mMenuWidth;
	
	public MyHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
		
		viewSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		if (!once)
		{
			mWapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) mWapper.getChildAt(0);
			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth;
			once = true;
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	static int currentX;
	int moveX;
	int viewSlop;
	boolean isXMove;
	
	static boolean actionDown;
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		int action = ev.getAction();
		Log.d("touch", "hori view"+action);
		Log.e("zd", "action:"+action);
		switch(action){
		case MotionEvent.ACTION_DOWN:
			currentX = (int) ev.getX();
			actionDown = true;
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		
		return super.onTouchEvent(ev);
	}
	

}
