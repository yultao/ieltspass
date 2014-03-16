package tt.lab.android.ieltspass;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.os.Environment;

public class Constants {
	public static String APP_PATH="";
	public static String SD_PATH="";
	public static String INTERNAL_PATH="";
	static {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		Environment.getRootDirectory();
	}
	public static final String STORAGE = SD_PATH;//could be sd card, or something else.
	private static final String ROOT_PATH="IELTSPASS";
	public static final String SETTINGS_NAME="settings";
	
	public static final String DATA_PATH=STORAGE+"/"+ROOT_PATH+"/DATA";
	public static final String DATA_NAME="word.txt";
	
	public static final String LISTENING_LYRICS_PATH="Listening/Lyrics";
	public static final String LISTENING_AUDIO_PATH=STORAGE+"/"+ROOT_PATH+"/Listening/Audios";
	public static final String LISTENING_QUESTION_PATH=STORAGE+"/"+ROOT_PATH+"/Listening/Questions";
	public static final String LISTENING_ANSWER_PATH=STORAGE+"/"+ROOT_PATH+"/Listening/Answers";
	
	public static final String SPEAKING_AUDIO_PATH=STORAGE+"/"+ROOT_PATH+"/Speaking/Audios";

	public static final String VOCABULARY_IMAGE_PATH=STORAGE+"/"+ROOT_PATH+"/Vocabulary/Images";
	public static final String LOG_PATH=STORAGE+"/"+ROOT_PATH+"/Logs";
	public static  AssetManager assetManager;
	
	
	public static final String SETTINGS_KEY_INTERNET = "INTERNET";
	public static final String SETTINGS_KEY_STORAGE = "STORAGE";
	public static final String SETTINGS_VALUE_INTERNET_NO = "NO";
	public static final String SETTINGS_VALUE_INTERNET_WIFI = "WIFI";
	public static final String SETTINGS_VALUE_INTERNET_MOBILE = "MOBILE";
}
