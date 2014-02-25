
package tt.lab.android.ieltspass.fragment;

import java.util.ArrayList;
import java.util.List;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.activity.SlidingActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

public class CenterFragmentIndicator extends CenterFragment {
	private List<String> titles = new ArrayList<String>(); // æ ‡é¢˜
	private Button button2, button3;
	private static final String VOCABULARY = "词汇语法";
	private static final String LSRW = "听说读写";
	private static final String DAILY = "每日一练";
	private TabPageIndicator tabbPageIndicator; //
	private UnderlinePageIndicator underlinePageIndicator;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.center_indicator, null);
		initTitle(view);
		
		initPager(view);
		return view;
	}

	private void initTitle(View view) {
		button2 = (Button) view.findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((SlidingActivity) getActivity()).showLeft();
			}
		});
		button3 = (Button) view.findViewById(R.id.button3);
		button3.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent=new Intent(Intent.ACTION_SEND);  
				intent.setType("image/*");//intent.setType("text/plain");  
				intent.putExtra(Intent.EXTRA_SUBJECT, "好友推荐");  
				intent.putExtra(Intent.EXTRA_TEXT, "感觉雅思通（IELTS PASS)还是不错的，用过吧？");  
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				startActivity(Intent.createChooser(intent, "分享"));  
			}
		});
	}

	private void initPager(View view) {
		mPager = (ViewPager) view.findViewById(R.id.pager);

		PageFragmentVocabulary pageVocabulary = new PageFragmentVocabulary();
		PageFragmentLSRW pageLSRW = new PageFragmentLSRW();

		PageFragmentOther pageOther = new PageFragmentOther();
		pagerItemList.add(pageOther);
		pagerItemList.add(pageVocabulary);
		pagerItemList.add(pageLSRW);
		
		titles.add(DAILY);
		titles.add(VOCABULARY);
		titles.add(LSRW);
		
		

		ViewPagerFrameAdapter adapter = new ViewPagerFrameAdapter(getFragmentManager(), titles);
		mPager.setAdapter(adapter);
		
		
		// å¾—åˆ°æŒ‡ç¤ºï¿½?		
		tabbPageIndicator = (TabPageIndicator) view.findViewById(R.id.tab_indicator);
		tabbPageIndicator.setViewPager(mPager);
		// ä¸‹æ ‡
		underlinePageIndicator = (UnderlinePageIndicator) view.findViewById(R.id.underline_indicator);
		underlinePageIndicator.setViewPager(mPager);
		underlinePageIndicator.setFades(false);
		// è®¾ç½®æŒ‡ç¤ºï¿½?		
		tabbPageIndicator.setOnPageChangeListener(underlinePageIndicator);

		underlinePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
				Log.i("onPageScrollStateChanged", arg0+"");
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.i("onPageScrolled", arg0+", "+arg1+", "+arg2);
			}

			public void onPageSelected(int position) {
				
				if (myPageChangeListener != null)
					myPageChangeListener.onPageSelected(position);
			}

		});

	}

	private class ViewPagerFrameAdapter extends FragmentStatePagerAdapter {

		private List<String> title; // é¡µé�¢æ ‡é¢˜å¤´éƒ¨æ•°æ�®

		public ViewPagerFrameAdapter(FragmentManager fm, List<String> title) {
			super(fm);
			this.title = title;
		}

		// æ ¹æ�®position è¿”å›ž fragment
		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			if (position < pagerItemList.size())
				fragment = pagerItemList.get(position);
			else
				fragment = pagerItemList.get(0);
			return fragment;
		}

		// size
		@Override
		public int getCount() {
			return pagerItemList.size();
		}

		// é¡¶éƒ¨çš„title
		@Override
		public CharSequence getPageTitle(int position) {
			if (titles != null) {
				return titles.get(position).toString();
			}
			return super.getPageTitle(position);
		}
	}

	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

	}


}
