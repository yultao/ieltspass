package tt.lab.android.ieltspass.fragment;

import java.util.HashMap;
import java.util.Map;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.activity.ListeningActivity;
import tt.lab.android.ieltspass.activity.SettingsActivity;
import tt.lab.android.ieltspass.data.LsrwItem;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PageFragmentLSRWTab extends Fragment {
	View view;
	private int type;

	public PageFragmentLSRWTab() {

	}

	public void setType(int type) {
		this.type = type;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_lsrw_tab, null);
		initExpandableList();
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initExpandableList() {

		ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView1);
		final ExpandableListAdapter adapter = new ExpandableListAdapter();
		expandableListView.setAdapter(adapter);

		// 设置item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

				// Toast.makeText(PageFragmentLSRW.this.getActivity(), "你点击了" + adapter.getChild(groupPosition,
				// childPosition), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(getActivity(), ListeningActivity.class);
				LsrwItem lsrwItem = (LsrwItem) adapter.getChild(groupPosition, childPosition);

				switch (lsrwItem.getType()) {
				case 1:
					intent.putExtra("title", lsrwItem.getTitle());
					intent.putExtra("type", lsrwItem.getType());
					intent.putExtra("audio", lsrwItem.getAudio());
					intent.putExtra("lyrics", lsrwItem.getLyrics());
					intent.putExtra("questions", lsrwItem.getQuestions());
					intent.putExtra("answers", lsrwItem.getAnswers());
					startActivity(intent);
					break;
				}

				return false;
			}
		});
	}

	private class ExpandableListAdapter extends BaseExpandableListAdapter {

		int[] logos = new int[] { R.drawable.wei, R.drawable.shu, R.drawable.wu, R.drawable.shu, R.drawable.wei,
				R.drawable.shu, R.drawable.wu, R.drawable.shu, R.drawable.wei };

		private String[] generalsTypes = new String[9];

		private LsrwItem[][] generals = new LsrwItem[9][];

		ExpandableListAdapter() {
			for (int i = 0; i <9; i++)
				generalsTypes[i] = "剑桥雅思" + (9-i);

			initData ();
		}
		void initData() {
			String t = null;
			switch (type){
			case 1:
				t = "听力";
				break;
			case 2:
				t = "口语";
				break;
			case 3:
				t = "阅读";
				break;
			case 4:
				t = "写作";
				break;
			}
			for(int i=0;i<9;i++){
				LsrwItem[] listeningItems = new LsrwItem[16];
				for(int j=0;j<4;j++){
					for(int k=0;k<4;k++){
						String name = "C"+(i+1)+"T"+(j+1)+"S"+(k+1);
						LsrwItem lsrwItem = new LsrwItem();
						
						lsrwItem.setTitle("剑桥雅思"+(i+1)+"-测试"+(j+1)+"-"+t+"Section"+(k+1));
						lsrwItem.setType(1);
						lsrwItem.setQuestions(name+".Q.html");
						lsrwItem.setAnswers(name+".A.html");
						lsrwItem.setAudio(name+".mp3");
						lsrwItem.setLyrics(name+".lrc");
						listeningItems[j*4+k] = lsrwItem;
					}
				}
				
				generals[8-i] = listeningItems;
			}
		}

		// 子视图图思
		/*
		 * public int[][] generallogos = new int[][] { { R.drawable.xiahoudun, R.drawable.zhenji, R.drawable.xuchu,
		 * R.drawable.guojia, R.drawable.simayi, R.drawable.yangxiu }, { R.drawable.machao, R.drawable.zhangfei,
		 * R.drawable.liubei, R.drawable.zhugeliang, R.drawable.huangyueying, R.drawable.zhaoyun }, { R.drawable.lvmeng,
		 * R.drawable.luxun, R.drawable.sunquan, R.drawable.zhouyu, R.drawable.sunshangxiang } };
		 */
		TextView getTextView() {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
			TextView textView = new TextView(PageFragmentLSRWTab.this.getActivity());
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setPadding(10, 0, 0, 0);
			textView.setTextSize(18);
			return textView;
		}

		// 重写ExpandableListAdapter中的各个方法
		public int getGroupCount() {
			return generalsTypes.length;
		}

		public Object getGroup(int groupPosition) {
			return generalsTypes[groupPosition];
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return generals[groupPosition].length;
		}

		public Object getChild(int groupPosition, int childPosition) {
			return generals[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public boolean hasStableIds() {
			return true;
		}

		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

			LinearLayout ll = new LinearLayout(PageFragmentLSRWTab.this.getActivity());
			ll.setOrientation(0);
			ImageView logo = new ImageView(PageFragmentLSRWTab.this.getActivity());
			logo.setImageResource(logos[groupPosition]);
			logo.setPadding(60, 0, 0, 0);
			ll.addView(logo);
			TextView textView = getTextView();
			textView.setTextColor(Color.BLACK);
			textView.setText(getGroup(groupPosition).toString());
			ll.addView(textView);

			return ll;
		}

		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			LinearLayout ll = new LinearLayout(PageFragmentLSRWTab.this.getActivity());
			ll.setOrientation(0);
			ImageView generallogo = new ImageView(PageFragmentLSRWTab.this.getActivity());
			// generallogo.setImageResource(generallogos[groupPosition][childPosition]);
			ll.addView(generallogo);
			TextView textView = getTextView();
			textView.setPadding(60, 0, 0, 0);
			textView.setTextColor(getResources().getColor(R.color.sub_text_color));
			textView.setText(getChild(groupPosition, childPosition).toString());
			ll.addView(textView);
			return ll;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}
