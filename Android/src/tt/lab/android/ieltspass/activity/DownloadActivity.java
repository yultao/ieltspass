package tt.lab.android.ieltspass.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.R.layout;
import tt.lab.android.ieltspass.R.menu;
import tt.lab.android.ieltspass.data.Settings;
import tt.lab.android.ieltspass.model.Word;
import android.os.AsyncTask;
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
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

public class DownloadActivity extends Activity {
	public static final String TAG = DownloadActivity.class.getName();
	private SimpleAdapter simpleAdapter;
	private ListView listView;
	private Settings settings;
	private List<Map<String, String>> listData = new ArrayList<Map<String, String>>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = Settings.getInstance(this);
		setContentView(R.layout.activity_download);
		initTitle();
		initListView();
	}

	private void initListView() {
		listView = (ListView) findViewById(R.id.listView1);
		listData = getListData();
		simpleAdapter = new DownloadSimpleAdapter(this, listData, R.layout.activity_download_vlist, new String[] {
				"name", "progressBar1", "current", "length", "button1" }, new int[] { R.id.name, R.id.progressBar1, R.id.current, R.id.length,R.id.button1});
		listView.setAdapter(simpleAdapter);
		
		
	}

	private List<Map<String, String>> getListData() {

		List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

			Map<String, String> map = new HashMap<String, String>();
			
			int current = 189102;
			int max = 1189102;
			map.put("name", "单词图片");
			map.put("progressBar1", String.valueOf(current));//已下载字节数
			map.put("current", Utilities.formatFizeSize(current));
			map.put("length", Utilities.formatFizeSize(1189102));
			map.put("button1", "true");
			
			map.put("url", "http://ieltspass-ieltspass.stor.sinaapp.com/cb/6-1-2.mp3");
			map.put("maxbyte", String.valueOf(max));//最大字节数
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
		private List<Map<String, String>> mData;
		private String[] mFrom;
		private int[] mTo;
		private LayoutInflater mInflater;
		private List<ProgressBar> progressBars = new ArrayList<ProgressBar>();
		private List<TextView> currents = new ArrayList<TextView>();
		public DownloadSimpleAdapter(Context context, List<Map<String, String>> data, int resource, String[] from,
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
	    private void bindView(final int position, View view) {
	        final Map<String, String> dataSet = mData.get(position);
	        if (dataSet == null) {
	            return;
	        }

	        final ViewBinder binder = getViewBinder();
	        final String[] from = mFrom;
	        final int[] to = mTo;
	        final int count = to.length;

	        //遍历一条中的所有组件
	        for (int i = 0; i < count; i++) {
	            final View currentView = view.findViewById(to[i]);
	            if (currentView != null) {
	                final String data = dataSet.get(from[i]);//Map<1,2> 2
	                String text = data == null ? "" : data.toString();
	                if (text == null) {
	                    text = "";
	                }

	                boolean bound = false;
	                if (binder != null) {
	                    bound = binder.setViewValue(currentView, data, text);
	                }

	                if (!bound) {
	                    if (currentView instanceof Button) {
	                    	final Button button  = (Button)currentView;
	                    	if(Boolean.valueOf(text)){
	                    		button.setBackgroundResource(R.drawable.playbutton);
	                    	} else {
	                    		button.setBackgroundResource(R.drawable.pausebutton);
	                    	}
	                    	button.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if(isPause(position)){
										Map<String, String> map = mData.get(position);
										String strurl = map.get("url");
										int startbyte = Integer.parseInt(map.get("progressBar1"));
										int maxbyte = Integer.parseInt(map.get("maxbyte"));
										//Toast.makeText(DownloadActivity.this, "strurl: "+strurl+", startbyte: "+startbyte, Toast.LENGTH_SHORT).show();
										ProgressBar progressBar = progressBars.get(position);
										TextView textView = currents.get(position);
										new DownloadAsyncTask(position, progressBar,textView,button, strurl, startbyte, maxbyte).execute();
										setPause(position, false);
										button.setBackgroundResource(R.drawable.pausebutton);
									} else {
										setPause(position, true);
										button.setBackgroundResource(R.drawable.playbutton);
									}
								}
							});
	                    } else if (currentView instanceof ImageView) {
	                       setViewImage((ImageView) currentView, text);
	                    } else if (currentView instanceof ProgressBar) {
	                    	Map<String, String> map = mData.get(position);
	                    	ProgressBar progressBar = ((ProgressBar) currentView);
	                    	progressBar.setMax(Integer.parseInt(map.get("maxbyte")));
	                    	progressBar.setProgress(Integer.parseInt(text));
	                    	progressBars.add(progressBar);
	                    } else if (currentView instanceof TextView) {
	                    	TextView textView = (TextView) currentView;
	                    	if(textView.getId()==R.id.current){
	                    		currents.add(textView);
	                    	}
	                        setViewText(textView, text);
	                    } else {
	                        throw new IllegalStateException(currentView.getClass().getName() + " is not a " +
	                                " view that can be bounds by this SimpleAdapter");
	                    }
	                }
	            }
	        }
	    }
	}
	
	
	private boolean isPause(int position){
		return Boolean.valueOf(listData.get(position).get("button1"));
	}
	private void setPause(int position, boolean pause){
		listData.get(position).put("button1", String.valueOf(pause));
	}
	private class DownloadAsyncTask extends AsyncTask<Integer, Integer, String> {
		private ProgressBar progressBar;
		private TextView current;
		private Button button;
		private String strurl;
		private int startbyte;
		private int currentByte;
		private int max;
		private int position;
		public DownloadAsyncTask(int position, ProgressBar progressBar, TextView current, Button button, String strurl, int startbyte, int max) {
			this.position = position;
			this.progressBar = progressBar;
			this.current = current;
			this.button = button;
			this.strurl = strurl;
			this.startbyte = startbyte;
			this.max = max;
		}

		@Override
		protected String doInBackground(Integer... arg0) {
			try {
				URL url = new URL(strurl);
				URLConnection openConnection = url.openConnection();
				openConnection.setRequestProperty("RANGE","bytes="+startbyte+"-");//0-1023, 1024-...

				InputStream is = openConnection.getInputStream();
				String downloadPath = settings.getDownloadPath();
				Utilities.ensurePath(downloadPath);
				
				String name = strurl.substring(strurl.lastIndexOf("/") + 1);
				String filename = downloadPath + "/" + name;
				String tmpfilename = filename + ".d";
				
				RandomAccessFile randomAccessFile = new RandomAccessFile(tmpfilename,"rw");   
				randomAccessFile.seek(startbyte);
				byte[] buffer = new byte[1024];
				int len;
				int readbyte = 0;
				while ((len = is.read(buffer)) != -1 && !isPause(position)) {
					readbyte += len;
					randomAccessFile.write(buffer, 0, len);
					currentByte = startbyte+readbyte;
					publishProgress(currentByte);
					Utilities.writeFile(downloadPath+"/"+name+".t",readbyte+"/"+currentByte);
					try{
						Thread.sleep(10);
					} catch (Exception e){
						
					}
				}
				is.close();
				randomAccessFile.close();
				if(currentByte == max){//重命名
					File tmp = new File(tmpfilename);
					tmp.renameTo(new File(filename));
				}
			} catch (Exception e) {
				Logger.e(TAG, "doInBackground E: " + e);
				e.printStackTrace();
			}
			return "DONE.";
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(String result) {
			button.setBackgroundResource(R.drawable.playbutton);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int value = values[0];
			progressBar.setProgress(value);
			current.setText(Utilities.formatFizeSize(value));
		}
	}
}
