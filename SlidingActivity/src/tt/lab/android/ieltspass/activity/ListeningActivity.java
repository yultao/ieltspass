package tt.lab.android.ieltspass.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.data.Constants;
import tt.lab.android.ieltspass.data.Logger;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ListeningActivity extends FragmentActivity {
	private static final String TAG = ListeningActivity.class.getName();
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
	 * If this becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private Button btnPlayStop;
	private boolean playing;
	private MediaPlayer player;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	private Handler handler = new Handler();
	private SeekBar seekBar;
	private TextView textView1, textView3;
	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listening);
		initPager();
		initFragement();

		initTitle();
		initControls();
		initPlayer();
	}

	private void initControls() {
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// fromUser判断是用户改变的滑块的值
				if (fromUser == true) {
					player.seekTo(progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		seekBar.setEnabled(false);

		btnPlayStop = (Button) findViewById(R.id.button1);
		btnPlayStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playing) {
					stopPlaying();
				} else {
					startPlaying();
				}
			}
		});

		textView1 = (TextView) findViewById(R.id.textView1);
		textView3 = (TextView) findViewById(R.id.textView3);
	}

	private void initPager() {
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	private void initTitle() {
		Button back = (Button) findViewById(R.id.menuButton);
		// back.setBackground(Resources.getSystem().getDrawable(R.drawable.back));
		back.setBackground(getResources().getDrawable(R.drawable.back));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				navigateUp();
			}
		});
		Button share = (Button) findViewById(R.id.shareButton);
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/*");// intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "好友推荐");
				intent.putExtra(Intent.EXTRA_TEXT, "雅思通(IELTS PASS)分享：");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享"));
			}
		});
		TextView title = (TextView) findViewById(R.id.titleText);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String string = bundle.getString("title");

		title.setText(string.toUpperCase());
	}

	private void initFragement() {

		DummySectionFragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, 1);
		fragment.setArguments(args);
		pagerItemList.add(fragment);

		fragment = new DummySectionFragment();
		args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, 2);
		fragment.setArguments(args);
		pagerItemList.add(fragment);

		fragment = new DummySectionFragment();
		args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, 3);
		fragment.setArguments(args);
		pagerItemList.add(fragment);

		fragment = new DummySectionFragment();
		args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, 4);
		fragment.setArguments(args);
		pagerItemList.add(fragment);
	}

	private void navigateUp() {
		player.release();
		NavUtils.navigateUpTo(this, new Intent(this, SlidingActivity.class));
	}

	private void refreshButtonText() {
		if (playing) {
			btnPlayStop.setText("Stop");
		} else {
			btnPlayStop.setText("Start");
		}
	}

	private Runnable updateProgressThread = new Runnable() {
		public void run() {
			// 获得歌曲现在播放位置并设置成播放进度条的值
			try {
				int currentPosition = player.getCurrentPosition();
				seekBar.setProgress(currentPosition);
				textView1.setText(formatTime(currentPosition));
				// 每次延迟100毫秒再启动线程
				handler.postDelayed(updateProgressThread, 100);
			} catch (Exception e) {
				Logger.i(TAG, "updateProgressThread: " + e.getMessage());
			}

		}
	};

	private void initPlayer() {
		// AudioManager am = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
		// am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		player = new MediaPlayer();
		player.setOnSeekCompleteListener(new OnSeekCompleteListener() {

			@Override
			public void onSeekComplete(MediaPlayer mp) {
			}
		});
		/* 当MediaPlayer.OnCompletionLister会运行的Listener */
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			// @Override
			/* 覆盖文件播出完毕事件 */
			public void onCompletion(MediaPlayer arg0) {
				stopPlaying();
			}
		});

		/* 当MediaPlayer.OnErrorListener会运行的Listener */
		player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			/* 覆盖错误处理事件 */
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				stopPlaying();
				return false;
			}
		});
		String name = Constants.SD_PATH + "/" + Constants.AUDIO_PATH + "/test.mp3";
		file = new File(name);
		Logger.i(TAG, "Playing " + file.getAbsolutePath());
		if (file.exists()) {
			try {
				seekBar.setEnabled(true);
				player.reset();
				player.setDataSource(file.getAbsolutePath());
				player.prepare();
				textView3.setText(formatTime(player.getDuration()));
			} catch (Exception e) {
				Logger.i(TAG, "startPlaying: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void startPlaying() {
		Logger.i(TAG, "startPlaying I");
		try {
			player.seekTo(seekBar.getProgress());
			player.start();
			playing = true;
			handler.post(updateProgressThread);
			seekBar.setMax(player.getDuration());
			textView1.setText(formatTime(player.getCurrentPosition()));
			refreshButtonText();

		} catch (Exception e) {
			Logger.i(TAG, "startPlaying: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void stopPlaying() {
		Logger.i(TAG, "stopPlaying I");
		try {
			playing = false;
			/* 发生错误时也解除资源与MediaPlayer的赋值 */
			player.pause();
			// tv.setText("播放发生异常!");
			handler.removeCallbacks(updateProgressThread);
			refreshButtonText();
		} catch (Exception e) {
			Logger.i(TAG, "stopPlaying: " + e.getMessage());
			e.printStackTrace();
		}
		Logger.i(TAG, "stopPlaying O");
	}

	private String formatTime(int l) {
		String str = "";
		int hour = 0;
		int minute = 0;
		int second = 0;

		second = l / 1000;

		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		str = (minute > 9 ? "" + minute : "0" + minute) + ":" + (second > 9 ? "" + second : "0" + second);
		if (hour > 0) {
			str = (hour > 9 ? "" + hour : "0" + hour) + ":" + str;
		}
		return str;
	}

	private void back() {
		stopPlaying();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			player.release();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.listening, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = pagerItemList.get(position);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_listening_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_listening_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_listening_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_listening_dummy, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}
