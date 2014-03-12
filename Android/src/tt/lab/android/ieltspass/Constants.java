package tt.lab.android.ieltspass;

import android.content.res.AssetManager;
import android.os.Environment;

public class Constants {
	public static String SD_PATH="";
	static {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
	}
	public static final String STORAGE = SD_PATH;//could be sd card, or something else.
	private static final String ROOT_PATH="IELTSPASS";

	public static final String DATA_PATH=STORAGE+"/"+ROOT_PATH+"/DATA";
	public static final String DATA_NAME="word.txt";
	
	public static final String LISTENING_LYRICS_PATH="file:///android_asset/Listening/Lyrics";
	public static final String LISTENING_AUDIO_PATH=STORAGE+"/"+ROOT_PATH+"/Listening/Audios";
	public static final String LISTENING_QUESTION_PATH=STORAGE+"/"+ROOT_PATH+"/Listening/Questions";
	public static final String LISTENING_ANSWER_PATH=STORAGE+"/"+ROOT_PATH+"/Listening/Answers";

	public static final String SPEAKING_AUDIO_PATH=STORAGE+"/"+ROOT_PATH+"/Speaking/Audios";

	public static final String VOCABULARY_IMAGE_PATH=STORAGE+"/"+ROOT_PATH+"/Vocabulary/Images";
	public static final String LOG_PATH=STORAGE+"/"+ROOT_PATH+"/LOG";
	public static  AssetManager assetManager;
	
	public static class Preference{
		public static boolean onlyUseWifi = true;
	}
}
