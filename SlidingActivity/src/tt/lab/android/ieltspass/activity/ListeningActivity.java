package tt.lab.android.ieltspass.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.data.Constants;
import tt.lab.android.ieltspass.data.Database;
import tt.lab.android.ieltspass.data.Logger;
import tt.lab.android.ieltspass.fragment.PageFragmentLSRW;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
	private MediaPlayer player;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	private Handler handler = new Handler();
	private SeekBar seekBar;
	private TextView currentPosition, duration;
	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listening);
		initPager();

		initTitle();
		initControls();
		initPlayer();
		initFragement();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

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
		fragment.setPlayer(player);
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
		release();
		NavUtils.navigateUpTo(this, new Intent(this, SlidingActivity.class));
	}

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
				pause();
			}
		});

		/* 当MediaPlayer.OnErrorListener会运行的Listener */
		player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			/* 覆盖错误处理事件 */
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				pause();
				return false;
			}
		});
		String name = Constants.SD_PATH + "/" + Constants.AUDIO_PATH + "/test.mp3";
		file = new File(name);
		Logger.i(TAG, "initPlayer " + file.getAbsolutePath());
		if (file.exists()) {
			try {
				player.reset();
				player.setDataSource(file.getAbsolutePath());
				player.prepare();
				duration.setText(formatTimeSecond(player.getDuration()));
				seekBar.setMax(player.getDuration());
			} catch (Exception e) {
				Logger.i(TAG, "initPlayer: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void initControls() {
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				// fromUser判断是用户改变的滑块的值
				try {

					if (fromUser == true) {
						Logger.i(TAG, "onProgressChanged: fromUser: " + progress);
						player.seekTo(progress);
						currentPosition.setText(formatTimeSecond(progress));
					}
				} catch (Exception e) {
					Logger.i(TAG, "onProgressChanged: E: " + e.getMessage());
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		btnPlayStop = (Button) findViewById(R.id.button1);
		btnPlayStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (player.isPlaying()) {
					pause();
				} else {
					start();
				}
			}
		});

		currentPosition = (TextView) findViewById(R.id.currentPosition);
		duration = (TextView) findViewById(R.id.duration);
	}

	private void refreshButtonText() {
		btnPlayStop.setBackground(getResources().getDrawable(player.isPlaying() ? R.drawable.pause : R.drawable.play));
	}

	private Runnable updateProgressThread = new Runnable() {
		public void run() {
			// 获得歌曲现在播放位置并设置成播放进度条的值
			try {
				int cp = player.getCurrentPosition();
				seekBar.setProgress(cp);
				currentPosition.setText(formatTimeSecond(cp));
				// 每次延迟100毫秒再启动线程
				handler.postDelayed(updateProgressThread, 100);
			} catch (Exception e) {
				Logger.i(TAG, "updateProgressThread: " + e.getMessage());
			}

		}
	};

	private void start() {
		Logger.i(TAG, "start I");
		try {
			// player.seekTo(seekBar.getProgress());
			player.start();
			handler.post(updateProgressThread);

			currentPosition.setText(formatTimeSecond(player.getCurrentPosition()));
			refreshButtonText();

		} catch (Exception e) {
			Logger.i(TAG, "start: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void pause() {
		Logger.i(TAG, "pause I");
		try {
			/* 发生错误时也解除资源与MediaPlayer的赋值 */
			player.pause();
			// tv.setText("播放发生异常!");
			handler.removeCallbacks(updateProgressThread);
			refreshButtonText();
		} catch (Exception e) {
			Logger.i(TAG, "pause: E: " + e.getMessage());
			e.printStackTrace();
		}
		Logger.i(TAG, "pause O");
	}

	private void release() {
		Logger.i(TAG, "release I");
		try {
			/* 发生错误时也解除资源与MediaPlayer的赋值 */
			player.release();
			// tv.setText("播放发生异常!");
			handler.removeCallbacks(updateProgressThread);
			refreshButtonText();
		} catch (Exception e) {
			Logger.i(TAG, "release: E: " + e.getMessage());
			e.printStackTrace();
		}
		Logger.i(TAG, "release O");
	}
	private static int formatTimeInt(String time) {
		//03:02:01.83
		String[] split1 = time.split("[.]");
		String hour = "0";
		String minute = "0";
		String second ="0";
		String millis = "0";
		if(split1.length==2){//02:03.83
			millis = split1[1];
			String[] split2 = split1[0].split(":");
			if(split2.length==2){//02:01
				minute=split2[0];
				second = split2[1];
			} else if (split2.length==3){//01:02:03
				hour = split2[0];
				minute=split2[1];
				second = split2[2];	
			}
		}
		
		int milliseonds = Integer.parseInt(hour)*60*60*1000 + 
				Integer.parseInt(minute)*60*1000+
				Integer.parseInt(second)*1000+
				Integer.parseInt(millis)*10;
		
		return milliseonds;
	}
	private static String formatTimeSecond(int timeInt) {
		String str = "";
		int hour = 0;
		int minute = 0;
		int second = 0;

		second = timeInt / 1000;

		if (second >= 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute >= 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		str = (minute > 9 ? "" + minute : "0" + minute) + ":" + (second > 9 ? "" + second : "0" + second);
		if (hour > 0) {
			str = (hour > 9 ? "" + hour : "0" + hour) + ":" + str;
		}
		return str;
	}

	private static String formatTimeMillis(int timeInt) {
		String str = "";
		int hour = 0;
		int minute = 0;
		int second = 0;
		int millis = 0;
		// 11123
		millis = timeInt % 1000;
		millis /= 10;
		second = timeInt / 1000;

		if (second >= 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute >= 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		str = (minute > 9 ? "" + minute : "0" + minute) + ":" + (second > 9 ? "" + second : "0" + second) + "."
				+ (millis > 9 ? "" + millis : "0" + millis);
		if (hour > 0) {
			str = (hour > 9 ? "" + hour : "0" + hour) + ":" + str;
		}
		return str;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			release();
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
		ScrollView scrollView1;
		Map<String, Map<String,Object>> lyricsTextViewMap = new HashMap<String, Map<String,Object>>();
		List<Map<String, Object>> lyricsTextViewList = new ArrayList<Map<String, Object>>();
		Handler handler = new Handler();
		TextView lastTextView = null;
		MediaPlayer player;
		int currentIndex = 0;
		public DummySectionFragment() {
		}

		public void setPlayer(MediaPlayer player) {
			this.player = player;
			this.player.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					currentIndex = 0;
				}
			});
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_listening_dummy, container, false);

			scrollView1 = (ScrollView) rootView.findViewById(R.id.scrollView1);
			LinearLayout linearLayout1 = (LinearLayout) rootView.findViewById(R.id.linearLayout1);
			try {
				List<Map<String, String>> lyricsList = Database.getLyrics();
				for (int i=0;i<lyricsList.size();i++) {
					Map<String,String> sentence = lyricsList.get(i);
					String time = sentence.get("time");
					String word = sentence.get("word");
					TextView textView = createTextView(time+": "+word);
					linearLayout1.addView(textView);
					
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("time", time);
					m.put("text", textView);
					lyricsTextViewList.add(m);
					
					Map<String, Object> mm = new HashMap<String, Object>();
					mm.put("index", i);
					mm.put("text", textView);
					lyricsTextViewList.add(mm);
					lyricsTextViewMap.put(time, mm);
				}
				handler.post(updateLyricsThread);
			} catch (Exception e) {
				Logger.i(TAG, "addView e: " + e.getMessage());
			}
			return rootView;
		}

		
		private Runnable updateLyricsThread = new Runnable() {
			public void run() {
				try {
					
					//Algorithm 1
					/*
					int cp = player.getCurrentPosition();
					String formatTimeSecond = formatTimeSecond(cp);
					
					Map<String, Object> map = lyricsTextViewMap.get(formatTimeSecond);
					
					if (map != null) {
						if (lastTextView != null) {
							lastTextView.setTextColor(getResources().getColor(R.color.sub_text_color));
						}
						TextView textView = (TextView)map.get("text");
						textView.setTextColor(getResources().getColor(R.color.red));
						lastTextView = textView; 
						
						
						int index = Integer.parseInt((String)map.get("index"));
						int real = index + 5;
						if (real >= lyricsTextViewList.size()) { 
							real = lyricsTextViewList.size() - 1; 
						}
						TextView toScroll = (TextView)lyricsTextViewList.get(real).get("text");
						scrollView1.requestChildFocus(toScroll,toScroll);
					}
					*/
					
					//Algorithm 2
					
					if (currentIndex < lyricsTextViewList.size()) {
						Map<String, Object> map = lyricsTextViewList.get(currentIndex);
						String time = (String)map.get("time");
						
						int timeInt = formatTimeInt(time);
						int cp = player.getCurrentPosition();
						if(cp>=timeInt){
							
							TextView textView = (TextView)map.get("text");
							if (lastTextView != null) {
								lastTextView.setTextColor(getResources().getColor(R.color.sub_text_color));
							}
							lastTextView = textView; 
							
							int real = currentIndex + 5;
							if (real >= lyricsTextViewList.size()) { 
								real = lyricsTextViewList.size() - 1; 
							}
							TextView toScroll = (TextView)lyricsTextViewList.get(real).get("text");
							textView.setTextColor(getResources().getColor(R.color.red));
							
							scrollView1.requestChildFocus(toScroll,toScroll);
							currentIndex++;
						}
					}
					/**/
					handler.postDelayed(updateLyricsThread, 100);
				} catch (Exception e) {
					Logger.i(TAG, "updateLyricsThread e: " + e.getMessage());
				}

			}

			
		};

		private TextView createTextView(String text) {
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
			TextView textView = new TextView(this.getActivity());
			textView.setLayoutParams(lp);
			textView.setTextSize(18);
			textView.setText(text);
			return textView;
		}
	}

}
