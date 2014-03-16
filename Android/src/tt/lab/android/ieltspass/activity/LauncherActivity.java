package tt.lab.android.ieltspass.activity;

import tt.lab.android.ieltspass.Constants;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.fragment.CenterFragment;
import tt.lab.android.ieltspass.fragment.LeftFragment;
import tt.lab.android.ieltspass.fragment.RightFragment;
import tt.lab.android.ieltspass.fragment.CenterFragment.PageChangeListener;
import tt.lab.android.ieltspass.view.SlidingMenuRelativeLayout;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;
import tt.lab.android.ieltspass.R;

public class LauncherActivity extends FragmentActivity {
	private SlidingMenuRelativeLayout mSlidingMenuRelativeLayout;
	private LeftFragment leftFragment;
	private RightFragment rightFragment;
	private CenterFragment centerFragment;
	private boolean isExit;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main);
		initApp();
		init();
		initListener();
	}

	private void initApp() {
		Constants.APP_PATH = getFilesDir().getAbsolutePath();
		Constants.assetManager = getAssets();
		Utilities.ensurePath(Constants.VOCABULARY_IMAGE_PATH);
		Utilities.ensurePath(Constants.SPEAKING_AUDIO_PATH);
		Utilities.connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	private void init() {
		mSlidingMenuRelativeLayout = (SlidingMenuRelativeLayout) findViewById(R.id.slidingMenu);
		mSlidingMenuRelativeLayout.setLeftView(getLayoutInflater().inflate(R.layout.left_frame, null));
		mSlidingMenuRelativeLayout.setRightView(getLayoutInflater().inflate(R.layout.right_frame, null));
		mSlidingMenuRelativeLayout.setCenterView(getLayoutInflater().inflate(R.layout.center_frame, null));

		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		leftFragment = new LeftFragment();
		fragmentTransaction.replace(R.id.left_frame, leftFragment);

		rightFragment = new RightFragment();
		fragmentTransaction.replace(R.id.right_frame, rightFragment);

		centerFragment = new CenterFragment();
		fragmentTransaction.replace(R.id.center_frame, centerFragment);

		fragmentTransaction.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initListener() {
		/*
		*/
		centerFragment.setMyPageChangeListener(new PageChangeListener() {

			public void onPageSelected(int position) {
				Log.i("onTouchEvent()", "onPageSelected: " + position);
				if (centerFragment.isFirst()) {
					mSlidingMenuRelativeLayout.setCanSliding(true, false);
				} else if (centerFragment.isEnd()) {
					mSlidingMenuRelativeLayout.setCanSliding(false, true);
				} else {
					mSlidingMenuRelativeLayout.setCanSliding(false, false);
				}
			}
		});
	}

	public void showLeft() {
		mSlidingMenuRelativeLayout.showLeftView();
	}

	public void showRight() {
		mSlidingMenuRelativeLayout.showRightView();
	}

}
