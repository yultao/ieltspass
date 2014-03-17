package tt.lab.android.ieltspass.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.data.WordsDao;
import tt.lab.android.ieltspass.model.Example;
import tt.lab.android.ieltspass.model.Explanation;
import tt.lab.android.ieltspass.model.ExplanationCategory;
import tt.lab.android.ieltspass.model.Word;
import android.app.ActionBar;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
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

	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	private Button btnPlayStopA;
	private Button btnPlayStopB;
	private boolean playing;
	private AudioPlayer player = new AudioPlayer();
	private WordsDao wordsDao;

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

		wordsDao = new WordsDao(this.getApplicationContext());
		
		initTitle();
		try {
			TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
			TextView tvAPhoetic = (TextView) findViewById(R.id.tvAPhoetic);
			TextView tvBPhoetic = (TextView) findViewById(R.id.tvBPhoetic);
			btnPlayStopA = (Button) findViewById(R.id.btnPlayA);
			btnPlayStopB = (Button) findViewById(R.id.btnPlayB);
						
			textViewTitle.setText(word.getWord_vocabulary());
			tvAPhoetic.setText(word.getAE_phonetic_symbol());
			tvBPhoetic.setText(word.getBE_phonetic_symbol());

			btnPlayStopA.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					play(btnPlayStopA, word.getAE_sound());
				}
			});
			btnPlayStopB.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {					
					play(btnPlayStopB, word.getBE_sound());
				}
			});
			

		} catch (Exception e) {
			Logger.i(TAG, "Exception: " + e.getMessage());
			e.printStackTrace();
		}

		initFragement();
	}

	private void initFragement() {
		SectionBasicFragment fragment = new ChineseExplanationFragment();
		fragment.setWord(word);
		fragment.setWordsDao(wordsDao);
		pagerItemList.add(fragment);

		fragment = new EnglishExplanationFragment();
		fragment.setWord(word);
		fragment.setWordsDao(wordsDao);
		Bundle args = new Bundle();
		args.putInt(SectionBasicFragment.ARG_SECTION_NUMBER, 2);
		fragment.setArguments(args);
		pagerItemList.add(fragment);

		fragment = new ExampleFragment();
		fragment.setWord(word);
		fragment.setWordsDao(wordsDao);
		args = new Bundle();
		args.putInt(SectionBasicFragment.ARG_SECTION_NUMBER, 3);
		fragment.setArguments(args);
		pagerItemList.add(fragment);
	}

	private void initTitle() {
		Button back = (Button) findViewById(R.id.menuButton);
		// back.setBackground(Resources.getSystem().getDrawable(R.drawable.back));
		back.setBackgroundDrawable(getResources().getDrawable(R.drawable.backbutton));
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
						+ " 详见：http://www.ieltspass.com/" + word.getWord_vocabulary());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享"));
			}
		});
		TextView title = (TextView) findViewById(R.id.titleText);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String wordTitle = bundle.getString("title");
		word = wordsDao.getSingleWordInfo(wordTitle);

		title.setText(wordTitle);
	}

	private void navigateUp() {
		NavUtils.navigateUpTo(this, new Intent(this, LauncherActivity.class));
	}

	private void refreshButtonText() {
		if (playing) {
			btnPlayStopA.setText("Stop");
		} else {
			btnPlayStopA.setText("Start");
		}
	}
	
	public static class AudioPlayer {
		private MediaPlayer player;
		
		public AudioPlayer() {
			player = new MediaPlayer();
			
			/* 当MediaPlayer.OnCompletionLister会运行的Listener */
			player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				// @Override
				/* 覆盖文件播出完毕事件 */
				public void onCompletion(MediaPlayer arg0) {
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
					//refreshButtonText();
				}
			});

			/* 当MediaPlayer.OnErrorListener会运行的Listener */
			player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				/* 覆盖错误处理事件 */
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					try {
						/* 发生错误时也解除资源与MediaPlayer的赋值 */
						player.release();
						// tv.setText("播放发生异常!");
					} catch (Exception e) {
						// tv.setText(e.toString());
						e.printStackTrace();
					}
					//refreshButtonText();
					return false;
				}
			});
			
			player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					player.start();
					//playing = true;
				}
			});
		}
		
		public void play(String url) {			
			stop();
			
			//Logger.i(this.getClass().getName(), "Playing " + file.getAbsolutePath());
			try {
				player.reset();
				player.setDataSource(url);
				player.prepareAsync();
				//refreshButtonText();
			} catch (Exception e) {
				Logger.i(this.getClass().getName(), "Exception: " + e.getMessage());
				e.printStackTrace();
			}			
		}
		
		public void stop() {
			try {
				/* 发生错误时也解除资源与MediaPlayer的赋值 */
				if (player != null)
					player.release();
				// tv.setText("播放发生异常!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void play(Button invoker, String url) {
		player.play(url);
		if (invoker.getText().equals("Start")) {
			invoker.setText("Stop");
		} else {
			invoker.setText("Start");
		}
	}
	
	private void stop() {
		btnPlayStopA.setText("Start");
		btnPlayStopB.setText("Start");
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
		stop();
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
	public static abstract class SectionBasicFragment extends Fragment {
		protected WordsDao wordsDao;
		protected Word word;

		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public SectionBasicFragment() {
		}

		public void setWord(Word word) {
			this.word = word;
		}

		public void setWordsDao(WordsDao wordsDao) {
			this.wordsDao = wordsDao;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_vocabulary_section_basic, container, false);
			buildContent(rootView, container, savedInstanceState);
			return rootView;
		}
		
		public abstract void buildContent(View rootView, ViewGroup container, Bundle savedInstanceState);
		
		protected TextView createTextView() {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			TextView textView = new TextView(SectionBasicFragment.this.getActivity());
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setPadding(10, 0, 0, 0);
			textView.setTextSize(15);
			
			return textView;
		}

	}
	
	public static class ChineseExplanationFragment extends SectionBasicFragment {

		@Override
		public void buildContent(View rootView, ViewGroup container, Bundle savedInstanceState) {
			List<Explanation> wordExplains = wordsDao.getExplanationsForSingleWord(word.getWord_vocabulary());
			
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.vacabulary_section_basic);
			for(Explanation explanation : wordExplains) {
				if (explanation.getCategory().equalsIgnoreCase(ExplanationCategory.BASIC.name())) {
					TextView tv = createTextView();
					tv.setText(explanation.getPart_of_speech() + " " + explanation.getContent());
					layout.addView(tv);
				} else if (explanation.getCategory().equalsIgnoreCase(ExplanationCategory.CHINESE.name())) {
					TextView tv = createTextView();
					tv.setText(explanation.getContent());
					layout.addView(tv);
					tv.setPadding(30, 0, 0, 10);
				}
			}
		}
		
	}
	
	public static class EnglishExplanationFragment extends SectionBasicFragment {

		@Override
		public void buildContent(View rootView, ViewGroup container, Bundle savedInstanceState) {
			List<Explanation> wordExplains = wordsDao.getExplanationsForSingleWord(word.getWord_vocabulary());
			
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.vacabulary_section_basic);
			for(Explanation explanation : wordExplains) {
				if (explanation.getCategory().equalsIgnoreCase(ExplanationCategory.ENGLISH.name())) {
					TextView tv = createTextView();
					tv.setText(explanation.getContent());
					layout.addView(tv);
				}
			}			
		}
		
	}
	
	public static class ExampleFragment extends SectionBasicFragment {

		@Override
		public void buildContent(View rootView, ViewGroup container, Bundle savedInstanceState) {
			List<Example> examples = wordsDao.getExamplesForSingleWord(word.getWord_vocabulary());
			
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.vacabulary_section_basic);
			for(Example example : examples) {
				TextView tv = createTextView();
				tv.setText(example.getSentence());
				layout.addView(tv);
				
				tv = createTextView();
				tv.setText(example.getCn_explanation());
				layout.addView(tv);
				tv.setPadding(tv.getPaddingLeft(), 0, 0, 10);
			}				
		}
		
	}

}
