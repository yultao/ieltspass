package tt.lab.android.ieltspass.data;

import android.content.res.AssetManager;
import android.os.Environment;

public class Constants {
	public static String SD_PATH="";
	static {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
	}
	public static final String ROOT_PATH="IELTSPASS";

	public static final String DATA_PATH=ROOT_PATH+"/DATA";
	public static final String DATA_NAME="word.txt";
	
	public static final String AUDIO_PATH=ROOT_PATH+"/AUDIO";
	public static final String AUDIO_NAME="test.mp3";
	
	public static final String LOG_PATH=ROOT_PATH+"/LOG";
	public static  AssetManager assetManager;
	
}
