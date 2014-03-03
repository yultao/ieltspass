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
import tt.lab.android.ieltspass.data.Utilities;
import tt.lab.android.ieltspass.fragment.ListeningFragmentAnswers;
import tt.lab.android.ieltspass.fragment.ListeningFragmentLyrics;
import tt.lab.android.ieltspass.fragment.ListeningFragmentQuestions;
import android.content.Intent;
import android.media.MediaPlayer;
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
	private String title;
	private String lyrics;
	private String audio;
	private String questions;
	private String answers;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listening);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		title = bundle.getString("title");
		lyrics = bundle.getString("lyrics");
		audio = bundle.getString("audio");
		questions = bundle.getString("questions");
		answers = bundle.getString("answers");
		
		initPager();
		initTitle();
		initControls();
		initPlayer();
		initFragement();
		start();
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
		TextView titleText = (TextView) findViewById(R.id.titleText);
		titleText.setText(title.toUpperCase());
	}

	private void initFragement() {

		ListeningFragmentQuestions fragmentQuestions = new ListeningFragmentQuestions();
		fragmentQuestions.setQuestions(questions);
		pagerItemList.add(fragmentQuestions);
		
		ListeningFragmentLyrics fragmentLyrics = new ListeningFragmentLyrics();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, 1);
		fragmentLyrics.setArguments(args);
		fragmentLyrics.setPlayer(player,seekBar, lyrics);
		pagerItemList.add(fragmentLyrics);

		ListeningFragmentAnswers fragmentAnswers = new ListeningFragmentAnswers();
		fragmentAnswers.setAnswers(answers);
		pagerItemList.add(fragmentAnswers);

	}

	private void navigateUp() {
		release();
		NavUtils.navigateUpTo(this, new Intent(this, SlidingActivity.class));
	}

	private void initPlayer() {
		// AudioManager am = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
		// am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		player = new MediaPlayer();
		
		/* 当MediaPlayer.OnCompletionLister会运行的Listener */
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			// @Override
			/* 覆盖文件播出完毕事件 */
			public void onCompletion(MediaPlayer arg0) {
				seekBar.setProgress(0);
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
		String name = Constants.SD_PATH + "/" + Constants.AUDIO_PATH + "/"+audio;
		file = new File(name);
		Logger.i(TAG, "initPlayer " + file.getAbsolutePath());
		if (file.exists()) {
			try {
				player.reset();
				player.setDataSource(file.getAbsolutePath());
				player.prepare();
				duration.setText(Utilities.formatTime(player.getDuration()));
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
					//Logger.i(TAG, "onProgressChanged: fromUser: " +  progress +", "+fromUser);
					if (fromUser == true) {
						player.seekTo(progress);
					}
					currentPosition.setText(Utilities.formatTime(progress));
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
				handler.postDelayed(updateProgressThread, 500);//No need to update UI so frequently, 500 is enough.
			} catch (Exception e) {
				Logger.i(TAG, "updateProgressThread: " + e.getMessage());
			}

		}
	};

	private void start() {
		//Logger.i(TAG, "start I");
		try {
			// player.seekTo(seekBar.getProgress());
			player.start();
			handler.post(updateProgressThread);

			currentPosition.setText(Utilities.formatTime(player.getCurrentPosition()));
			refreshButtonText();

		} catch (Exception e) {
			Logger.i(TAG, "start: " + e.getMessage());
			e.printStackTrace();
		}
		//Logger.i(TAG, "start O");
	}

	private void pause() {
		//Logger.i(TAG, "pause I ");
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
		//Logger.i(TAG, "pause O");
	}

	private void release() {
		//Logger.i(TAG, "release I");
		try {

			//Logger.i(TAG, "release: 0: ");
			refreshButtonText();
			//Logger.i(TAG, "release: 1: ");
			player.release();
			//Logger.i(TAG, "release: 2: "+updateProgressThread);
			

			// tv.setText("播放发生异常!");
			handler.removeCallbacks(updateProgressThread);
			//Logger.i(TAG, "release: 3: ");
			
		} catch (Exception e) {
			Logger.i(TAG, "release: E: " + e.getMessage());
			e.printStackTrace();
		}
		//Logger.i(TAG, "release O");
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
			return pagerItemList.size();
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
		Map<String, Map<String, Object>> lyricsTextViewMap = new HashMap<String, Map<String, Object>>();
		List<Map<String, Object>> lyricsTextViewList = new ArrayList<Map<String, Object>>();
		Handler handler = new Handler();
		TextView lastTextView = null;
		MediaPlayer player;
		SeekBar seekBar;
		private String lyrics;
		public DummySectionFragment() {
		}

		public void setPlayer(MediaPlayer player, SeekBar seekBar, String lyrics) {
			this.player = player;
			this.seekBar = seekBar;
			this.lyrics = lyrics;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_listening_lyrics, container, false);

			scrollView1 = (ScrollView) rootView.findViewById(R.id.scrollView1);
			LinearLayout linearLayout1 = (LinearLayout) rootView.findViewById(R.id.linearLayout);
			try {
				List<Map<String, String>> lyricsList = Database.parseLyrics(lyrics);
				for (int i = 0; i < lyricsList.size(); i++) {
					Map<String, String> sentence = lyricsList.get(i);
					String time = sentence.get("time");
					String word = sentence.get("word");
					
					TextView textView = createTextView(time+": "+ word);
					linearLayout1.addView(textView);

					Map<String, Object> m = new HashMap<String, Object>();
					m.put("time", time);
					m.put("text", textView);
					lyricsTextViewList.add(m);

					Map<String, Object> mm = new HashMap<String, Object>();
					mm.put("index", i);
					mm.put("text", textView);
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

					// Algorithm 1
					int cp = player.getCurrentPosition();
					cp = seekBar.getProgress();
					String formatedTime = Utilities.formatTime(cp);

					Map<String, Object> map = lyricsTextViewMap.get(formatedTime);

					if (map != null) {
						
						TextView textView = (TextView) map.get("text");
						//Logger.i(TAG, "equals: " + (textView.equals(lastTextView)));
						if (lastTextView == null || !lastTextView.equals(textView)) {
							Logger.i(TAG, "in: ");
							if (lastTextView != null) {

								lastTextView.setTextColor(getResources().getColor(R.color.sub_text_color));
							}
							textView.setTextColor(getResources().getColor(R.color.red));
							lastTextView = textView;

							int index = (Integer) map.get("index");
							Logger.i(TAG, "index: " + index);

							int real = index - 5;
							if (real <=0) {
								real = 0;
							}
							TextView toScroll = (TextView) lyricsTextViewList.get(real).get("text");
							
							scrollView1.requestChildFocus(toScroll, toScroll);
							
							real = index + 5;
							if (real >= lyricsTextViewList.size()) {
								real = lyricsTextViewList.size() - 1;
							}
							toScroll = (TextView) lyricsTextViewList.get(real).get("text");

							scrollView1.requestChildFocus(toScroll, toScroll);
						}
					}

					// Algorithm 2
					/*
					 * if (currentIndex < lyricsTextViewList.size()) { Map<String, Object> map =
					 * lyricsTextViewList.get(currentIndex); String time = (String)map.get("time");
					 * 
					 * int timeInt = formatTimeInt(time); int cp = player.getCurrentPosition(); if(cp>=timeInt){
					 * 
					 * TextView textView = (TextView)map.get("text"); if (lastTextView != null) {
					 * lastTextView.setTextColor(getResources().getColor(R.color.sub_text_color)); } lastTextView =
					 * textView;
					 * 
					 * int real = currentIndex + 5; if (real >= lyricsTextViewList.size()) { real =
					 * lyricsTextViewList.size() - 1; } TextView toScroll =
					 * (TextView)lyricsTextViewList.get(real).get("text");
					 * textView.setTextColor(getResources().getColor(R.color.red));
					 * 
					 * 
					 * scrollView1.requestChildFocus(textView,textView); currentIndex++; } }
					 */
					handler.postDelayed(updateLyricsThread, 10);
				} catch (Exception e) {
					Logger.i(TAG, "updateLyricsThread e: " + e.getMessage());
				}

			}

		};

		private TextView createTextView(String text) {
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			
			TextView textView = new TextView(this.getActivity());
			textView.setLayoutParams(lp);
			textView.setTextSize(18);
			textView.setText(text);
			
			return textView;
		}
	}

}
