package tt.lab.android.ieltspass.fragment;

import java.util.ArrayList;
import java.util.List;

import tt.lab.android.ieltspass.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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