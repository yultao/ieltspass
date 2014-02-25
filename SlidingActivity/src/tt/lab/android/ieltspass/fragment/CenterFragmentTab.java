
package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
public class CenterFragmentTab extends CenterFragment {

	
	private static final String VOCABULARY = "词汇语法";
	private static final String LSRW = "听说读写";
	private static final String INFO = "考试信息";

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.center_tab, null);

		initTabHost(view);
		return view;
	}

	private void initTabHost(View view) {
		
		TabHost tabHost = (TabHost) view.findViewById(R.id.tabhost);
		tabHost.setup();

		String[] title = new String[] { this.VOCABULARY, this.LSRW, this.INFO };
		int[] tabIds = new int[] { R.id.tab1, R.id.tab2, R.id.tab3 };
		for (int i = 0; i < title.length; i++) {
			Button button = new Button(this.getActivity());
			button.setText(title[i]);
			// button.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shade_bg));
			tabHost.addTab(tabHost.newTabSpec(title[i]).setIndicator(button).setContent(tabIds[i]));
		}

		final Fragment pageFragment1 = new PageFragmentVocabulary();
		final Fragment pageFragment2 = new PageFragmentOther();
		final Fragment pageFragment3 = new PageFragmentLSRW();

		switchTab(R.id.tab1, pageFragment1, VOCABULARY);// 默认页替换成page1

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				Fragment fragment = null;
				int layoutId = 0;
				if (tabId.equals(VOCABULARY)) {
					fragment = pageFragment1;
					layoutId = R.id.tab1;
				} else if (tabId.equals(LSRW)) {
					fragment = pageFragment2;
					layoutId = R.id.tab2;
				} else if (tabId.equals(INFO)) {
					fragment = pageFragment3;
					layoutId = R.id.tab3;
				}
				if (fragment != null) {
					switchTab(layoutId, fragment, tabId);
				}
			}

		});
		
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


	}
}
