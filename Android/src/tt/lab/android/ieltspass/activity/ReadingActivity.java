package tt.lab.android.ieltspass.activity;

import java.util.ArrayList;
import java.util.Locale;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.R.id;
import tt.lab.android.ieltspass.R.layout;
import tt.lab.android.ieltspass.R.menu;
import tt.lab.android.ieltspass.R.string;
import tt.lab.android.ieltspass.activity.SpeakingActivity.SectionsPagerAdapter;
import tt.lab.android.ieltspass.fragment.ReadingFragmentAnswers;
import tt.lab.android.ieltspass.fragment.ReadingFragmentArticles;
import tt.lab.android.ieltspass.fragment.ReadingFragmentQuestions;
import tt.lab.android.ieltspass.fragment.SpeakingFragmentQuestions;
import tt.lab.android.ieltspass.fragment.SpeakingFragmentRecordings;
import tt.lab.android.ieltspass.fragment.SpeakingFragmentScripts;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ReadingActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
	 * If this becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	private String title;
	private String lyrics;
	private String audio;
	private String questions;
	private String answers;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reading);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		title = bundle.getString("title");
		lyrics = bundle.getString("lyrics");
		audio = bundle.getString("audio");
		questions = bundle.getString("questions");
		answers = bundle.getString("answers");
		initTitle();
		initPager();
		initFragement();
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
				intent.putExtra(Intent.EXTRA_TEXT, "雅思通(IELTS PASS)分享：");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享"));
			}
		});
		TextView titleText = (TextView) findViewById(R.id.titleText);
		titleText.setText(title.toUpperCase());
	}
	private void initPager() {
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}
	private void navigateUp() {
		NavUtils.navigateUpTo(this, new Intent(this, LauncherActivity.class));
	}
	private void initFragement() {
		

		ReadingFragmentArticles fragmentRecordings = new ReadingFragmentArticles();
		pagerItemList.add(fragmentRecordings);
		
		ReadingFragmentQuestions fragmentQuestions = new ReadingFragmentQuestions();
		pagerItemList.add(fragmentQuestions);
		
		ReadingFragmentAnswers fragmentAnswers = new ReadingFragmentAnswers();
		pagerItemList.add(fragmentAnswers);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.reading, menu);
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
			return pagerItemList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "文章".toUpperCase(l);
			case 1:
				return "题目".toUpperCase(l);
			case 2:
				return "答案".toUpperCase(l);
			}
			return null;
		}
	}

}
