
package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.activity.SlidingActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class CenterFragmentPager extends CenterFragment {

	private Button button1, button2, button3, button4;
	private static final String VOCABULARY = "词汇语法";
	private static final String LSRW = "听说读写";
	private static final String INFO = "考试信息";
	private static final boolean TAB = false;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.center_pager, null);
		// showLeft = (Button) mView.findViewById(R.id.showLeft);
		// showRight = (Button) mView.findViewById(R.id.showRight);

		button1 = (Button) view.findViewById(R.id.button1);
		button2 = (Button) view.findViewById(R.id.button2);
		button3 = (Button) view.findViewById(R.id.button3);

		button4 = (Button) view.findViewById(R.id.button4);
		if (TAB) {
			initTabHost(view);
		} else {
			initPager(view);
		}
		return view;
	}

	private void initPager(View view) {
		mPager = (ViewPager) view.findViewById(R.id.pager);

		PageFragmentVocabulary page1 = new PageFragmentVocabulary();
		PageFragmentOther page2 = new PageFragmentOther();
		PageFragmentLSRW page3 = new PageFragmentLSRW();
		pagerItemList.add(page1);
		pagerItemList.add(page2);
		pagerItemList.add(page3);
		button1.setBackgroundColor(getResources().getColor(R.color.green));
		button2.setBackgroundColor(getResources().getColor(R.color.bg_color));
		button3.setBackgroundColor(getResources().getColor(R.color.bg_color));
		button4.setBackgroundColor(getResources().getColor(R.color.bg_color));
		mPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {

			@Override
			public int getCount() {
				return pagerItemList.size();
			}

			@Override
			public Fragment getItem(int position) {
				Fragment fragment = null;
				if (position < pagerItemList.size())
					fragment = pagerItemList.get(position);
				else
					fragment = pagerItemList.get(0);
				return fragment;

			}
			@Override  
	        public CharSequence getPageTitle(int position) {  
	            
	            return "aaab"+position;  
	        } 
		});

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			public void onPageScrollStateChanged(int arg0) {
				Log.i("onPageScrollStateChanged", arg0+"");
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.i("onPageScrolled", arg0+", "+arg1+", "+arg2);
			}

			public void onPageSelected(int position) {
				if (myPageChangeListener != null)
					myPageChangeListener.onPageSelected(position);
				switch (position) {
				case 0:
					button1.setBackgroundColor(getResources().getColor(R.color.green));
					button2.setBackgroundColor(getResources().getColor(R.color.bg_color));
					button3.setBackgroundColor(getResources().getColor(R.color.bg_color));
					break;
				case 1:
					button1.setBackgroundColor(getResources().getColor(R.color.bg_color));
					button2.setBackgroundColor(getResources().getColor(R.color.blue));
					button3.setBackgroundColor(getResources().getColor(R.color.bg_color));
					break;
				case 2:
					button1.setBackgroundColor(getResources().getColor(R.color.bg_color));
					button2.setBackgroundColor(getResources().getColor(R.color.bg_color));
					button3.setBackgroundColor(getResources().getColor(R.color.red));
					break;
				}
			}

		});
	}

	private void initTabHost(View view) {
		/*
		 * TabHost tabHost = (TabHost) view.findViewById(R.id.tabhost); tabHost.setup();
		 * 
		 * String[] title = new String[] { this.VOCABULARY, this.LSRW, this.INFO }; int[] tabIds = new int[] {
		 * R.id.tab1, R.id.tab2, R.id.tab3 }; for (int i = 0; i < title.length; i++) { Button button = new
		 * Button(this.getActivity()); button.setText(title[i]); //
		 * button.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shade_bg));
		 * tabHost.addTab(tabHost.newTabSpec(title[i]).setIndicator(button).setContent(tabIds[i])); }
		 * 
		 * final Fragment pageFragment1 = new PageFragment1(); final Fragment pageFragment2 = new PageFragment2(); final
		 * Fragment pageFragment3 = new PageFragment3();
		 * 
		 * switchTab(R.id.tab1, pageFragment1, VOCABULARY);// 默认页替换成page1
		 * 
		 * tabHost.setOnTabChangedListener(new OnTabChangeListener() { public void onTabChanged(String tabId) { Fragment
		 * fragment = null; int layoutId = 0; if (tabId.equals(VOCABULARY)) { fragment = pageFragment1; layoutId =
		 * R.id.tab1; } else if (tabId.equals(LSRW)) { fragment = pageFragment2; layoutId = R.id.tab2; } else if
		 * (tabId.equals(INFO)) { fragment = pageFragment3; layoutId = R.id.tab3; } if (fragment != null) {
		 * switchTab(layoutId, fragment, tabId); } }
		 * 
		 * });
		 */
	}

	private void switchTab(int layoutId, Fragment fragment, String destId) {
		final FragmentManager fragmentManager = this.getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.replace(layoutId, fragment, destId);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
	}

	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		button4.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((SlidingActivity) getActivity()).showLeft();
			}
		});

		//
		// showLeft.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// ((SlidingActivity) getActivity()).showLeft();
		// }
		// });
		//
		// showRight.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// ((SlidingActivity) getActivity()).showRight();
		// }
		// });
	}
	public interface PageChangeListener {
		public void onPageSelected(int position);
	}
}
