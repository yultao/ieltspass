package tt.lab.android.ieltspass.fragment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.activity.AboutActivity;
import tt.lab.android.ieltspass.activity.DownloadActivity;
import tt.lab.android.ieltspass.activity.FeedbackActivity;
import tt.lab.android.ieltspass.activity.LoginActivity;
import tt.lab.android.ieltspass.activity.SettingsActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LeftFragment extends Fragment {
	private ListView listView;

	private View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.left, null);
		TextView name = (TextView) view.findViewById(R.id.name_textview);
		name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				intent.putExtra("title", "");
				startActivity(intent);
			}
		});
		initList();
		return view;
	}

	private void initList() {
		listView = (ListView) view.findViewById(R.id.listView1);
//		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
//				android.R.layout.simple_list_item_1, getData());
		SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), getData(), R.layout.left_vlist, new String[] { "img", "title" },
				new int[] { R.id.img, R.id.title });

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("onItemClick", "position: " + position + ", id: " + id);

				Intent intent = new Intent();
				switch (position) {
				case 0:
					intent.setClass(getActivity(), SettingsActivity.class);
					break;
				case 1:
					intent.setClass(getActivity(), DownloadActivity.class);
					break;
				case 2:
					intent.setClass(getActivity(), FeedbackActivity.class);
					break;
				case 3:
					intent.setClass(getActivity(), AboutActivity.class);
					break;
				}

				startActivity(intent);
			}
		});
	}

	private List<Map<String, String>> getData() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		Map<String, String> map = new HashMap<String, String>();
		map.put("img", String.valueOf(R.drawable.settings));
		map.put("title", "系统设置");
		data.add(map);
		
		map = new HashMap<String, String>();
		map.put("img", String.valueOf(R.drawable.offlinepack));
		map.put("title", "离线包");
		data.add(map);
		
		map = new HashMap<String, String>();
		map.put("img", String.valueOf(R.drawable.feedback));
		map.put("title", "意见反馈");
		data.add(map);
		
		map = new HashMap<String, String>();
		map.put("img", String.valueOf(R.drawable.about));
		map.put("title", "关于");
		data.add(map);

		return data;
	}

	private List<String> getData1() {

		List<String> data = new ArrayList<String>();

		data.add("  系统设置");
		data.add("  下载离线包");
		data.add("  意见反馈");
		data.add("  关于");

		return data;
	}
}
