
package tt.lab.android.ieltspass.fragment;

import java.util.ArrayList;
import java.util.List;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.activity.DailyActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CenterFragmentDaily extends Fragment {
	private static final String TAG = CenterFragmentDaily.class.getName();
	private ListView listView;
	private View view;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Logger.i(TAG, "onCreateView");
		view = inflater.inflate(R.layout.fragment_daily, null);
		initList();
		return view;
	}

	private void initList() {
		listView = (ListView) view.findViewById(R.id.listView1);
		final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_expandable_list_item_1, getData());
		listView.setAdapter(arrayAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("onItemClick", "position: " + position + ", id: " + id);
				String item = (String) arrayAdapter.getItem(position);
				Intent intent = new Intent();
				intent.setClass(getActivity(), DailyActivity.class);
				intent.putExtra("title", item);
				startActivity(intent);
			}
		});
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private List<String> getData() {

		List<String> data = new ArrayList<String>();
		data.add("每日一词");
		data.add("每日一听");
		data.add("每日一句");
		data.add("每日一读");

		return data;
	}
}
