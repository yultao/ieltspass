package tt.lab.android.ieltspass.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.R.layout;
import tt.lab.android.ieltspass.R.menu;
import tt.lab.android.ieltspass.model.Word;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class DownloadActivity extends Activity {
	private SimpleAdapter simpleAdapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		initTitle();
		initListView();
	}

	private void initListView() {
		listView = (ListView) findViewById(R.id.listView1);
		simpleAdapter = new DownloadSimpleAdapter(this, getListData(), R.layout.activity_download_vlist, new String[] {
				"name", "progressBar1", "current", "length", "button1" }, new int[] { R.id.name, R.id.progressBar1, R.id.current, R.id.length,R.id.button1});
		listView.setAdapter(simpleAdapter);
	}

	private List<Map<String, String>> getListData() {

		List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

			Map<String, String> map = new HashMap<String, String>();
			map.put("name", "单词图片");
			map.put("progressBar1", "20");
			map.put("current", "2MB");
			map.put("length", "12MB");
			map.put("button1", "Downaloding");
			listData.add(map);
			
			map = new HashMap<String, String>();
			map.put("name", "单词读音（英）");
			map.put("progressBar1", "90");
			map.put("current", "12MB");
			map.put("length", "20MB");
			map.put("button1", "Stop");
			listData.add(map);
		
		return listData;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.download, menu);
		return true;
	}

	private void initTitle() {
		Button back = (Button) findViewById(R.id.menuButton);
		back.setBackgroundResource(R.drawable.backbutton);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				navigateUp();
			}
		});
		Button share = (Button) findViewById(R.id.shareButton);
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/*");// intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "好友推荐");
				intent.putExtra(Intent.EXTRA_TEXT, "雅思通(IELTS PASS)分享：");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享"));
			}
		});
		TextView titleText = (TextView) findViewById(R.id.titleText);
		titleText.setText("离线包".toUpperCase());
	}

	private void navigateUp() {
		NavUtils.navigateUpTo(this, new Intent(this, LauncherActivity.class));
	}
	
	private class DownloadSimpleAdapter extends SimpleAdapter{
		
		private int mResource;
		private List<? extends Map<String, ?>> mData;
		private String[] mFrom;
		private int[] mTo;
		private LayoutInflater mInflater;

		public DownloadSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mData = data;
	        mResource = resource;
	        mFrom = from;
	        mTo = to;
	        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
	        return createViewFromResource(position, convertView, parent, mResource);
	    }

	    private View createViewFromResource(int position, View convertView,
	            ViewGroup parent, int resource) {
	        View v;
	        if (convertView == null) {
	            v = mInflater.inflate(resource, parent, false);
	        } else {
	            v = convertView;
	        }

	        bindView(position, v);

	        return v;
	    }
	    private void bindView(int position, View view) {
	        final Map dataSet = mData.get(position);
	        if (dataSet == null) {
	            return;
	        }

	        final ViewBinder binder = getViewBinder();
	        final String[] from = mFrom;
	        final int[] to = mTo;
	        final int count = to.length;

	        for (int i = 0; i < count; i++) {
	            final View v = view.findViewById(to[i]);
	            if (v != null) {
	                final Object data = dataSet.get(from[i]);
	                String text = data == null ? "" : data.toString();
	                if (text == null) {
	                    text = "";
	                }

	                boolean bound = false;
	                if (binder != null) {
	                    bound = binder.setViewValue(v, data, text);
	                }

	                if (!bound) {
	                    if (v instanceof Checkable) {
	                        if (data instanceof Boolean) {
	                            ((Checkable) v).setChecked((Boolean) data);
	                        } else if (v instanceof TextView) {
	                            // Note: keep the instanceof TextView check at the bottom of these
	                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
	                            setViewText((TextView) v, text);
	                        } else {
	                            throw new IllegalStateException(v.getClass().getName() +
	                                    " should be bound to a Boolean, not a " +
	                                    (data == null ? "<unknown type>" : data.getClass()));
	                        }
	                    } else if (v instanceof Button) {
	                    	Button button  = (Button)v;
	                    	if("Stop".equals(text)){
	                    		button.setBackgroundResource(R.drawable.playbutton);
	                    	} else if ("Downaloding".equals(text)){
	                    		button.setBackgroundResource(R.drawable.pausebutton);
	                    	}
	                    } else if (v instanceof TextView) {
	                        // Note: keep the instanceof TextView check at the bottom of these
	                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
	                        setViewText((TextView) v, text);
	                    } else if (v instanceof ImageView) {
	                        if (data instanceof Integer) {
	                            setViewImage((ImageView) v, (Integer) data);                            
	                        } else {
	                            setViewImage((ImageView) v, text);
	                        }
	                    } else if (v instanceof ProgressBar) {
	                    	ProgressBar progressBar = ((ProgressBar) v);
	                    	progressBar.setProgress(Integer.parseInt(text));
	                    } else {
	                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
	                                " view that can be bounds by this SimpleAdapter");
	                    }
	                }
	            }
	        }
	    }
	}
}
