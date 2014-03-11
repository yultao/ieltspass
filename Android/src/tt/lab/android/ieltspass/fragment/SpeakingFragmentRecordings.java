package tt.lab.android.ieltspass.fragment;

import java.util.ArrayList;
import java.util.List;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.activity.DailyActivity;
import tt.lab.android.ieltspass.data.Constants;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SpeakingFragmentRecordings extends Fragment {
	private static final String TAG = SpeakingFragmentRecordings.class.getName();
	private String questions;
	private ListView listView;
	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_speaking_recordings, container, false);
		initList();
		return view;
	}
	private void initList() {
		listView = (ListView) view.findViewById(R.id.listView1);
		final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_list_item_1, getData());
		listView.setAdapter(arrayAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
	}


	private List<String> getData() {

		List<String> data = new ArrayList<String>();
		data.add("[2014-03-10 11:14:12] 时长：03:04");
		data.add("[2014-03-11 18:24:13] 时长：13:12");
		return data;
	}
	public String getQuestions() {
		return questions;
	}

	public void setQuestions(String questions) {
		this.questions = questions;
	}

	

}