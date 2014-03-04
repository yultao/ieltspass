package tt.lab.android.ieltspass.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.R.id;
import tt.lab.android.ieltspass.R.layout;
import tt.lab.android.ieltspass.R.menu;
import tt.lab.android.ieltspass.R.string;
import tt.lab.android.ieltspass.data.Constants;
import tt.lab.android.ieltspass.data.Database;
import tt.lab.android.ieltspass.data.Logger;
import tt.lab.android.ieltspass.data.Word;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class VocabularyActivity extends FragmentActivity {
	private static final String TAG = VocabularyActivity.class.getName();
	

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
	Word word;

	SectionBasicFragment sectionBasicFragment;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	private Button btnPlayStop;
	private boolean playing;
	private MediaPlayer player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocabulary);
		ActionBar actionBar = getActionBar();
		if (actionBar != null)
			actionBar.setDisplayHomeAsUpEnabled(true);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		initTitle();
		try {
			TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
			TextView textViewPhoetic = (TextView) findViewById(R.id.textViewPhoetic);
			TextView textViewDate = (TextView) findViewById(R.id.textViewDate);
			btnPlayStop = (Button) findViewById(R.id.btnPlay);
			
			
			textViewTitle.setText(word.getWord_vacabulary());
			textViewPhoetic.setText(word.getBE_phonetic_symbol());
			textViewDate.setText(word.getCategory());
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
			

		} catch (Exception e) {
			Logger.i(TAG, "Exception: " + e.getMessage());
			e.printStackTrace();
		}

		initFragement();
	}

	private void initFragement() {
		sectionBasicFragment = new SectionBasicFragment();
		sectionBasicFragment.setWord(word);
		pagerItemList.add(sectionBasicFragment);

		DummySectionFragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, 2);
		fragment.setArguments(args);
		pagerItemList.add(fragment);

		fragment = new DummySectionFragment();
		args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, 3);
		fragment.setArguments(args);
		pagerItemList.add(fragment);
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
				intent.putExtra(Intent.EXTRA_TEXT, "雅思通(IELTS PASS)分享：" + word.toString()
						+ " 详见：http://www.ieltspass.com/" + word.getWord_vacabulary());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享"));
			}
		});
		TextView title = (TextView) findViewById(R.id.titleText);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String string = bundle.getString("title");
		Map<String, Word> wordMap = Database.getWords();
		word = wordMap.get(string);

		title.setText(string.toUpperCase());
	}

	private void navigateUp() {
		NavUtils.navigateUpTo(this, new Intent(this, LaunchActivity.class));
	}

	private void refreshButtonText() {
		if (playing) {
			btnPlayStop.setText("Stop");
		} else {
			btnPlayStop.setText("Start");
		}
	}

	private void startPlaying() {
		String name = Constants.SD_PATH + "/" + Constants.AUDIO_PATH + "/test.mp3";
		// AudioManager am = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
		// am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		player = new MediaPlayer();

		/* 当MediaPlayer.OnCompletionLister会运行的Listener */
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			// @Override
			/* 覆盖文件播出完毕事件 */
			public void onCompletion(MediaPlayer arg0) {
				playing = false;
				try {
					/*
					 * 解除资源与MediaPlayer的赋值关系 让资源可以为其它程序利用
					 */
					player.release();
					/* 改变TextView为播放结束 */
					// tv.setText("音乐播发结束!");

				} catch (Exception e) {
					// tv.setText(e.toString());
					e.printStackTrace();
				}
				refreshButtonText();
			}
		});

		/* 当MediaPlayer.OnErrorListener会运行的Listener */
		player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			/* 覆盖错误处理事件 */
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				playing = false;
				try {
					/* 发生错误时也解除资源与MediaPlayer的赋值 */
					player.release();
					// tv.setText("播放发生异常!");
				} catch (Exception e) {
					// tv.setText(e.toString());
					e.printStackTrace();
				}
				refreshButtonText();
				return false;
			}
		});

		File file = new File(name);
		if (file.exists()) {
			Logger.i(this.getClass().getName(), "Playing " + file.getAbsolutePath());
			try {
				player.reset();
				player.setDataSource(file.getAbsolutePath());
				player.prepare();
				player.start();
				playing = true;
				refreshButtonText();
			} catch (Exception e) {
				Logger.i(this.getClass().getName(), "Exception: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			Logger.i(this.getClass().getName(), file.getAbsolutePath() + " does not exist.");
		}
		Logger.i(this.getClass().getName(), "Release " + file.getAbsolutePath());

	}

	private void stopPlaying() {
		playing = false;
		try {
			/* 发生错误时也解除资源与MediaPlayer的赋值 */
			if (player != null)
				player.release();
			// tv.setText("播放发生异常!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		refreshButtonText();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// NavUtils.navigateUpTo(this, new Intent(this, SlidingActivity.class));
			navigateUp();
			// NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	private void back() {
		stopPlaying();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @author taog
	 * 
	 */
	public static class SectionBasicFragment extends Fragment {
		private Word word;

		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public SectionBasicFragment() {
		}

		public void setWord(Word word) {
			this.word = word;

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_vocabulary_section_basic, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			TextView textView1 = (TextView) rootView.findViewById(R.id.titleText);
			TextView textView2 = (TextView) rootView.findViewById(R.id.textView2);
			TextView textView3 = (TextView) rootView.findViewById(R.id.duration);
			textView1.setText("1");
//			if (word == null) {
//				
//			} else {
//
//				dummyTextView.setText(word.);
//
//				textView1.setText(word.getPhoneticSymbol());
//
//				textView2.setText(word.getDate());
//
//				textView3.setText(word.getExplanation());
//			}
			return rootView;
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
			View rootView = inflater.inflate(R.layout.fragment_vocabulary_dummy, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}
