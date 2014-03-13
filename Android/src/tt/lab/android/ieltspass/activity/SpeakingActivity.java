package tt.lab.android.ieltspass.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import tt.lab.android.ieltspass.Constants;
import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.fragment.SpeakingFragmentQuestions;
import tt.lab.android.ieltspass.fragment.SpeakingFragmentRecordings;
import tt.lab.android.ieltspass.fragment.SpeakingFragmentScripts;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class SpeakingActivity extends FragmentActivity {

	protected static final String TAG = SpeakingActivity.class.getName();;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
	 * If this becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	private String title;
	private String questions;
	private String name;
	private Button btnPlayStop, btnRecordStop;
	private TextView currentPosition, percentage, duration;
	private MediaPlayer player;
	private MediaRecorder recorder;
	private boolean recording;
	private SeekBar seekBar;
	private Handler handler = new Handler();
	private String currentAudio;

	private long recordingStart;
	private long MAX_RECORDING_LENGTH = 1000+1000 * 60*5;//最长5分钟
	private SpeakingFragmentRecordings fragmentRecordings;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private boolean autoStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speaking);

		initParameters();
		initTitle();
		initButtons();
		initPlayer();
		initPager();
		initFragement();

	}

	private void initParameters() {
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		title = bundle.getString("title");
		questions = bundle.getString("questions");
		name = bundle.getString("name");
	}

	private void initButtons() {
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setEnabled(false);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				try {
					if (fromUser == true) {
						Logger.i(TAG, "onProgressChanged: fromUser: " + progress + ", " + fromUser);
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
		btnPlayStop = (Button) findViewById(R.id.buttonPlay);
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

		btnRecordStop = (Button) findViewById(R.id.buttonRecord);
		btnRecordStop.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Logger.i(TAG, "recording I: " + recording);
				try {
					if (!recording) {// 开始录
						startRecording();
					} else {// 停止
						stopRecording();
					}
				} catch (IOException e) {
					Logger.i(TAG, "recording E: " + e);
					e.printStackTrace();
				}
				Logger.i(TAG, "recording O: " + recording);
			}

			private void startRecording() throws IOException {
				Logger.i(TAG, "recording start");
				recordingStart = System.currentTimeMillis();
				resetPlayerControls();
				String path = Constants.SPEAKING_AUDIO_PATH + "/" + name;
				Utilities.ensurePath(path);
				currentAudio = path + "/" + Utilities.getRecordingFileName() + ".amr";
				recorder = new MediaRecorder();
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				recorder.setOutputFile(currentAudio);
				recorder.prepare();
				recorder.start();
				handler.post(updateDurationThread);
				recording = true;
				
				btnRecordStop.setBackgroundDrawable(getResources().getDrawable(R.drawable.recordpausebutton));
			}

			
		});

		currentPosition = (TextView) findViewById(R.id.currentPosition1);
		percentage = (TextView) findViewById(R.id.percentage1);
		duration = (TextView) findViewById(R.id.duration1);
	}
	
	private void stopRecording() {
		Logger.i(TAG, "recording stop");
		recorder.stop();
		recorder.release();
		recorder = null;
		recording = false;
		resetPlayer(currentAudio, false);
		fragmentRecordings.refreshListView();
		handler.removeCallbacks(updateDurationThread);
		btnRecordStop.setBackgroundDrawable(getResources().getDrawable(R.drawable.recordbutton));
	}
	private void initPlayer() {
		player = new MediaPlayer();
		player.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				Logger.i(TAG, "onPrepared");
				if (autoStart) {// 点击列表项
					start();
				}
				enablePlayStopButton(true);
				seekBar.setEnabled(true);
				percentage.setText("");
				// Logger.i(TAG, "onPrepared " + 4 + ", " + player.getDuration());
				duration.setText(Utilities.formatTime(player.getDuration()));
				Logger.i(
						TAG,
						"onPrepared  getDuration " + player.getDuration() + " = "
								+ Utilities.formatTime(player.getDuration()));
				seekBar.setMax(player.getDuration());
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

		player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				Logger.i(TAG, "onError " + arg1 + ", " + arg2);
				percentage.setText("Error");
				release();
				return false;
			}
		});

	}

	/**
	 * Called when recording starts.
	 */
	private void resetPlayerControls() {
		Logger.i(TAG, "resetPlayerControls I");
		pause();
		btnPlayStop.setEnabled(false);
		refreshButtonText();
		seekBar.setProgress(0);
		seekBar.setEnabled(false);
		currentPosition.setText("00:00");
	}

	public boolean resetPlayer(String url, boolean autoStart) {
		Logger.i(TAG, "resetPlayer I: url: " + url + ", autoStart: " + autoStart);
		if (!recording) {// 不在录音状态

			this.autoStart = autoStart;
			currentAudio = url;
			try {
				player.reset();
				percentage.setText("Setting");
				player.setDataSource(url);
				percentage.setText("Preparing.");
				player.prepareAsync();
				percentage.setText("Preparing..");
			} catch (Exception e) {
				Logger.i(TAG, "resetPlayer: E " + e);
				e.printStackTrace();
			}
		}
		boolean success = !recording;
		Logger.i(TAG, "resetPlayer O: success? " + success);

		return success;
	}

	private void enablePlayStopButton(boolean enabled) {
		btnPlayStop.setEnabled(enabled);
		refreshButtonText();
	}

	private void start() {
		if (player != null) {
			// Logger.i(TAG, "start I");
			try {
				// player.seekTo(seekBar.getProgress());
				player.start();
				handler.post(updateProgressThread);
				currentPosition.setText(Utilities.formatTime(player.getCurrentPosition()));
				refreshButtonText();
			} catch (Exception e) {
				Logger.i(TAG, "start: " + e);
				e.printStackTrace();
			}
		}
		// Logger.i(TAG, "start O");
	}

	private void pause() {
		Logger.i(TAG, "pause I： " + player + ", playing: " + player.isPlaying());
		try {
			if (player != null && player.isPlaying()) {
				/* 发生错误时也解除资源与MediaPlayer的赋值 */
				player.pause();
				// tv.setText("播放发生异常!");
				
			}
			handler.removeCallbacks(updateProgressThread);
			refreshButtonText();
		} catch (Exception e) {
			Logger.i(TAG, "pause: E: " + e);
			e.printStackTrace();
		}
		Logger.i(TAG, "pause O");
	}

	private void release() {
		// Logger.i(TAG, "release I");
		try {
			if (player != null) {
				
				player.release();
				
				// Logger.i(TAG, "release: 3: ");
			}
			refreshButtonText();
			handler.removeCallbacks(updateProgressThread);
		} catch (Exception e) {
			Logger.i(TAG, "release: E: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private Runnable updateProgressThread = new Runnable() {
		public void run() {
			try {
				int cp = player.getCurrentPosition();
				seekBar.setProgress(cp);
				handler.postDelayed(updateProgressThread, 500);// No need to update UI so frequently, 500 is enough.
			} catch (Exception e) {
				Logger.i(TAG, "updateProgressThread: " + e.getMessage());
			}

		}
	};

	private Runnable updateDurationThread = new Runnable() {
		public void run() {
			try {
				int length = (int) (System.currentTimeMillis() - recordingStart);

				duration.setText(Utilities.formatTimeSecond(length));
				handler.postDelayed(updateDurationThread, 500);
				if (length > MAX_RECORDING_LENGTH) {
					stopRecording();
				}
			} catch (Exception e) {
				Logger.i(TAG, "updateDurationThread: " + e.getMessage());
			}

		}
	};

	private void refreshButtonText() {
		int a = 0;
		if (btnPlayStop.isEnabled()) {
			if (player.isPlaying()) {
				a = R.drawable.pausebutton;
			} else {
				a = R.drawable.playbutton;
			}
		} else {
			if (player != null && player.isPlaying()) {
				a = R.drawable.pause;
			} else {
				a = R.drawable.play;
			}
		}
		btnPlayStop.setBackgroundDrawable(getResources().getDrawable(a));
	}

	private void initPager() {
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	private void initTitle() {
		Button back = (Button) findViewById(R.id.menuButton);
		// back.setBackground(Resources.getSystem().getDrawable(R.drawable.back));
		back.setBackgroundDrawable(getResources().getDrawable(R.drawable.backbutton));
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

	private void navigateUp() {
		NavUtils.navigateUpTo(this, new Intent(this, LauncherActivity.class));
	}

	private void initFragement() {

		fragmentRecordings = new SpeakingFragmentRecordings();
		fragmentRecordings.setSpeakingActivity(this);
		fragmentRecordings.setName(name);
		pagerItemList.add(fragmentRecordings);

		SpeakingFragmentQuestions fragmentQuestions = new SpeakingFragmentQuestions();
		fragmentQuestions.setQuestions(questions);
		pagerItemList.add(fragmentQuestions);

		SpeakingFragmentScripts fragmentAnswers = new SpeakingFragmentScripts();
		pagerItemList.add(fragmentAnswers);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.speaking, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			release();
		}
		return super.onKeyDown(keyCode, event);
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
				return "录音".toUpperCase(l);
			case 1:
				return "题目".toUpperCase(l);
			case 2:
				return "讲稿".toUpperCase(l);
			}
			return null;
		}
	}

}
