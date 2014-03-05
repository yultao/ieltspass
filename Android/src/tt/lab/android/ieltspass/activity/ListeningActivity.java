package tt.lab.android.ieltspass.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.data.Constants;
import tt.lab.android.ieltspass.data.Database;
import tt.lab.android.ieltspass.data.Logger;
import tt.lab.android.ieltspass.data.Utilities;
import tt.lab.android.ieltspass.fragment.ListeningFragmentAnswers;
import tt.lab.android.ieltspass.fragment.ListeningFragmentLyrics;
import tt.lab.android.ieltspass.fragment.ListeningFragmentQuestions;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ListeningActivity extends FragmentActivity {
	private static final String TAG = ListeningActivity.class.getName();
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private Button btnPlayStop;
	private MediaPlayer player;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	private Handler handler = new Handler();
	private SeekBar seekBar;
	private TextView currentPosition, percentage, duration;
	private File file;
	private String title;
	private String lyrics;
	private String audio;
	private String questions;
	private String answers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listening);
		initParameters();
		initPager();
		initTitle();
		initAudio();
		initControls();
		initPlayer();
		initFragement();

	}

	private void initParameters() {
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		title = bundle.getString("title");
		lyrics = bundle.getString("lyrics");
		audio = bundle.getString("audio");
		questions = bundle.getString("questions");
		answers = bundle.getString("answers");
	}

	private void initAudio() {
		String name = Constants.SD_PATH + "/" + Constants.AUDIO_PATH + "/" + audio;
		file = new File(name);
	}

	private void initPager() {
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	private void initTitle() {
		Button back = (Button) findViewById(R.id.menuButton);
		// back.setBackground(Resources.getSystem().getDrawable(R.drawable.back));
		back.setBackgroundDrawable(getResources().getDrawable(R.drawable.back));
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
		titleText.setText(title.toUpperCase());
	}

	private void initFragement() {
		ListeningFragmentQuestions fragmentQuestions = new ListeningFragmentQuestions();
		fragmentQuestions.setQuestions(questions);
		pagerItemList.add(fragmentQuestions);

		ListeningFragmentLyrics fragmentLyrics = new ListeningFragmentLyrics();
//		Bundle args = new Bundle();
//		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, 1);
//		fragmentLyrics.setArguments(args);
		fragmentLyrics.setPlayer(player, seekBar, lyrics);
		pagerItemList.add(fragmentLyrics);

		ListeningFragmentAnswers fragmentAnswers = new ListeningFragmentAnswers();
		fragmentAnswers.setAnswers(answers);
		pagerItemList.add(fragmentAnswers);

	}

	private void enablePlayStopButton(boolean enabled) {
		btnPlayStop.setEnabled(enabled);
		int a = 0;
		if(enabled){
			if(player.isPlaying()){
				a = R.drawable.pause_enabled;
			} else {
				a = R.drawable.play_enabled;
			}
		} else {
			a = R.drawable.play;
		}
		
		btnPlayStop.setBackgroundDrawable(getResources().getDrawable(a));
	}

	private void initControls() {
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setEnabled(false);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				// fromUser判断是用户改变的滑块的值
				try {
					// Logger.i(TAG, "onProgressChanged: fromUser: " + progress +", "+fromUser);
					if (fromUser == true) {
						player.seekTo(progress);
					}
					currentPosition.setText(Utilities.formatTime(progress));
				} catch (Exception e) {
					Logger.i(TAG, "onProgressChanged: E: " + e.getMessage());
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		btnPlayStop = (Button) findViewById(R.id.button1);
		enablePlayStopButton(false);
		btnPlayStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (player.isPlaying()) {
					pause();
				} else {
					start();
				}
			}
		});

		currentPosition = (TextView) findViewById(R.id.currentPosition);
		percentage = (TextView) findViewById(R.id.percentage);
		duration = (TextView) findViewById(R.id.duration);
	}

	private void initPlayer() {
		if (file.exists()) {
			// AudioManager am = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
			// am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			player = new MediaPlayer();
			player.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					Logger.i(TAG, "onPrepared");
					start();
					enablePlayStopButton(true);
					seekBar.setEnabled(true);
					percentage.setText("Prepared");
					//Logger.i(TAG, "onPrepared " + 4 + ", " + player.getDuration());
					duration.setText(Utilities.formatTime(player.getDuration()));
					Logger.i(TAG, "onPrepared " + 5 + ", " +  player.getDuration()+" = "+Utilities.formatTime(player.getDuration()));
					seekBar.setMax(player.getDuration());
					
				}
			});

			player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
				int i = 0;

				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					int a = seekBar.getMax() * percent / 100;
					//Logger.i(TAG, "onBufferingUpdate: " + percent + "% " + a + ", " + seekBar.getMax());
					
					if (percent == 100) {
						percentage.setText("");
					} else {
						percentage.setText("Loading " + percent + "% ");
					}

					seekBar.setSecondaryProgress(a);
					// player.addTimedTextSource(fd, mimeType);

				}
			});
			/* 当MediaPlayer.OnCompletionLister会运行的Listener */
			player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				// @Override
				public void onCompletion(MediaPlayer arg0) {
					seekBar.setProgress(0);
					pause();
				}
			});

			/* 当MediaPlayer.OnErrorListener会运行的Listener */
			player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					Logger.i(TAG, "onError " + arg1+", "+arg2);
					release();
					return false;
				}
			});

			try {
				String strurl  = "http://taog.ueuo.com/C9T1S2.mp3";
				player.reset();
				Logger.i(TAG, "initPlayer " + 2+", "+strurl);
				player.setDataSource(file.getAbsolutePath());
				//player.setDataSource(strurl);
				Logger.i(TAG, "initPlayer " + 3);
				player.prepareAsync();
				Logger.i(TAG, "initPlayer " + 4);
				percentage.setText("Preparing");
				Logger.i(TAG, "initPlayer " + 5);
				new DownloadAsyncTask(seekBar,strurl).execute();
			} catch (Exception e) {
				Logger.i(TAG, "initPlayer: E " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void refreshButtonText() {
		btnPlayStop.setBackgroundDrawable(getResources().getDrawable(
				player.isPlaying() ? R.drawable.pause_enabled : R.drawable.play_enabled));
	}

	private void navigateUp() {
		release();
		NavUtils.navigateUpTo(this, new Intent(this, LaunchActivity.class));
	}

	private Runnable updateProgressThread = new Runnable() {
		public void run() {
			// 获得歌曲现在播放位置并设置成播放进度条的值
			try {
				int cp = player.getCurrentPosition();
				seekBar.setProgress(cp);
				handler.postDelayed(updateProgressThread, 500);// No need to update UI so frequently, 500 is enough.
			} catch (Exception e) {
				Logger.i(TAG, "updateProgressThread: " + e.getMessage());
			}

		}
	};
	private Runnable downloadThread = new Runnable() {
		public void run() {

			try {

			} catch (Exception e) {
				Logger.i(TAG, "downloadThread: " + e.getMessage());
			}

		}
	};

	private void start() {
		if (player != null) {
			// Logger.i(TAG, "start I");
			try {
				// player.seekTo(seekBar.getProgress());
				Logger.i(TAG, "start " + 6);
				player.start();
				handler.post(updateProgressThread);

				currentPosition.setText(Utilities.formatTime(player.getCurrentPosition()));
				refreshButtonText();

			} catch (Exception e) {
				Logger.i(TAG, "start: " + e.getMessage());
				e.printStackTrace();
			}
		}
		// Logger.i(TAG, "start O");
	}

	private void pause() {
		// Logger.i(TAG, "pause I ");
		try {
			if (player != null) {
				/* 发生错误时也解除资源与MediaPlayer的赋值 */
				player.pause();
				// tv.setText("播放发生异常!");
				handler.removeCallbacks(updateProgressThread);
				refreshButtonText();
			}

		} catch (Exception e) {
			Logger.i(TAG, "pause: E: " + e.getMessage());
			e.printStackTrace();
		}
		// Logger.i(TAG, "pause O");
	}

	private void release() {
		// Logger.i(TAG, "release I");
		try {
			if (player != null) {
				// Logger.i(TAG, "release: 0: ");
				refreshButtonText();
				// Logger.i(TAG, "release: 1: ");
				player.release();
				// Logger.i(TAG, "release: 2: "+updateProgressThread);

				// tv.setText("播放发生异常!");
				handler.removeCallbacks(updateProgressThread);
				// Logger.i(TAG, "release: 3: ");
			}
		} catch (Exception e) {
			Logger.i(TAG, "release: E: " + e.getMessage());
			e.printStackTrace();
		}
		// Logger.i(TAG, "release O");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			release();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.listening, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = pagerItemList.get(position);
			return fragment;
		}

		@Override
		public int getCount() {
			return pagerItemList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_listening_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_listening_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_listening_section3).toUpperCase(l);
			}
			return null;
		}
	}

	public class DownloadAsyncTask extends AsyncTask<Integer, Integer, String> {
		private SeekBar seekBar;
		private String strurl;

		public DownloadAsyncTask(SeekBar seekBar, String strurl) {
			this.seekBar = seekBar;
			this.strurl = strurl;
		}

		@Override
		protected String doInBackground(Integer... arg0) {
			try {
				Logger.i(TAG, "doInBackground "+arg0);
				URL url = new URL(strurl);
				String name = strurl.substring(strurl.lastIndexOf("/")+1);
				Logger.i(TAG, "doInBackground name "+name);
				URLConnection openConnection = url.openConnection();

				InputStream is = openConnection.getInputStream();
				int available = is.available();
				//Logger.i(TAG, "doInBackground "+21+", "+available);
				String filename=Constants.SD_PATH+"/"+Constants.AUDIO_PATH+"/"+name;
				Logger.i(TAG, "doInBackground filename "+filename);
				String tmpfilename = filename+".d";
				Logger.i(TAG, "doInBackground tmpfilename "+tmpfilename);
				File tmp = new File(tmpfilename);
				OutputStream os = new FileOutputStream(tmp);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = is.read(buffer)) != -1) {
					//Logger.i(TAG, "doInBackground "+3+", "+len);
					os.write(buffer, 0, len);
				}
				File file = new File(filename);
				boolean renameTo = tmp.renameTo(file);
				Logger.i(TAG, "doInBackground renameTo "+renameTo);
				is.close();
				os.close();
//				for (int i = 0; i < 100; i++) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					publishProgress(i);
//				}
			} catch (Exception e) {
				Logger.i(TAG, "doInBackground E: "+e.getMessage());
				e.printStackTrace();
			}
			return "DONE.";
		}

		@Override
		protected void onPreExecute() {
			Logger.i(TAG, "onPreExecute");
		}

		@Override
		protected void onPostExecute(String result) {
			Logger.i(TAG, "onPostExecute: " + result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int value = values[0];
			// Logger.i(TAG, "onProgressUpdate: " + value);
			//seekBar.setSecondaryProgress((value * seekBar.getMax() / 100));
		}
	}
}
