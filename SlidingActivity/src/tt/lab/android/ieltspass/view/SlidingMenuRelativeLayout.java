
package tt.lab.android.ieltspass.view;

import tt.lab.android.ieltspass.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Scroller;


public class SlidingMenuRelativeLayout extends RelativeLayout {

	private View mCenterView;
	private View mLeftView;
	private View mRightView;
	private RelativeLayout bgShade;
	private int screenWidth;
	private int screenHeight;
	private Context mContext;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	private static final int VELOCITY = 50;
	private boolean mIsBeingDragged = true;
	private boolean tCanSlideLeft = true;
	private boolean tCanSlideRight = false;
	private boolean hasClickLeft = false;
	private boolean hasClickRight = false;

	public SlidingMenuRelativeLayout(Context context) {
		super(context);
		init(context);
	}


	public SlidingMenuRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlidingMenuRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {

		mContext = context;
		bgShade = new RelativeLayout(context);
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		WindowManager windowManager = ((Activity) context).getWindow().getWindowManager();

		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		LayoutParams bgParams = new LayoutParams(screenWidth, screenHeight);
		bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		bgShade.setLayoutParams(bgParams);

	}
	public void addViews(View left, View center, View right) {
		setLeftView(left);
		setRightView(right);
		setCenterView(center);
	}

