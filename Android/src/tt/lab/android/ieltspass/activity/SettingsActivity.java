package tt.lab.android.ieltspass.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import tt.lab.android.ieltspass.Constants;
import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.R.layout;
import tt.lab.android.ieltspass.R.menu;
import tt.lab.android.ieltspass.activity.ReadingActivity.SectionsPagerAdapter;
import tt.lab.android.ieltspass.data.Settings;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	private TextView textView3;
	private Settings settings;
	private String[] storagePaths;
	private int storageIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		settings = Settings.getInstance(this);
		initTitle();
		
		
		initNetwork();
		initStorage();
	}

	private void initTitle() {
		Button back = (Button) findViewById(R.id.menuButton);
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
		titleText.setText("设置");

	}

	private void initNetwork() {
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		RadioButton radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
		
		RadioButton radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
		RadioButton radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
		final int id1 = radioButton1.getId();
		final int id2 = radioButton2.getId();
		final int id3 = radioButton3.getId();
		if (settings.isNetwordForbidden()) {
			radioButton1.setChecked(true);
		} else if (settings.isOnlyUseWifi()) {
			radioButton2.setChecked(true);
		} else if (settings.isWifiAndMobile()) {
			radioButton3.setChecked(true);
		}
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int radioButtonId = arg0.getCheckedRadioButtonId();
				if(radioButtonId==id1){
					settings.write(Settings.SETTINGS_KEY_INTERNET, Settings.SETTINGS_VALUE_INTERNET_NO);
					settings.setInternet(Settings.SETTINGS_VALUE_INTERNET_NO);
				} else if (radioButtonId==id2){
					settings.write(Settings.SETTINGS_KEY_INTERNET, Settings.SETTINGS_VALUE_INTERNET_WIFI);
					settings.setInternet(Settings.SETTINGS_VALUE_INTERNET_WIFI);
				} else if (radioButtonId==id3){
					settings.write(Settings.SETTINGS_KEY_INTERNET, Settings.SETTINGS_VALUE_INTERNET_MOBILE);
					settings.setInternet(Settings.SETTINGS_VALUE_INTERNET_MOBILE);
				}
			}
		});
	}

	private void initStorage() {
		textView3 = (TextView) findViewById(R.id.textView3);
		storagePaths = listExternalDirs();
		
		for (int i = 0; i < storagePaths.length; i++) {
			Logger.i("initStorage", "storagePath " + storagePaths[i]);
			if (storagePaths[i].equals(settings.getStorage())) {
				storageIndex = i;
			}
		}
		textView3.setText(storagePaths[storageIndex]);
		
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				show();
			}
		});
	}

	private void sd() {

//		Logger.i("sd", "root:　" + Environment.getRootDirectory().getAbsolutePath());
//		Logger.i("sd", "data:　" + Environment.getDataDirectory().getAbsolutePath());
//		Logger.i("sd", "DownloadCache " + Environment.getDownloadCacheDirectory().getAbsolutePath());
//		Logger.i("sd", "ext " + Environment.getExternalStorageDirectory());
//		Logger.i("sd", "DIRECTORY_MUSIC " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
//		Logger.i("sd",
//				"DIRECTORY_PODCASTS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS));
//		Logger.i("sd",
//				"DIRECTORY_RINGTONES " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES));
//		Logger.i(
//				"sd",
//				"DIRECTORY_NOTIFICATIONS "
//						+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS));
//		Logger.i("sd",
//				"DIRECTORY_ALARMS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS));
//		Logger.i("sd",
//				"DIRECTORY_PICTURES " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
//		Logger.i("sd",
//				"DIRECTORY_MOVIES " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
//		Logger.i("sd",
//				"DIRECTORY_DOWNLOADS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
//		Logger.i("sd", "DIRECTORY_DCIM " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
		Logger.i("sd", "getFilesDir " + getFilesDir());
		
		File filesDir = getFilesDir();
		File[] listFiles = filesDir.listFiles();
		Logger.i("sd", "filesDir.size " + listFiles.length);
		for (File file : listFiles) {
			Logger.i("sd", "file " + file.getAbsolutePath());
		}

		
	}

	private void show() {
		Builder builder = new Builder(this);
		// builder.setMessage("确定删除");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		builder.setSingleChoiceItems(storagePaths, storageIndex, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				textView3.setText(storagePaths[which]);
				settings.write(Settings.SETTINGS_KEY_STORAGE, storagePaths[which]);
				settings.setStorage(storagePaths[which]);
			}
		});

		builder.create().show();
	}

	private String[] listExternalDirs() {
		File dir = new File("/storage");
		File[] listFiles = dir.listFiles();
		final String[] listFileNames = new String[listFiles.length];
		for (int i = 0; i < listFileNames.length; i++) {
			listFileNames[i] = listFiles[i].getAbsolutePath();
		}
		return listFileNames;
	}

	private void navigateUp() {
		NavUtils.navigateUpTo(this, new Intent(this, LauncherActivity.class));
	}
}
