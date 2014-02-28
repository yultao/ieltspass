package tt.lab.android.ieltspass.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.res.AssetManager;
import android.os.Environment;

public class Database {
	private static final String TAG= Database.class.getName();
	private static Map<String, Word> words = new HashMap<String, Word>();
	private static boolean internal;

	static {
		String dataFileName = null;

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			internal = true;
		} else {
			String  dataPath = Constants.SD_PATH+"/"+Constants.DATA_PATH;
			Logger.i(TAG, "dataPath: "+dataPath);
			File dir = new File(dataPath);
			
			if (!dir.exists()) {
				dir.mkdirs();
			}
			dataFileName = dataPath  +"/"+Constants.DATA_NAME;
			File dataFile = new File(dataFileName);

			// 第一次初始化将预装文件复制到SD卡，以后就用这个文件
			if (!dataFile.exists()) {
				long t1 = System.currentTimeMillis();
				try {
					InputStream is = Constants.assetManager.open(Constants.DATA_NAME);
					OutputStream os = new FileOutputStream(dataFileName);
					byte[] buffer = new byte[1024];
					int len;
					while ((len = is.read(buffer)) != -1) {
						os.write(buffer, 0, len);
					}
					is.close();
					os.close();
					internal = false;
				} catch (Exception e) {
					internal = true;
					Logger.i(Database.class.getName(), "Copy DATA failed: " + e.getMessage());
				}
				long t2 = System.currentTimeMillis();
				Logger.i(Database.class.getName(), "Copy DATA completed: " + (t2-t1));
			} else {
				internal = false;
			}
			
			
			String audioPath = Constants.SD_PATH+"/"+Constants.AUDIO_PATH;
			Logger.i(TAG, "audioPath: "+audioPath);
			File audioDir = new File(audioPath);
			if (!audioDir.exists()) {
				audioDir.mkdirs();
			}
			
			String audioFileName = audioPath+"/" +Constants.AUDIO_NAME;
			File audioFile = new File(audioFileName);

			// 第一次初始化将预装文件复制到SD卡，以后就用这个文件
			if (!audioFile.exists()) {
				long t1 = System.currentTimeMillis();
				try {
					InputStream is = Constants.assetManager.open(Constants.AUDIO_NAME);
					OutputStream os = new FileOutputStream(audioFileName);
					byte[] buffer = new byte[1024];
					int len;
					while ((len = is.read(buffer)) != -1) {
						os.write(buffer, 0, len);
					}
					is.close();
					os.close();
				} catch (Exception e) {
					Logger.i(Database.class.getName(), "Copy AUDIO failed: " + e.getMessage());
				}
				long t2 = System.currentTimeMillis();
				Logger.i(Database.class.getName(), "Copy AUDIO completed: " + (t2-t1));
			} 
			
			Logger.i(TAG, "internal: " + internal);
		}
		try {
			InputStream is = internal ? Constants.assetManager.open(Constants.DATA_NAME) : new FileInputStream(
					dataFileName);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {

				String wordInfo = s.trim();
				if (wordInfo.length() != 0) {

					String[] split = wordInfo.split("\t\t");
					Word word = new Word();

					String title = "";
					String phoneticSymbol = "/ /";
					String category = "";
					String date = "";
					String explanation = "";

					if (split.length == 5) {
						title = split[0];
						phoneticSymbol = split[1];
						category = split[2];
						date = split[3];
						explanation = split[4];

					} else if (split.length == 4) {
						title = split[0];
						category = split[1];
						date = split[2];
						explanation = split[3];
					}
					if (!title.trim().equals("")) {

						word.setTitle(title);
						word.setCategory(category);
						word.setDate(date);
						word.setPhoneticSymbol(phoneticSymbol);
						word.setExplanation(explanation.replace("\t", "\n"));
						words.put(title, word);
					}
				}
			}
			is.close();
			bufferedReader.close();

		} catch (Exception e) {
			e.printStackTrace();
			Logger.i(Database.class.getName(), "Exception: " + e.getMessage());
		}
	}

	public static Map<String, Word> getWords() {
		return words;
	}
}
