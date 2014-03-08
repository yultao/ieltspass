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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.AssetManager;
import android.os.Environment;

public class Database {
	private static final String TAG = Database.class.getName();
	private static Map<String, Word> words = new HashMap<String, Word>();
	
	private static boolean internal;

	static {
		String dataFileName = null;

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			internal = true;
		} else {
			dataFileName = initVocabulary();
			//initData(Constants.AUDIO_PATH, Constants.AUDIO_NAME);
			//initData(Constants.AUDIO_PATH, Constants.LRC_NAME);
			
			Logger.i(TAG, "internal: " + internal);
		}
		
		parseWord(dataFileName);
	}

	private static void parseWord(String dataFileName) {
//		try {
//			InputStream is = internal ? Constants.assetManager.open(Constants.DATA_NAME) : new FileInputStream(
//					dataFileName);
//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
//			String s = null;
//			while ((s = bufferedReader.readLine()) != null) {
//
//				String wordInfo = s.trim();
//				if (wordInfo.length() != 0) {
//
//					String[] split = wordInfo.split("\t\t");
//					Word word = new Word();
//
//					String title = "";
//					String phoneticSymbol = "/ /";
//					String category = "";
//					String date = "";
//					String explanation = "";
//
//					if (split.length == 5) {
//						title = split[0];
//						phoneticSymbol = split[1];
//						category = split[2];
//						date = split[3];
//						explanation = split[4];
//
//					} else if (split.length == 4) {
//						title = split[0];
//						category = split[1];
//						date = split[2];
//						explanation = split[3];
//					}
//					if (!title.trim().equals("")) {
//
//						word.setTitle(title);
//						word.setCategory(category);
//						word.setDate(date);
//						word.setPhoneticSymbol(phoneticSymbol);
//						word.setExplanation(explanation.replace("\t", "\n"));
//						words.put(title, word);
//					}
//				}
//			}
//			is.close();
//			bufferedReader.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			Logger.i(Database.class.getName(), "Exception: " + e.getMessage());
//		}
	}

	public static List<Map<String,String>> parseLyrics(String lyricsName) {
		List<Map<String,String>> lyricsList = new ArrayList<Map<String,String>>();
		try {
			InputStream is = new FileInputStream(Constants.SD_PATH + "/" + Constants.AUDIO_PATH + "/" + lyricsName);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {
				try{
					String ss = s.trim();
					if (ss.length() != 0 && ss.startsWith("[")) {
						//[02:01.83]我却受控在你手里
						//[02:06.44]
						String time = ss.substring(ss.indexOf("[") + 1, ss.indexOf("]"));
						String word = ss.substring(ss.indexOf("]") + 1).trim();
						Map<String, String> m = new HashMap<String, String>();
						time = time.substring(0,time.indexOf("."));
						//time = time.substring(0,time.length()-1);
						m.put("time", time);
						m.put("word", word);
						lyricsList.add(m);
					}
				}catch (Exception e){
					Logger.i(TAG, "parseLyrics E: "+e.getMessage());
				}
			}
			is.close();
			bufferedReader.close();

		} catch (Exception e) {
			e.printStackTrace();
			Logger.i(Database.class.getName(), "Exception: " + e.getMessage());
		}
		return lyricsList;
	}

	private static void initData(String path, String name) {
		String audioPath = Constants.SD_PATH + "/" + path;
		Logger.i(TAG, "audioPath: " + audioPath);
		File audioDir = new File(audioPath);
		if (!audioDir.exists()) {
			audioDir.mkdirs();
		}

		String audioFileName = audioPath + "/" + name;
		File audioFile = new File(audioFileName);

		// 第一次初始化将预装文件复制到SD卡，以后就用这个文件
		if (!audioFile.exists()) {
			long t1 = System.currentTimeMillis();
			try {
				InputStream is = Constants.assetManager.open(name);
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
			Logger.i(Database.class.getName(), "Copy AUDIO completed: " + (t2 - t1));
		}
	}

	private static String initVocabulary() {
		String dataFileName;
		String dataPath = Constants.SD_PATH + "/" + Constants.DATA_PATH;
		Logger.i(TAG, "dataPath: " + dataPath);
		File dir = new File(dataPath);

		if (!dir.exists()) {
			dir.mkdirs();
		}
		dataFileName = dataPath + "/" + Constants.DATA_NAME;
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
			Logger.i(Database.class.getName(), "Copy DATA completed: " + (t2 - t1));
		} else {
			internal = false;
		}
		return dataFileName;
	}

	public static Map<String, Word> getWords() {
		return words;
	}

	
	public static String getSql(String sqlFile) {
		Logger.i(TAG, "getSql sqlFile "+ sqlFile);
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = Constants.assetManager.open("sql/"+sqlFile+".sql");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {
				sb.append("\n "+s );
			}
			is.close();
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.i(Database.class.getName(), "getSql E: " + e.getMessage());
		}
		String sql  =sb.toString();
		Logger.i(TAG, "getSql sql "+ sql);
		return sql;
	}
}
