package tt.lab.android.ieltspass.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import tt.lab.android.ieltspass.Constants;
import tt.lab.android.ieltspass.Logger;

public class Settings {
	public static final String SETTINGS_KEY_INTERNET = "INTERNET";
	public static final String SETTINGS_KEY_STORAGE = "STORAGE";
	public static final String SETTINGS_VALUE_INTERNET_NO = "NO";
	public static final String SETTINGS_VALUE_INTERNET_WIFI = "WIFI";
	public static final String SETTINGS_VALUE_INTERNET_MOBILE = "MOBILE";

	private static Settings instance;
	private String TAG = Settings.class.getName();
	private Context context;
	private Map<String, String> map = new HashMap<String, String>();
	// public static boolean onlyUseWifi = true;

	private String internet = SETTINGS_VALUE_INTERNET_WIFI;
	private String storage = Constants.SD_PATH;

	public boolean isNetwordForbidden() {
		return SETTINGS_VALUE_INTERNET_NO.equals(internet);
	}

	public boolean isOnlyUseWifi() {
		return SETTINGS_VALUE_INTERNET_WIFI.equals(internet);
	}

	public boolean isWifiAndMobile() {
		return SETTINGS_VALUE_INTERNET_MOBILE.equals(internet);
	}

	public static synchronized Settings getInstance(Context context) {
		if (instance == null) {
			instance = new Settings(context);
			instance.read();
		}
		return instance;
	}

	private void read() {
		// Logger.i(TAG, "read I");
		try {
			// InputStream is = Constants.assetManager.open();
			FileInputStream is = this.context.openFileInput(Constants.SETTINGS_NAME);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {
				s = s.trim();
				if (!"".equals(s)) {
					// Logger.i(TAG, s + "\n");

					String[] ss = s.split("\t");
					String key = ss[0];
					String value = ss[1];
					if (SETTINGS_KEY_INTERNET.equals(key)) {
						this.setInternet(value);
					} else if (SETTINGS_KEY_STORAGE.equals(key)) {
						this.setStorage(value);
					}
					map.put(key, value);
				}
			}
		} catch (IOException e) {
			// Logger.i(TAG, "read E: " + e);
			e.printStackTrace();
		}
		// Logger.i(TAG, "read O");
	}

	public void write(String key, String value) {
		Logger.i(TAG, "write I");
		map.put(key, value);
		StringBuilder sb = new StringBuilder();
		for (String k : map.keySet()) {
			String v = map.get(k);
			sb.append(k + "\t" + v + "\n");
		}

		try {
			// FileOutputStream fileOutputStream = new FileOutputStream("");
			FileOutputStream fileOutputStream = this.context.openFileOutput(Constants.SETTINGS_NAME,
					Context.MODE_PRIVATE);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileOutputStream));
			writer.println(sb.toString());
			Logger.i(TAG, "write sb " + sb.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			Logger.e(TAG, "write E: " + e);
			e.printStackTrace();
		}
		Logger.i(TAG, "write O");
	}

	private Settings(Context context) {
		this.context = context;
	}

	public void setStorage(String storage) {
		this.storage = storage;
		Logger.initLog(getLogsPath());
	}

	public String getStorage() {
		return storage;
	}

	public void setInternet(String internet) {
		this.internet = internet;
	}

	public final String getDataPath() {
		return this.getStorage() + "/" + Constants.ROOT_PATH + "/DATA";
	}

	public final String getListeningAudiosPath() {
		return this.getStorage() + "/" + Constants.ROOT_PATH + "/Listening/Audios";
	}

	public final String getListeningQuestionsPath() {
		return this.getStorage() + "/" + Constants.ROOT_PATH + "/Listening/Questions";
	}

	public final String getListeningAnswersPath() {
		return this.getStorage() + "/" + Constants.ROOT_PATH + "/Listening/Answers";
	}

	public final String getSpeakingAudiosPath() {
		return this.getStorage() + "/" + Constants.ROOT_PATH + "/Speaking/Audios";
	}

	public final String getVocabularyImagesPath() {
		return this.getStorage() + "/" + Constants.ROOT_PATH + "/Vocabulary/Images";
	}

	public final String getVocabularyAudiosPath() {
		return this.getStorage() + "/" + Constants.ROOT_PATH + "/Vocabulary/Audios";
	}

	public final String getLogsPath() {
		return this.getStorage() + "/" + Constants.ROOT_PATH + "/Logs";
	}

	public String getReadingQuestionsPath() {
		return this.getStorage() + "/" + Constants.ROOT_PATH + "/Reading/Questions";
	}

	public String getSpeakingQuestionsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSpeakingScriptsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReadingAnswersPath() {
		// TODO Auto-generated method stub
		return null;
	}

}