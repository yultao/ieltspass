package tt.lab.android.ieltspass.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import tt.lab.android.ieltspass.Constants;
import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.data.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DownloadActivity extends Activity {
	private static final int DOWNLOAD_INCOMPLETE = 0;
	private static final int DOWNLOAD_COMPLETE = 1;
	private static final int UNZIP_COMPLETE = 2;

	public static final String TAG = DownloadActivity.class.getName();
	
	//private static Map<Integer, DownloadAsyncTask> downloadTasks = new HashMap<Integer, DownloadAsyncTask>();
	private SimpleAdapter simpleAdapter;
	private ListView listView;
	private List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		initTitle();
		initListView();
	}

	private void initListView() {
		listView = (ListView) findViewById(R.id.listView1);
		listData = getListData();
		simpleAdapter = new DownloadSimpleAdapter(this, listData, R.layout.activity_download_vlist, new String[] {
				"name", "progressBar1", "current", "length", "button1" }, new int[] { R.id.name, R.id.progressBar1,
				R.id.current, R.id.length, R.id.button1 });
		listView.setAdapter(simpleAdapter);

	}

	private List<Map<String, String>> getListData() {

		List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
		List<String> downloads = Utilities.readAsset("download");
		for (int i=0;i<downloads.size();i++) {
			String download = downloads.get(i);
			try {
				if (download.trim().length() != 0) {
					download = download.trim();
					// 剑桥雅思9-听力 22180042 IELTSPASS_Listening_Audios_9.zip
					String[] split = download.split("\t");
					String name = split[0];
					int max = Integer.parseInt(split[1]);
					String filename = split[2];

					Map<String, String> map = new HashMap<String, String>();

					// 读取各条下载和解压情况。Simple way, to be refactored.
					int current = 0;
					int state = 0;// 默认未下载完
					String tFile = Settings.getDownloadPathStatic() + "/" + filename + ".t";

					if (Utilities.isFileExist(tFile)) {// 曾经下载过
						List<String> readFile = Utilities.readFile(tFile);
						if (readFile.size() > 0) {
							current = Integer.parseInt(readFile.get(0));
							if (current == -1) {
								state = UNZIP_COMPLETE;// 已解压
								current = max;
							} else if (current == max) {
								state = DOWNLOAD_COMPLETE;// 下载完，未解压（极端情况下，或者压缩包有问题解压失败）
							}
						}
					}

					// 以下6行用于ui显示
					map.put("name", name);
					map.put("progressBar1", String.valueOf(current));// 已下载字节数
					map.put("current", Utilities.formatFizeSize(current));// 已下载字节数
					map.put("length", Utilities.formatFizeSize(max));
					map.put("button1", String.valueOf(state));

					// 以下3行用于控制
					//if(downloadTasks.containsKey(i)){
						//map.put("startstop", String.valueOf(true));
					//} else {
						map.put("startstop", String.valueOf(false));
					//}
					
					map.put("url", Constants.DOWNLOAD_URL + "/" + filename);
					map.put("maxbyte", String.valueOf(max));

					listData.add(map);
				}
			} catch (Exception e) {
				Logger.e(TAG, "getListData E: " + e);
			}
		}
		return listData;
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.i(TAG, "onDestroy");
		stopAll();
	}

	private class DownloadSimpleAdapter extends SimpleAdapter {

		private int mResource;
		private List<Map<String, String>> mData;
		private String[] mFrom;
		private int[] mTo;
		private LayoutInflater mInflater;
		private List<ProgressBar> progressBars = new ArrayList<ProgressBar>();
		private List<TextView> currents = new ArrayList<TextView>();
		private List<TextView> lengths = new ArrayList<TextView>();

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

		private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
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

			// 遍历一条中的所有组件
			for (int i = 0; i < count; i++) {
				final View currentView = view.findViewById(to[i]);
				if (currentView != null) {
					final String data = dataSet.get(from[i]);// Map<1,2> 2
					String text = data == null ? "" : data.toString();
					if (text == null) {
						text = "";
					}

					boolean bound = false;
					if (binder != null) {
						bound = binder.setViewValue(currentView, data, text);
					}

					if (!bound) {

						if (currentView instanceof Button) {// Button
							final Button button = (Button) currentView;
							final Map<String, String> map = mData.get(position);
							final String strurl = map.get("url");
							final int maxbyte = Integer.parseInt(map.get("maxbyte"));

							// 控制按钮图标
							if (Integer.valueOf(text) == DOWNLOAD_INCOMPLETE) {
								if (isStart(position)) {
									button.setBackgroundResource(R.drawable.pausebutton);
								} else {
									button.setBackgroundResource(R.drawable.playbutton);
								}
								button.setEnabled(true);
							} else if (Integer.valueOf(text) == DOWNLOAD_COMPLETE) {
								button.setBackgroundResource(R.drawable.unzipbutton);
								button.setEnabled(true);
							} else if (Integer.valueOf(text) == UNZIP_COMPLETE) {
								button.setBackgroundResource(R.drawable.complete);
								button.setEnabled(false);
							}
							button.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									boolean start = isStart(position);
									//Logger.i(TAG, "preparing to download: "+map+", "+position+", "+start);
									// 控制开始停止
									if (start) {
										setStart(position, false);
										button.setBackgroundResource(R.drawable.playbutton);
									} else {
										setStart(position, true);
										button.setBackgroundResource(R.drawable.pausebutton);
										int startbyte = Integer.parseInt(map.get("progressBar1"));
										int state = Integer.parseInt(map.get("button1"));
										
										ProgressBar progressBar = progressBars.get(position);
										TextView currentTextView = currents.get(position);
										TextView lengthTextView = lengths.get(position);
										DownloadAsyncTask downloadTask = new DownloadAsyncTask(position, progressBar, currentTextView, lengthTextView,
												button, strurl, startbyte, maxbyte,state);
										//downloadTasks.put(position, downloadTask);
										downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

									}
								}
							});

						} else if (currentView instanceof ImageView) {// ImageView
							setViewImage((ImageView) currentView, text);
						} else if (currentView instanceof ProgressBar) {// ProgressBar
							Map<String, String> map = mData.get(position);
							ProgressBar progressBar = ((ProgressBar) currentView);
							progressBar.setMax(Integer.parseInt(map.get("maxbyte")));
							progressBar.setProgress(Integer.parseInt(text));
							progressBars.add(progressBar);
						} else if (currentView instanceof TextView) {// TextView
							TextView textView = (TextView) currentView;
							if (textView.getId() == R.id.current) {
								Map<String, String> map = mData.get(position);
								int parseInt = Integer.parseInt(map.get("button1"));
								if (parseInt == UNZIP_COMPLETE) {
									text = "安装完成";
								}
								currents.add(textView);
							} else if (textView.getId() == R.id.length) {
								lengths.add(textView);
							}
							setViewText(textView, text);
						} else {
							throw new IllegalStateException(currentView.getClass().getName() + " is not a "
									+ " view that can be bounds by this SimpleAdapter");
						}
					}// end if (!bound)
				}
			}
		}
	}

	private int getState(int position) {
		return Integer.valueOf(listData.get(position).get("button1"));
	}

	private void setState(int position, int state) {
		listData.get(position).put("button1", String.valueOf(state));
	}

	private boolean isStart(int position) {
		return Boolean.valueOf(listData.get(position).get("startstop"));
	}

	private void setStart(int position, boolean start) {
		listData.get(position).put("startstop", String.valueOf(start));
	}

	private void stopAll() {
		for (Map<String, String> m : listData) {
			m.put("startstop", "false");
		}
	}

	private void setProgress(int position, int progress) {
		listData.get(position).put("progressBar1", String.valueOf(progress));
	}

	private class DownloadAsyncTask extends AsyncTask<Integer, Integer, String> {

		private ProgressBar progressBar;
		private TextView currentTextView;
		private TextView lengthTextView;
		private Button button;
		private String strurl;
		private int startbyte;
		private int max;
		private int position;
		private int state;
		private String zipFileName;

		private String downloadPath = Settings.getDownloadPathStatic();
		private String zipAbsFileName;
		private String infoAbsFileName;

		public DownloadAsyncTask(int position, ProgressBar progressBar, TextView current, TextView length,
				Button button, String strurl, int startbyte, int max, int state) {
			this.position = position;
			this.progressBar = progressBar;
			this.currentTextView = current;
			this.lengthTextView = length;
			this.button = button;
			this.strurl = strurl;
			this.startbyte = startbyte;
			this.max = max;
			this.state = state;
			zipFileName = strurl.substring(strurl.lastIndexOf("/") + 1);
			zipAbsFileName = downloadPath + "/" + zipFileName;
			infoAbsFileName = zipAbsFileName + ".t";
			
		}

		@Override
		protected String doInBackground(Integer... arg0) {
			Logger.i(TAG, "doInBackground: start: " + startbyte + ", max: " + max);
			// download
			try {
				if (state == DOWNLOAD_INCOMPLETE) {

					URL url = new URL(strurl);
					URLConnection openConnection = url.openConnection();
					openConnection.setRequestProperty("RANGE", "bytes=" + startbyte + "-");// 0-1023, 1024-...
					InputStream is = openConnection.getInputStream();
					Utilities.ensurePath(downloadPath);

					String tmpfilename = zipAbsFileName + ".d";

					RandomAccessFile randomAccessFile = new RandomAccessFile(tmpfilename, "rw");
					randomAccessFile.seek(startbyte);
					byte[] buffer = new byte[4096];
					int len;
					int readbyte = 0;
					int progress = 0;
					while ((len = is.read(buffer)) != -1 && isStart(position)) {
						readbyte += len;
						randomAccessFile.write(buffer, 0, len);
						progress = startbyte + readbyte;

						publishProgress(progress);// 保证界面中最新
						setProgress(position, progress);// 保证内存中最新

						//Utilities.writeFile(infoAbsFileName, String.valueOf(progress));// 保证外存中最新
					}
					if(progress!=0){
						Utilities.writeFile(infoAbsFileName, String.valueOf(progress));// 保证外存中最新
					}

					is.close();
					randomAccessFile.close();
					if (progress == max) {// 重命名
						File tmp = new File(tmpfilename);
						tmp.renameTo(new File(zipAbsFileName));
						state = DOWNLOAD_COMPLETE;
					}
				}
			} catch (Exception e) {
				Logger.e(TAG, "doInBackground downloading E: " + e);
				e.printStackTrace();
			}

			// unzip
			if (state == DOWNLOAD_COMPLETE) {
				publishProgress(0);// 下载完成
				try {
					ZipFile zfile = new ZipFile(zipAbsFileName);
					String folderPath = Settings.getStorageStatic();
					Enumeration zList = zfile.entries();
					ZipEntry zipEntry = null;
					byte[] buf = new byte[4096];

					int size = 0;
					while (zList.hasMoreElements()) {
						zipEntry = (ZipEntry) zList.nextElement();
						if (!zipEntry.isDirectory())
							size++;
					}

					zList = zfile.entries();
					int curr = 0;
					while (zList.hasMoreElements()) {// 解压过程不可暂停（暂定），但有可能强制退出或断电，这样下次需要重新再来。
						zipEntry = (ZipEntry) zList.nextElement();
						if (zipEntry.isDirectory()) {
							String dirstr = folderPath + "/" + zipEntry.getName();
							Utilities.ensurePath(dirstr);
						} else {
							OutputStream os = new FileOutputStream(folderPath + "/" + zipEntry.getName());
							InputStream is = zfile.getInputStream(zipEntry);
							int readLen = 0;
							while ((readLen = is.read(buf)) != -1) {
								os.write(buf, 0, readLen);
							}
							is.close();
							os.close();
							publishProgress(++curr, size);
						}

					}
					zfile.close();

					// 解压完全成功，修改标志文件，删除压缩包
					Utilities.writeFile(infoAbsFileName, String.valueOf(-1));// -1表示解压完成
					new File(zipAbsFileName).delete();
					state = UNZIP_COMPLETE;
				} catch (Exception e) {
					Logger.e(TAG, "doInBackground unziping E: " + e);
				}
			}

			return "DONE.";
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(String result) {
			setStart(position, false);
			//downloadTasks.remove(position);
			if (state == DOWNLOAD_INCOMPLETE) {
				button.setBackgroundResource(R.drawable.playbutton);
			} else if (state == DOWNLOAD_COMPLETE) {
				button.setBackgroundResource(R.drawable.unzip);
			} else if (state == UNZIP_COMPLETE) {
				button.setBackgroundResource(R.drawable.complete);
				currentTextView.setText("安装成功");
			}

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progress = values[0];
			progressBar.setProgress(progress);
			if (values.length == 2) {
				max = values[1];
				progressBar.setMax(max);
			}

			// 中间状态只有下载完成与否
			if (state == DOWNLOAD_INCOMPLETE) {
				currentTextView.setText(Utilities.formatFizeSize(progress));
			} else if (state == DOWNLOAD_COMPLETE) {
				button.setBackgroundResource(R.drawable.unzip);
				button.setEnabled(false);// 解压时候不能停止
				currentTextView.setText("正在安装... (" + String.valueOf(progress * 100 / max) + "%)");
			}
		}
	}
}