	public void setLeftView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		addView(view, behindParams);
		mLeftView = view;
	}

	public void setRightView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		behindParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(view, behindParams);
		mRightView = view;
	}

	public void setCenterView(View view) {
		LayoutParams aboveParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		LayoutParams bgParams = new LayoutParams(screenWidth, screenHeight);
		bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		View bgShadeContent = new View(mContext);
		bgShadeContent.setBackgroundDrawable(getResources().getDrawable(R.drawable.shade_bg));
		bgShade.addView(bgShadeContent, bgParams);

		addView(bgShade, bgParams);

		addView(view, aboveParams);
		mCenterView = view;
		mCenterView.bringToFront();
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		
		//System.out.println("computeScroll: "+mCenterView.getScrollX()+", "+mCenterView.getScrollY()+" vs. "+mScroller.getCurrX()+", "+mScroller.getCurrY());
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = mCenterView.getScrollX();
				int oldY = mCenterView.getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					if (mCenterView != null) {
						mCenterView.scrollTo(x, y);
						if (x < 0)
							bgShade.scrollTo(x + 20, y);
						else
							bgShade.scrollTo(x - 20, y);
					}
				}
				invalidate();
			}
		}
	}

	private boolean canSlideLeft = true;
	private boolean canSlideRight = false;

	public void setCanSliding(boolean left, boolean right) {
		canSlideLeft = left;
		canSlideRight = right;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		final int action = ev.getAction();
		Log.i("onTouchEvent()", "onInterceptTouchEvent action: "+action);
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN://为什么有的只能触发down，触发不了move
			mLastMotionX = x;
			mLastMotionY = y;
			mIsBeingDragged = false;
			/*
			if (canSlideLeft) {
				mLeftView.setVisibility(View.VISIBLE);
				mRightView.setVisibility(View.INVISIBLE);
			}
			if (canSlideRight) {
				mLeftView.setVisibility(View.INVISIBLE);
				mRightView.setVisibility(View.VISIBLE);
			}*/
			break;

		case MotionEvent.ACTION_MOVE:
			final float dx = x - mLastMotionX;
			final float xDiff = Math.abs(dx);
			final float yDiff = Math.abs(y - mLastMotionY);
			boolean b = xDiff > mTouchSlop && xDiff > yDiff;
			System.out.println("ACTION_MOVE canSlideLeft: "+canSlideLeft+", canSlideRight: "+canSlideRight+", b: "+b);
			if (b) {
				if (canSlideLeft) {
					float oldScrollX = mCenterView.getScrollX();
					System.out.println("ACTION_MOVE oldScrollX "+oldScrollX);
					if (oldScrollX < 0) {//左边已经显示 _[]
						mIsBeingDragged = true;
						mLastMotionX = x;
					} else {
						if (dx > 0) {
							mIsBeingDragged = true;
							mLastMotionX = x;
						}
					}

				} 

				if (canSlideRight) {
					float oldScrollX = mCenterView.getScrollX();
					if (oldScrollX > 0) {
						mIsBeingDragged = true;
						mLastMotionX = x;
					} else {
						if (dx < 0) {
							mIsBeingDragged = true;
							mLastMotionX = x;
						}
					}
				}

			}
			break;

		}
		
		Log.i("onTouchEvent()", "onInterceptTouchEvent mIsBeingDragged: "+mIsBeingDragged);
		return mIsBeingDragged;
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.i("onTouchEvent()", "canSlideLeft:"+canSlideLeft+", canSlideRight: "+canSlideRight+", mIsBeingDragged: "+mIsBeingDragged+", action: "+ev.getAction());
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mLastMotionY = y;
				
			if (mCenterView.getScrollX() == -getMenuViewWidth() && mLastMotionX < getMenuViewWidth()) {
				return false;
			}

			if (mCenterView.getScrollX() == getDetailViewWidth() && mLastMotionX > getMenuViewWidth()) {
				return false;
			}

			break;
		case MotionEvent.ACTION_MOVE:
			Log.i("onTouchEvent()", "mIsBeingDragged: "+mIsBeingDragged);
			mIsBeingDragged = true;
			if (mIsBeingDragged) {//It's a bug!
				final float deltaX = mLastMotionX - x;//deltax <0: 向右滑动
				Log.i("onTouchEvent()", "deltaX: "+deltaX);
				mLastMotionX = x;
				float oldScrollX = mCenterView.getScrollX();//oldScrollX < 0: 滚动条左移动�?图在�?				System.out.println("moving oldScrollX "+oldScrollX);
				float scrollX = oldScrollX + deltaX;
				
				/**/
				if (canSlideLeft) {//
					if (scrollX > 0)
						scrollX = 0;
				}
				if (canSlideRight) {
					if (scrollX < 0)
						scrollX = 0;
				}
				
				//当前在原位：
				//
				//
				if (deltaX < 0 && oldScrollX<=0) { // left view，当前中间视图在原位或偏�?向右滑动=要显示左�?					
					final float leftBound = 0;
					final float rightBound = -getMenuViewWidth();
					if (scrollX > leftBound) {
						scrollX = leftBound;
					} else if (scrollX < rightBound) {
						scrollX = rightBound;
					}
				} else if (deltaX > 0&& oldScrollX>=0) { // right view 当前中间视图在原位或偏左+向左滑动=要显示右�?					
					final float rightBound = getDetailViewWidth();
					final float leftBound = 0;
					if (scrollX < leftBound) {
						scrollX = leftBound;
					} else if (scrollX > rightBound) {
						scrollX = rightBound;
					}
				}
				//delta>0 + oldScrollX<0 
				
				if (mCenterView != null) {
					mCenterView.scrollTo((int) scrollX, mCenterView.getScrollY());
					if (scrollX < 0)
						bgShade.scrollTo((int) scrollX + 20, mCenterView.getScrollY());
					else
						bgShade.scrollTo((int) scrollX - 20, mCenterView.getScrollY());
				}

			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(100);
				float xVelocity = velocityTracker.getXVelocity();				
				int oldScrollX = mCenterView.getScrollX();
				int dx = 0;
				if (oldScrollX <= 0 && canSlideLeft) {// left view
					if (xVelocity > VELOCITY) {
						dx = -getMenuViewWidth() - oldScrollX;
					} else if (xVelocity < -VELOCITY) {
						dx = -oldScrollX;
						if (hasClickLeft) {
							hasClickLeft = false;
							setCanSliding(tCanSlideLeft, tCanSlideRight);
						}
					} else if (oldScrollX < -getMenuViewWidth() / 2) {
						dx = -getMenuViewWidth() - oldScrollX;
					} else if (oldScrollX >= -getMenuViewWidth() / 2) {
						dx = -oldScrollX;
						if (hasClickLeft) {
							hasClickLeft = false;
							setCanSliding(tCanSlideLeft, tCanSlideRight);
						}
					}

				}
				
				if (oldScrollX >= 0 && canSlideRight) {
					if (xVelocity < -VELOCITY) {
						dx = getDetailViewWidth() - oldScrollX;
					} else if (xVelocity > VELOCITY) {
						dx = -oldScrollX;
						if (hasClickRight) {
							hasClickRight = false;
							setCanSliding(tCanSlideLeft, tCanSlideRight);
						}
					} else if (oldScrollX > getDetailViewWidth() / 2) {
						dx = getDetailViewWidth() - oldScrollX;
					} else if (oldScrollX <= getDetailViewWidth() / 2) {
						dx = -oldScrollX;
						if (hasClickRight) {
							hasClickRight = false;
							setCanSliding(tCanSlideLeft, tCanSlideRight);
						}
					}
				}

				smoothScrollTo(dx);

			}

			break;
		}

		return true;
	}

	private int getMenuViewWidth() {
		if (mLeftView == null) {
			return 0;
		}
		return mLeftView.getWidth();
	}

	private int getDetailViewWidth() {
		if (mRightView == null) {
			return 0;
		}
		return mRightView.getWidth();
	}

	void smoothScrollTo(int dx) {
		int duration = 200;
		int oldScrollX = mCenterView.getScrollX();
		mScroller.startScroll(oldScrollX, mCenterView.getScrollY(), dx, mCenterView.getScrollY(), duration);
		invalidate();
	}

	
	public void showLeftView() {
		
		int menuWidth = mLeftView.getWidth();
		System.out.println("SlidingMenu.showLeftView(): "+menuWidth);
		int oldScrollX = mCenterView.getScrollX();
		if (oldScrollX == 0) {
			mLeftView.setVisibility(View.VISIBLE);
			mRightView.setVisibility(View.INVISIBLE);
			smoothScrollTo(-menuWidth);
			tCanSlideLeft = canSlideLeft;
			tCanSlideRight = canSlideRight;
			hasClickLeft = true;
			setCanSliding(true, false);
		} else if (oldScrollX == -menuWidth) {
			smoothScrollTo(menuWidth);
			if (hasClickLeft) {
				hasClickLeft = false;
				setCanSliding(tCanSlideLeft, tCanSlideRight);
			}
		}
	}

	
	public void showRightView() {
		
		int menuWidth = mRightView.getWidth();
		System.out.println("SlidingMenu.showRightView(): "+menuWidth);
		int oldScrollX = mCenterView.getScrollX();
		if (oldScrollX == 0) {
			mLeftView.setVisibility(View.INVISIBLE);
			mRightView.setVisibility(View.VISIBLE);
			smoothScrollTo(menuWidth);
			tCanSlideLeft = canSlideLeft;
			tCanSlideRight = canSlideRight;
			hasClickRight = true;
			setCanSliding(false, true);
		} else if (oldScrollX == menuWidth) {
			smoothScrollTo(-menuWidth);
			if (hasClickRight) {
				hasClickRight = false;
				setCanSliding(tCanSlideLeft, tCanSlideRight);
			}
		}
	}

}
