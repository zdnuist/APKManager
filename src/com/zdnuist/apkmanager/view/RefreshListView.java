package com.zdnuist.apkmanager.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zdnuist.apkmanager.R;

public class RefreshListView extends ListView implements OnScrollListener {

	public static final String TAG = "RefreshListView";

	private View topView; // 顶部layout

	private int topHeight; // 顶部layout的高度

	private int state; // 定义下拉状态

	public static final int NONE = 0; // 正常状态
	public static final int PULL = 1; // 下拉状态
	public static final int RELEASE = 2; // 释放状态
	public static final int REFRESH = 3; // 刷新状态

	public RefreshListView(Context context) {
		super(context);
		initView(context);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private TextView tip; // 提示
	private TextView updateTime;
	private ImageView arrow;
	private ProgressBar progress;

	private void initView(Context context) {

		LayoutInflater layoutInflater = LayoutInflater.from(context);
		topView = layoutInflater.inflate(R.layout.top_view, null);
		// 通过此方法来获取当前高度
		topView.measure(0, 0);
		topHeight = topView.getMeasuredHeight();
		Log.d(TAG, "topHeight:" + topHeight);
		setTopPadding(-topHeight);
		this.addHeaderView(topView);
		this.setOnScrollListener(this);

		/**
		 * 初始化控件
		 */
		tip = (TextView) topView.findViewById(R.id.topview_tip);
		updateTime = (TextView) topView.findViewById(R.id.topview_updatetime);
		arrow = (ImageView) topView.findViewById(R.id.topview_arrow);
		progress = (ProgressBar) topView.findViewById(R.id.topview_progressbar);
	}

	/**
	 * 设置top padding
	 * 
	 * @param topPadding
	 */
	private void setTopPadding(int topPadding) {
		topView.setPadding(topView.getPaddingLeft(), topPadding,
				topView.getPaddingRight(), topView.getPaddingBottom());
		topView.invalidate();
	}

	private int downY; // 按下屏幕的y坐标
	private int moveY;// 手指滑动屏幕的y坐标
	private int disY; // 滑动的距离
	private boolean isTop; // 当前位于listView的最顶端
	private int paddingSpace; // 下拉距离

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();

		Log.d("touch", "fefresh" + action);
		if(MyHorizontalScrollView.actionDown){
			
			if (firstVisibleItem == 0) {
				isTop = true;
				downY = MyHorizontalScrollView.currentX;
			}
			
			MyHorizontalScrollView.actionDown = false;
		}
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (firstVisibleItem == 0) {
				isTop = true;
				downY = (int) ev.getY();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			onMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			if (state == RELEASE) {
				state = REFRESH;
				refreshView();
				iRefreshListener.onRefresh();
			} else if (state == PULL) {
				state = NONE;
				isTop = false;
				refreshView();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 判断是否是移动状态中
	 * 
	 * @param ev
	 */
	private void onMove(MotionEvent ev) {
		if (!isTop) {
			return;
		}

		 moveY = (int) ev.getY();
		 disY = moveY - downY;
		 paddingSpace = -topHeight + disY;
//		Log.e(TAG, "disY:"+disY);
//		Log.e(TAG, "toHeight:"+topHeight);
		Log.e(TAG, "scrollState:"+scrollState);
		switch (state) {
		case NONE:
			if (disY > 0) {
				state = PULL;
				refreshView();
			}

			break;
		case PULL:
			setTopPadding(paddingSpace);
			if (disY > topHeight + 30
					&& scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				state = RELEASE;
				refreshView();
			}
		case RELEASE:
			setTopPadding(paddingSpace);
			if (disY < topHeight + 30) {
				state = PULL;
				refreshView();
			} else if (disY <= 0) {
				state = NONE;
				isTop = false;
				refreshView();
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
	}

	private int firstVisibleItem; // 当前界面最顶端的view位置
	private int scrollState; // 滚动状态

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}


	private void refreshView() {
		RotateAnimation anim = new RotateAnimation(0, 180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(500);
		anim.setFillAfter(true);
		RotateAnimation anim1 = new RotateAnimation(180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim1.setDuration(500);
		anim1.setFillAfter(true);
		Log.w(TAG, "state:"+state);
		switch (state) {
		case NONE:
			arrow.clearAnimation();
			setTopPadding(-topHeight);
			break;
		case PULL:
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			tip.setText("下拉可以刷新！");
			arrow.clearAnimation();
			arrow.setAnimation(anim1);
			break;
		case RELEASE:
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			tip.setText("松开可以刷新！");
			arrow.clearAnimation();
			arrow.setAnimation(anim);
			break;
		case REFRESH:
			setTopPadding(topHeight);
			arrow.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
			tip.setText("正在刷新...");
			arrow.clearAnimation();
			break;
		}
	}
	
	/**
	 * 获取完数据；
	 */
	public void refreshComplete() {
		state = NONE;
		isTop = false;
		refreshView();
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String time = format.format(date);
		updateTime.setText(time);
	}
	
	
	private IRefreshListener iRefreshListener;
	
	public void setListener(IRefreshListener iRefreshListener){
		this.iRefreshListener = iRefreshListener;
	}
	
	
	public interface IRefreshListener{
		public void onRefresh();
	}
	
	

}
