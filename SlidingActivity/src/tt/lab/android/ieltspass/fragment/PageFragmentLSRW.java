
package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.R;
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

public class PageFragmentLSRW extends Fragment {
	View view;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.page_lsrw, null);
		/*
		TabHost mTabHost = (TabHost) view.findViewById(R.id.tabhost);
		mTabHost.setup();

		String[] title = new String[] { "涓婚思, "甯思, "娴嬭思 };
		int[] tabIds = new int[] { R.id.tab1, R.id.tab2, R.id.tab3 };
		for (int i = 0; i < title.length; i++) {
			Button button = new Button(this.getActivity());
			button.setText(title[i]);
			// button.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.tab_lable)); //鑷畾涔夋寜閽牱寮思		
			mTabHost.addTab(mTabHost.newTabSpec(title[i]).setIndicator(button).setContent(tabIds[i]));
		}*/
		 initExpandableList();
		return view;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	private void initExpandableList(){
		final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            //设置组视图的图片
            int[] logos = new int[] { R.drawable.wei, R.drawable.shu,R.drawable.wu, R.drawable.shu};
            //设置组视图的显示文字
            private String[] generalsTypes = new String[] { "听", "说", "读", "写" };
            private String[][] generals = new String[][] {
                    { "剑桥雅思1", "剑桥雅思2", "剑桥雅思3", "剑桥雅思4", "剑桥雅思5", "剑桥雅思6", "剑桥雅思7", "剑桥雅思8", "剑桥雅思9"},
                    { "Part1", "Part2", "Part3" },
                    { "剑桥雅思1", "剑桥雅思2", "剑桥雅思3", "剑桥雅思4", "剑桥雅思5", "剑桥雅思6", "剑桥雅思7", "剑桥雅思8", "剑桥雅思9"},
                    { "剑桥雅思1", "剑桥雅思2", "剑桥雅思3", "剑桥雅思4", "剑桥雅思5", "剑桥雅思6", "剑桥雅思7", "剑桥雅思8", "剑桥雅思9"}

            };
            //子视图图思            
            /*
            public int[][] generallogos = new int[][] {
                    { R.drawable.xiahoudun, R.drawable.zhenji,
                            R.drawable.xuchu, R.drawable.guojia,
                            R.drawable.simayi, R.drawable.yangxiu },
                    { R.drawable.machao, R.drawable.zhangfei,
                            R.drawable.liubei, R.drawable.zhugeliang,
                            R.drawable.huangyueying, R.drawable.zhaoyun },
                    { R.drawable.lvmeng, R.drawable.luxun, R.drawable.sunquan,
                            R.drawable.zhouyu, R.drawable.sunshangxiang } };
            */
            //自己定义思��获得文字信息的方思            
            TextView getTextView() {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, 64);
                TextView textView = new TextView(PageFragmentLSRW.this.getActivity());
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(36, 0, 0, 0);
                textView.setTextSize(20);
                textView.setTextColor(Color.BLACK);
                return textView;
            }

            
            //重写ExpandableListAdapter中的各个方法
            public int getGroupCount() {
                return generalsTypes.length;
            }

            
            public Object getGroup(int groupPosition) {
                // TODO Auto-generated method stub
                return generalsTypes[groupPosition];
            }

            
            public long getGroupId(int groupPosition) {
                // TODO Auto-generated method stub
                return groupPosition;
            }

            
            public int getChildrenCount(int groupPosition) {
                // TODO Auto-generated method stub
                return generals[groupPosition].length;
            }

            
            public Object getChild(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return generals[groupPosition][childPosition];
            }

           
            public long getChildId(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return childPosition;
            }

            
            public boolean hasStableIds() {
                return true;
            }

            
            public View getGroupView(int groupPosition, boolean isExpanded,
                    View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(PageFragmentLSRW.this.getActivity());
                ll.setOrientation(0);
                ImageView logo = new ImageView(PageFragmentLSRW.this.getActivity());
                //logo.setImageResource(logos[groupPosition]);
                logo.setPadding(40, 0, 0, 0);
                ll.addView(logo);
                TextView textView = getTextView();
                textView.setTextColor(Color.BLACK);
                textView.setText(getGroup(groupPosition).toString());
                ll.addView(textView);

                return ll;
            }

           
            public View getChildView(int groupPosition, int childPosition,
                    boolean isLastChild, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(PageFragmentLSRW.this.getActivity());
                ll.setOrientation(0);
                ImageView generallogo = new ImageView(PageFragmentLSRW.this.getActivity());
                //generallogo.setImageResource(generallogos[groupPosition][childPosition]);
                ll.addView(generallogo);
                TextView textView = getTextView();
                textView.setText(getChild(groupPosition, childPosition)
                        .toString());
                ll.addView(textView);
                return ll;
            }

            
            public boolean isChildSelectable(int groupPosition,
                    int childPosition) {
                // TODO Auto-generated method stub
                return true;
            }

        };

        ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView1);
        expandableListView.setAdapter(adapter);
        
        expandableListView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Toast.makeText(PageFragmentLSRW.this.getActivity(),
                        "你点击了" + arg0+", "+arg1+", "+arg2+", "+arg3,
                        Toast.LENGTH_SHORT).show();
				
			}
        	
        });
        //设置item点击的监听器
        expandableListView.setOnChildClickListener(new OnChildClickListener() {

           
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {

                Toast.makeText(PageFragmentLSRW.this.getActivity(),
                        "你点击了" + adapter.getChild(groupPosition, childPosition),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
	}
}
