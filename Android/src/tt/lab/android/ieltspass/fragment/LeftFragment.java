package tt.lab.android.ieltspass.fragment;

import java.util.ArrayList;
import java.util.List;

import tt.lab.android.ieltspass.R;
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
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_list_item_1, getData());
		listView.setAdapter(arrayAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("onItemClick", "position: " + position + ", id: " + id);

				Intent intent = new Intent();
				switch (position){
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


	private List<String> getData() {

		List<String> data = new ArrayList<String>();
		
		data.add("设置");
		data.add("下载离线包");
		data.add("意见反馈");
		data.add("关于");

		return data;
	}
}
