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

public class PageFragmentLSRWTabHost extends Fragment {
	View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_lsrw_tabhost, null);
		initTabHost(view);
		return view;
	}
	private void initTabHost(View view) {
		
		TabHost tabHost = (TabHost) view.findViewById(R.id.tabhost);
		tabHost.setup();
		final Button[] buttons = new Button[4];
		String[] title = new String[] { "听", "说", "读", "写" };
		int[] tabIds = new int[] { R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4 };
		for (int i = 0; i < title.length; i++) {
			Button button = new Button(this.getActivity());
			buttons[i]=button;
			button.setBackgroundColor(getResources().getColor(R.color.bg_color));
			button.setText(title[i]);
			// button.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shade_bg));
			tabHost.addTab(tabHost.newTabSpec(title[i]).setIndicator(button).setContent(tabIds[i]));
		}

		final PageFragmentLSRWTab pageFragment1 = new PageFragmentLSRWTab();
		pageFragment1.setType(1);
		
		final PageFragmentLSRWTab pageFragment2 = new PageFragmentLSRWTab();
		pageFragment2.setType(2);
		
		final PageFragmentLSRWTab pageFragment3 = new PageFragmentLSRWTab();
		pageFragment3.setType(3);
		
		final PageFragmentLSRWTab pageFragment4 = new PageFragmentLSRWTab();
		pageFragment4.setType(4);
		switchTab(R.id.tab1, pageFragment1, "听");// 默认页替换成page1
		buttons[0].setBackgroundColor(getResources().getColor(R.color.bg_color));
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				Fragment fragment = null;
				int i =0;
				int layoutId = 0;
				if (tabId.equals("听")) {
					fragment = pageFragment1;
					layoutId = R.id.tab1;
					i=0;
				} else if (tabId.equals("说")) {
					fragment = pageFragment2;
					layoutId = R.id.tab2;
					i=1;
				} else if (tabId.equals("读")) {
					fragment = pageFragment3;
					layoutId = R.id.tab3;
					i=2;
				}else if (tabId.equals("写")) {
					fragment = pageFragment4;
					layoutId = R.id.tab4;
					i=3;
				}
				for(int a=0;a<buttons.length;a++){
					buttons[a].setBackgroundColor(getResources().getColor(R.color.bg_color));
				}
				buttons[i].setBackgroundColor(getResources().getColor(R.color.red));
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
