package tt.lab.android.ieltspass.fragment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tt.lab.android.ieltspass.Constants;
import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.activity.SpeakingActivity;
import tt.lab.android.ieltspass.data.Settings;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SpeakingFragmentRecordings extends Fragment {
	private static final String TAG = SpeakingFragmentRecordings.class.getName();
	private SpeakingActivity speakingActivity;
	private String questions;
	private ListView listView;
	private Settings settings;

	private View view;
	private SimpleAdapter simpleAdapter;
	private List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
	private String name;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = Settings.getInstance(this.getActivity());
	}
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
				Map<String, String> map = listData.get(position);
				Logger.i(TAG, "onItemClick: "+map);
				boolean success = speakingActivity.resetPlayer(map.get("absFileName"), true);
				if (success) {
					resetListViewColor(position);// 当前标红
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
		Map<String, String> map = listData.get(position);
		final String filename = map.get("name");
		final String absFileName = map.get("absFileName");
		Builder builder = new Builder(this.getActivity());
		builder.setMessage("确定删除" + filename + "？");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteAudio(absFileName);
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
		File audio = new File(absFileName);
		audio.delete();
		
		File t = new File(absFileName.replace("amr", "t"));
		t.delete();
	}

	private void resetListViewColor(int position) {
		for (int i = 0; i < listView.getChildCount(); i++) {
			LinearLayout linearLayout = (LinearLayout) listView.getChildAt(i);
			TextView textView = (TextView)linearLayout.getChildAt(0);
			if (i == position) {
				textView.setTextColor(getResources().getColor(R.color.red));
			} else {
				textView.setTextColor(getResources().getColor(R.color.black));
			}
		}
	}

	private List<Map<String, String>> listAudios() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try {

			File dir = new File(settings.getSpeakingAudiosPath() + "/" + name);
			if (dir.exists()) {
				for (File file : dir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return (filename.endsWith(".amr"));
					}
				})) {
					String name = null;
					String duration = null;
					String datetime = null;
					String length = null;
					long len = file.length();
					if(len>=1024*1024){
						length = len/1024/1024+"MB";
					} else if (len>=1024){
						length = len/1024+"KB";
					} else {
						length = len+"B";
					}
					List<String> readFile = Utilities.readFile(file.getAbsolutePath().replace("amr", "t"));

					if (readFile.size() == 1) {
						String[] ss = readFile.get(0).split("\t");
						if (ss.length == 3) {
							datetime = ss[0];
							duration = ss[1];
							name = ss[2];
						}
					} else {
						name = file.getName();
					}
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("name", name);
					map.put("datetime", datetime);
					//map.put("duration", duration);
					map.put("length", length+"  "+duration);
					map.put("absFileName", file.getAbsolutePath());
					data.add(map);
				}
			}
		} catch (Exception e) {
			Logger.e(TAG, "listAudios: " + e);
		}
		Logger.i(TAG, "listAudios: " + data.size());
		return data;
	}

	public void refreshListView() {
		listData = listAudios();
		// arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, listData2);
		// listView.setAdapter(arrayAdapter);

		simpleAdapter = new SimpleAdapter(this.getActivity(), listData, R.layout.fragment_speaking_recordings_vlist,
				new String[] { "name", "datetime", "length" }, new int[] { R.id.name, R.id.datetime, R.id.length });
		listView.setAdapter(simpleAdapter);
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