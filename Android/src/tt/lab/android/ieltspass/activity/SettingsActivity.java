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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	TextView textView3;
	Settings settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		settings = new Settings(this);
		initTitle();
		sd();
		initNetwork();
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
		if(Constants.SETTINGS_VALUE_INTERNET_NO.equals(Settings.INTERNET)){
			radioButton1.setChecked(true);
		} else if(Constants.SETTINGS_VALUE_INTERNET_WIFI.equals(Settings.INTERNET)){
			radioButton2.setChecked(true);
		} else if(Constants.SETTINGS_VALUE_INTERNET_MOBILE.equals(Settings.INTERNET)){
			radioButton3.setChecked(true);
		}
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int radioButtonId = arg0.getCheckedRadioButtonId();
				switch (radioButtonId) {
				case 0:
					settings.write(Constants.SETTINGS_KEY_INTERNET, Constants.SETTINGS_VALUE_INTERNET_NO);
					break;
				case 1:
					settings.write(Constants.SETTINGS_KEY_INTERNET, Constants.SETTINGS_VALUE_INTERNET_WIFI);
					break;
				case 2:
					settings.write(Constants.SETTINGS_KEY_INTERNET, Constants.SETTINGS_VALUE_INTERNET_MOBILE);
					break;
				}
			}
		});
	}

	private void sd() {
		textView3 = (TextView) findViewById(R.id.textView3);
		textView3.setText(Constants.SD_PATH);
		File rootDirectory = Environment.getRootDirectory();

		Logger.i("sd", "root:　" + Environment.getRootDirectory().getAbsolutePath());
		Logger.i("sd", "data:　" + Environment.getDataDirectory().getAbsolutePath());
		Logger.i("sd", "DownloadCache " + Environment.getDownloadCacheDirectory().getAbsolutePath());
		Logger.i("sd", "ext " + Environment.getExternalStorageDirectory());
		Logger.i("sd", "DIRECTORY_MUSIC " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
		Logger.i("sd",
				"DIRECTORY_PODCASTS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS));
		Logger.i("sd",
				"DIRECTORY_RINGTONES " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES));
		Logger.i(
				"sd",
				"DIRECTORY_NOTIFICATIONS "
						+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS));
		Logger.i("sd",
				"DIRECTORY_ALARMS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS));
		Logger.i("sd",
				"DIRECTORY_PICTURES " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
		Logger.i("sd",
				"DIRECTORY_MOVIES " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
		Logger.i("sd",
				"DIRECTORY_DOWNLOADS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
		Logger.i("sd", "DIRECTORY_DCIM " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
		Logger.i("sd", "getFilesDir " + getFilesDir());
		try {
			FileInputStream openFileInput = openFileInput("abc");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				show();
			}

		});
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

		final String[] listFileNames = listExternalDirs();
		int index = 0;
		for(int i=0;i<listFileNames.length;i++){
			if(listFileNames[i].equals(Settings.STORAGE)){
				index = i;
			}
		}
		textView3.setText(listFileNames[index]);
		
		builder.setSingleChoiceItems(listFileNames, index, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				textView3.setText(listFileNames[which]);
				settings.write(Constants.SETTINGS_KEY_STORAGE, listFileNames[which]);
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
