package tt.lab.android.ieltspass;

import android.content.res.AssetManager;
import android.os.Environment;

public class Constants {
	public static final String ROOT_PATH = "IELTSPASS";

	public static String APP_PATH = "";
	public static String SD_PATH = "";
	public static String INTERNAL_PATH = "";
	static {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
	}
	public static AssetManager assetManager;

	public static String STORAGE = SD_PATH;// could be sd card, or something else.

	public static final String SETTINGS_NAME = "settings";

	public static final String DATA_NAME = "word.txt";

	public static final String LISTENING_LYRICS_PATH = "Listening/Lyrics";

	public static final String LISTENING_AUDIOS_URL = "http://ieltspass-ieltspass.stor.sinaapp.com/cb/";


}
