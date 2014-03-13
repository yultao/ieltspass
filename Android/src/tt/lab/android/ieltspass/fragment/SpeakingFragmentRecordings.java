package tt.lab.android.ieltspass.fragment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tt.lab.android.ieltspass.Constants;
import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.activity.SpeakingActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SpeakingFragmentRecordings extends Fragment {
	private static final String TAG = SpeakingFragmentRecordings.class.getName();
	private SpeakingActivity speakingActivity;
	private String questions;
	private ListView listView;

	private View view;
	private ArrayAdapter arrayAdapter;// TODO to be change to simpleAdapter.
	private List<String> listData;
	private String name;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_speaking_recordings, container, false);

		initList();
		return view;
	}

	private void initList() {
		listView = (ListView) view.findViewById(R.id.listView1);
		refreshListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textView = (TextView) view;
				String filename = listData.get(position);
				boolean success = speakingActivity.resetPlayer(Constants.SPEAKING_AUDIO_PATH + "/" + name + "/"
						+ filename, true);
				if (success) {
					resetListView(position);// 当前标红
				}
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				showDialog(position);
				return false;
			}
		});
	}

	private void showDialog(final int position) {
		final String filename = listData.get(position);
		
		Builder builder = new Builder(this.getActivity());
		builder.setMessage("确定删除"+filename+"？");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteAudio(Constants.SPEAKING_AUDIO_PATH + "/" + name + "/" + filename);
				refreshListView();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}

	private void deleteAudio(String absFileName) {
		File home = new File(absFileName);
		home.delete();
	}

	private void resetListView(int position) {
		for (int i = 0; i < listView.getChildCount(); i++) {
			TextView textView = (TextView) listView.getChildAt(i);
			if (i == position) {
				textView.setTextColor(getResources().getColor(R.color.red));
			} else {
				textView.setTextColor(getResources().getColor(R.color.black));
			}
		}
}

	private List<String> listAudios() {
		List<String> data = new ArrayList<String>();
		try {

			File dir = new File(Constants.SPEAKING_AUDIO_PATH + "/" + name);
			if (dir.exists()){
				for (File file : dir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return (filename.endsWith(".amr"));
					}
				})) {
					data.add(file.getName());
				}
				Collections.reverse(data);
			}
		} catch (Exception e) {
			Logger.i(TAG, "listAudios: " + e);
		}
		Logger.i(TAG, "listAudios: " + data.size());
		return data;
	}

	public void refreshListView() {
		listData = listAudios();
		arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, listData);
		listView.setAdapter(arrayAdapter);
	}

	public String getQuestions() {
		return questions;
	}

	public void setQuestions(String questions) {
		this.questions = questions;
	}

	public SpeakingActivity getSpeakingActivity() {
		return speakingActivity;
	}

	public void setSpeakingActivity(SpeakingActivity speakingActivity) {
		this.speakingActivity = speakingActivity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}