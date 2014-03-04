
package tt.lab.android.ieltspass.fragment;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;


public class CenterFragment extends Fragment {
	protected ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();

	protected PageChangeListener myPageChangeListener;
	protected ViewPager mPager;
	

	public boolean isFirst() {
		if (mPager.getCurrentItem() == 0)
			return true;
		else
			return false;
	}

	public boolean isEnd() {
		if (mPager.getCurrentItem() == pagerItemList.size() - 1)
			return true;
		else
			return false;
	}

	public void setMyPageChangeListener(PageChangeListener l) {

		myPageChangeListener = l;

	}
	public interface PageChangeListener {
		public void onPageSelected(int position);
	}
}
