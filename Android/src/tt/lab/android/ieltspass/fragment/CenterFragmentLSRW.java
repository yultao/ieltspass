package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.activity.ListeningActivity;
import tt.lab.android.ieltspass.activity.ReadingActivity;
import tt.lab.android.ieltspass.activity.SpeakingActivity;
import tt.lab.android.ieltspass.activity.WritingActivity;
import tt.lab.android.ieltspass.model.LsrwItem;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CenterFragmentLSRW extends Fragment {
	private static int tabSelected;
	private static int[][] itemSelected = new int[4][9];
	private String TAG = CenterFragmentLSRW.class.getName();
	private Button button1, button2, button3, button4;
	private Button[] buttons = new Button[4];
	private View view;
	private int type = 0;
	private static String[] tt = { "听力", "口语", "阅读", "写作" };
	private ExpandableListView expandableListView;
	private ExpandableListAdapter adapter;

	public void setType(int type) {
		this.type = type;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Logger.i(TAG, "onCreateView " + view);

		view = inflater.inflate(R.layout.center_fragment_lsrw, null);
		button1 = (Button) view.findViewById(R.id.button1);
		button2 = (Button) view.findViewById(R.id.button2);
		button3 = (Button) view.findViewById(R.id.button3);
		button4 = (Button) view.findViewById(R.id.button4);
		buttons[0] = button1;
		buttons[1] = button2;
		buttons[2] = button3;
		buttons[3] = button4;

		switch (type){
		case 0://listening
			itemSelected = new int[4][10];
			break;
		case 1:
			itemSelected = new int[4][9];
			break;
		case 2:
			itemSelected = new int[4][9];
			break;
		case 3:
			itemSelected = new int[4][9];
			break;
		}
		
		refreshButtonColor(tabSelected);
		buttons[0].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshExpandableList(0);
				refreshButtonColor(0);
			}
		});
		buttons[1].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshExpandableList(1);
				refreshButtonColor(1);
			}
		});
		buttons[2].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshExpandableList(2);
				refreshButtonColor(2);
			}
		});
		buttons[3].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshExpandableList(3);
				refreshButtonColor(3);
			}
		});

		initExpandableList();

		return view;
	}

	private void refreshButtonColor(int index) {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabbutton_background));
			buttons[i].setTextColor(getResources().getColor(R.color.black));
		}
		buttons[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabbutton_background_selected));
		buttons[index].setTextColor(getResources().getColor(R.color.red));

		tabSelected = index;
		
		for (int i = 0; i < itemSelected[tabSelected].length; i++) {
			if (itemSelected[tabSelected][i] != 0)
				expandableListView.expandGroup(i);
		}

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initExpandableList() {
		//Logger.i(TAG, "expandableListView: " + expandableListView);
		expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView1);
		resetList();
		expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				itemSelected[tabSelected][groupPosition] = 1;
				//Logger.i(TAG, "onGroupExpand: " + groupPosition);
			}
		});
		expandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				itemSelected[tabSelected][groupPosition] = 0;
			}
		});
		// 设置item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

				// Toast.makeText(PageFragmentLSRW.this.getActivity(), "你点击了" + adapter.getChild(groupPosition,
				// childPosition), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				LsrwItem lsrwItem = (LsrwItem) adapter.getChild(groupPosition, childPosition);
				switch (lsrwItem.getType()) {
				case 0:
					intent.setClass(getActivity(), ListeningActivity.class);
					intent.putExtra("title", lsrwItem.getTitle());
					intent.putExtra("type", lsrwItem.getType());
					intent.putExtra("audio", lsrwItem.getAudio());
					intent.putExtra("lyrics", lsrwItem.getLyrics());
					intent.putExtra("questions", lsrwItem.getQuestions());
					intent.putExtra("answers", lsrwItem.getAnswers());
					break;
				case 1:
					intent.setClass(getActivity(), SpeakingActivity.class);
					intent.putExtra("title", lsrwItem.getTitle());
					intent.putExtra("type", lsrwItem.getType());
					intent.putExtra("questions", lsrwItem.getQuestions());
					intent.putExtra("answers", lsrwItem.getAnswers());
					break;
				case 2:
					intent.setClass(getActivity(), ReadingActivity.class);
					intent.putExtra("title", lsrwItem.getTitle());
					intent.putExtra("type", lsrwItem.getType());
					intent.putExtra("questions", lsrwItem.getQuestions());
					intent.putExtra("answers", lsrwItem.getAnswers());
					break;
				case 3:
					intent.setClass(getActivity(), WritingActivity.class);
					intent.putExtra("title", lsrwItem.getTitle());
					intent.putExtra("type", lsrwItem.getType());
					intent.putExtra("questions", lsrwItem.getQuestions());
					intent.putExtra("answers", lsrwItem.getAnswers());
					break;
				}
				startActivity(intent);
				return false;
			}
		});
	}

	private void resetList() {
		adapter = new ExpandableListAdapter();
		expandableListView.setAdapter(adapter);
	}

	public void refreshExpandableList(int type) {
		this.setType(type);
		resetList();
	}
	private String[] initParent() {
		String[] generalsTypes = null;
		switch (type){
		case 0://listening
			generalsTypes = new String[10];
			generalsTypes[9] = "王陆807";
			break;
		case 1:
			generalsTypes = new String[9];
			break;
		case 2:
			generalsTypes = new String[9];
			break;
		case 3:
			generalsTypes = new String[9];
			break;
		}

		for (int i = 0; i < 9; i++){
			generalsTypes[i] = "剑桥雅思" + (i + 1);
		}
		return generalsTypes;
	}
	private LsrwItem[][] initChildren() {
		
		LsrwItem[][] generals = null;
		switch (type){
		case 0://listening
			generals = new LsrwItem[10][];
			generals[9] = init807();
			break;
		case 1:
			generals = new LsrwItem[9][];
			break;
		case 2:
			generals = new LsrwItem[9][];
			break;
		case 3:
			generals = new LsrwItem[9][];
			break;
		}
		for (int i = 0; i < 9; i++) {// cambridge
			LsrwItem[] listeningItems = initCambridge(i);
			generals[i] = listeningItems;
		}
		return generals;
	}
	private LsrwItem[] init807() {
		LsrwItem[] listeningItems = new LsrwItem[1];
		
		String name = "基础词汇";
		LsrwItem lsrwItem = new LsrwItem();
		lsrwItem.setTitle("王陆807"+name);
		lsrwItem.setType(type);
		lsrwItem.setQuestions(name + ".Q.html");
		lsrwItem.setAnswers(name + ".A.html");
		lsrwItem.setAudio(name + ".mp3");
		lsrwItem.setLyrics(name + ".lrc");
		listeningItems[0] = lsrwItem;
		
		return listeningItems;
	}
	private LsrwItem[] initCambridge(int i) {
		String t = tt[type];
		LsrwItem[] listeningItems = new LsrwItem[16];
		for (int j = 0; j < 4; j++) {// test
			for (int k = 0; k < 4; k++) {// section
				String name = (i + 1) + "-" + (j + 1) + "-" + (k + 1);
				LsrwItem lsrwItem = new LsrwItem();
				lsrwItem.setTitle("剑桥雅思" + (i + 1) + "-" + t + (j + 1) + "-" + "Section" + (k + 1));
				lsrwItem.setType(type);
				lsrwItem.setQuestions(name + ".Q.html");
				lsrwItem.setAnswers(name + ".A.html");
				lsrwItem.setAudio(name + ".mp3");
				lsrwItem.setLyrics(name + ".lrc");
				listeningItems[j * 4 + k] = lsrwItem;
			}
		}
		return listeningItems;
	}

	private class ExpandableListAdapter extends BaseExpandableListAdapter {

		int[] logos = new int[] { R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5,
				R.drawable.c6, R.drawable.c7, R.drawable.c8, R.drawable.c9, R.drawable.c9 };

		private String[] generalsTypes = null;

		private LsrwItem[][] generals;

		public ExpandableListAdapter() {
			generalsTypes = initParent();
			generals = initChildren();
		}

		

		// 子视图图思
		/*
		 * public int[][] generallogos = new int[][] { { R.drawable.xiahoudun, R.drawable.zhenji, R.drawable.xuchu,
		 * R.drawable.guojia, R.drawable.simayi, R.drawable.yangxiu }, { R.drawable.machao, R.drawable.zhangfei,
		 * R.drawable.liubei, R.drawable.zhugeliang, R.drawable.huangyueying, R.drawable.zhaoyun }, { R.drawable.lvmeng,
		 * R.drawable.luxun, R.drawable.sunquan, R.drawable.zhouyu, R.drawable.sunshangxiang } };
		 */
		private TextView getTextView() {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
			TextView textView = new TextView(CenterFragmentLSRW.this.getActivity());
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
			// Logger.i(TAG, "getChildrenCount "+groupPosition );
			return generals[groupPosition].length;
		}

		public Object getChild(int groupPosition, int childPosition) {
			// Logger.i(TAG, "getChild "+groupPosition +", "+ childPosition);
			return generals[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			// Logger.i(TAG, "getChildId "+groupPosition +", "+ childPosition);
			return childPosition;
		}

		public boolean hasStableIds() {
			return true;
		}

		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

			LinearLayout ll = new LinearLayout(CenterFragmentLSRW.this.getActivity());
			ll.setOrientation(0);
			ImageView logo = new ImageView(CenterFragmentLSRW.this.getActivity());
			logo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 75));
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
			LinearLayout ll = new LinearLayout(CenterFragmentLSRW.this.getActivity());
			ll.setOrientation(0);
			ImageView generallogo = new ImageView(CenterFragmentLSRW.this.getActivity());
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
