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

public class PageFragmentLSRW1 extends Fragment {
	View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_lsrw_tab, null);
		initExpandableList();
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initExpandableList() {
		
		final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
			// 设置组视图的图片
			int[] logos = new int[] { R.drawable.wei, R.drawable.shu, R.drawable.wu, R.drawable.shu };
			// 设置组视图的显示文字
			private String[] generalsTypes = new String[] { "听", "说", "读", "写" };
			private LsrwItem[][] generals = new LsrwItem[4][];
			{
				LsrwItem[] listeningItems = new LsrwItem[36];
				for(int i=0;i<listeningItems.length;i++){
					LsrwItem lsrwItem = new LsrwItem();
					lsrwItem.setTitle("剑桥雅思6测试1听力-Section"+i);
					lsrwItem.setType(1);
					lsrwItem.setQuestions("C1T1S1.Q.html");
					lsrwItem.setAnswers("C6T1S1.A.html");
					lsrwItem.setAudio("C6T1S1.mp3");
					lsrwItem.setLyrics("C6T1S1.lrc");
					listeningItems[i]=lsrwItem;
				}
//				int i=0;
//				LsrwItem lsrwItem = new LsrwItem();
//				lsrwItem.setTitle("剑桥雅思6测试1听力-Section1");
//				lsrwItem.setType(1);
//				lsrwItem.setQuestions("C1T1S1.Q.html");
//				lsrwItem.setAnswers("C6T1S1.A.html");
//				lsrwItem.setAudio("C6T1S1.mp3");
//				lsrwItem.setLyrics("C6T1S1.lrc");
//				listeningItems[i++]=lsrwItem;
//				
//				lsrwItem = new LsrwItem();
//				lsrwItem.setTitle("剑桥雅思6测试1听力-Section2");
//				lsrwItem.setType(1);
//				lsrwItem.setQuestions("C6T1S2.Q.html");
//				lsrwItem.setAnswers("C6T1S2.A.html");
//				lsrwItem.setAudio("C6T1S2.mp3");
//				lsrwItem.setLyrics("C6T1S2.lrc");
//				listeningItems[i++]=lsrwItem;
//				
//				lsrwItem = new LsrwItem();
//				lsrwItem.setTitle("剑桥雅思6测试1听力-Section3");
//				lsrwItem.setType(1);
//				lsrwItem.setQuestions("C6T1S3.Q.html");
//				lsrwItem.setAnswers("C6T1S3.A.html");
//				lsrwItem.setAudio("C6T1S3.mp3");
//				lsrwItem.setLyrics("C6T1S3.lrc");
//				listeningItems[i++]=lsrwItem;
//				
//				
//				lsrwItem = new LsrwItem();
//				lsrwItem.setTitle("剑桥雅思6测试1听力-Section4");
//				lsrwItem.setType(1);
//				lsrwItem.setQuestions("C6T1S4.Q.html");
//				lsrwItem.setAnswers("C6T1S4.A.html");
//				lsrwItem.setAudio("C6T1S4.mp3");
//				lsrwItem.setLyrics("C6T1S4.lrc");
//				listeningItems[i++]=lsrwItem;
//
//				lsrwItem = new LsrwItem();
//				lsrwItem.setTitle("王菲 - 棋子");
//				lsrwItem.setType(1);
//				lsrwItem.setAudio("qizi.mp3");
//				lsrwItem.setLyrics("qizi.lrc");
//				listeningItems[i++]=lsrwItem;
//				
//				
//				lsrwItem = new LsrwItem();
//				lsrwItem.setTitle("王铮亮 - 时间都去哪儿了");
//				lsrwItem.setType(1);
//				lsrwItem.setAudio("Time.mp3");
//				lsrwItem.setLyrics("Time.lrc");
//				listeningItems[i++]=lsrwItem;
				
				generals[0]=listeningItems;
				generals[1]=listeningItems;
				generals[2]=listeningItems;
				generals[3]=listeningItems;
			}
			// 子视图图思
			/*
			 * public int[][] generallogos = new int[][] { { R.drawable.xiahoudun, R.drawable.zhenji, R.drawable.xuchu,
			 * R.drawable.guojia, R.drawable.simayi, R.drawable.yangxiu }, { R.drawable.machao, R.drawable.zhangfei,
			 * R.drawable.liubei, R.drawable.zhugeliang, R.drawable.huangyueying, R.drawable.zhaoyun }, {
			 * R.drawable.lvmeng, R.drawable.luxun, R.drawable.sunquan, R.drawable.zhouyu, R.drawable.sunshangxiang } };
			 */
			TextView getTextView() {
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
				TextView textView = new TextView(PageFragmentLSRW1.this.getActivity());
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

				LinearLayout ll = new LinearLayout(PageFragmentLSRW1.this.getActivity());
				ll.setOrientation(0);
				ImageView logo = new ImageView(PageFragmentLSRW1.this.getActivity());
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
				LinearLayout ll = new LinearLayout(PageFragmentLSRW1.this.getActivity());
				ll.setOrientation(0);
				ImageView generallogo = new ImageView(PageFragmentLSRW1.this.getActivity());
				// generallogo.setImageResource(generallogos[groupPosition][childPosition]);
				ll.addView(generallogo);
				TextView textView = getTextView();
				textView.setTextColor(getResources().getColor(R.color.sub_text_color));
				textView.setText(getChild(groupPosition, childPosition).toString());
				ll.addView(textView);
				return ll;
			}

			public boolean isChildSelectable(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return true;
			}

		};//End of adapter

		ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView1);
		expandableListView.setAdapter(adapter);

		expandableListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//Toast.makeText(PageFragmentLSRW.this.getActivity(),"你点击了" + arg0 + ", " + arg1 + ", " + arg2 + ", " + arg3, Toast.LENGTH_SHORT).show();

			}

		});
		// 设置item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

				//Toast.makeText(PageFragmentLSRW.this.getActivity(),	"你点击了" + adapter.getChild(groupPosition, childPosition), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(getActivity(), ListeningActivity.class);
				LsrwItem lsrwItem = (LsrwItem)adapter.getChild(groupPosition, childPosition);

				switch (lsrwItem.getType()){
				case 1:
					intent.putExtra("title", lsrwItem.getTitle());
					intent.putExtra("type",lsrwItem.getType()); 
					intent.putExtra("audio",lsrwItem.getAudio()); 
					intent.putExtra("lyrics",lsrwItem.getLyrics()); 
					intent.putExtra("questions",lsrwItem.getQuestions()); 
					intent.putExtra("answers",lsrwItem.getAnswers());
					startActivity(intent);
					break;
				}
				
				return false;
			}
		});
		expandableListView.expandGroup(0);
		expandableListView.expandGroup(1);
		expandableListView.expandGroup(2);
		expandableListView.expandGroup(3);
	}
}
