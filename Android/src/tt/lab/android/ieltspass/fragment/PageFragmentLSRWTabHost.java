package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.data.Logger;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class PageFragmentLSRWTabHost extends Fragment {
	View view;
	private static final String TAG = PageFragmentLSRWTabHost.class.getName();

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
			final Button button = new Button(this.getActivity());
			buttons[i] = button;
			button.setBackgroundColor(getResources().getColor(R.color.bg_color));
			button.setHeight(20);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					button.setBackgroundColor(getResources().getColor(R.color.blue));
				}
			});
			button.setText(title[i]);
			tabHost.addTab(tabHost.newTabSpec(title[i]).setIndicator(button).setContent(tabIds[i]));
		}
		final PageFragmentLSRWTab[] fragments = new PageFragmentLSRWTab[4];
		for(int i=0;i<fragments.length;i++){
			PageFragmentLSRWTab pageFragment = new PageFragmentLSRWTab();
			pageFragment.setType(i+1);
			fragments[i] = pageFragment;
		}
		 
		switchTab(R.id.tab1, fragments[0], "听");// 默认页替换成page1
		buttons[0].setBackgroundColor(getResources().getColor(R.color.red));

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				PageFragmentLSRWTab fragment = null;
				int i = 0;
				int layoutId = 0;
				if (tabId.equals("听")) {
					fragment = fragments[0];
					layoutId = R.id.tab1;
					i = 0;
				} else if (tabId.equals("说")) {
					fragment = fragments[1];
					layoutId = R.id.tab2;
					i = 1;
				} else if (tabId.equals("读")) {
					fragment = fragments[2];
					layoutId = R.id.tab3;
					i = 2;
				} else if (tabId.equals("写")) {
					fragment = fragments[3];
					layoutId = R.id.tab4;
					i = 3;
				}
				for (int a = 0; a < buttons.length; a++) {
					buttons[a].setBackgroundColor(getResources().getColor(R.color.bg_color));
				}
				buttons[i].setBackgroundColor(getResources().getColor(R.color.red));
				if (fragment != null) {
					switchTab(layoutId, fragment, tabId);
					// switchTab(i,fragment);
				}
			}

		});

	}

	private void switchTab(int layoutId, Fragment fragment, String destId) {
		try {
			LinearLayout linearLayout = (LinearLayout) view.findViewById(layoutId);
			Logger.i(TAG , "switchTab getChildCount: "+layoutId+", "+linearLayout.getChildCount());
			
			final FragmentManager fragmentManager = this.getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(layoutId, fragment, destId);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();
		} catch (Exception e) {
			Logger.i(TAG , "switchTab E: "+e.getMessage());
			e.printStackTrace();
		}
	}

	private void switchTab(int type, PageFragmentLSRWTab fragment) {
		fragment.reload(type);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
