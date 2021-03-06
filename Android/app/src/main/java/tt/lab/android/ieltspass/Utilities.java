package tt.lab.android.ieltspass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import tt.lab.android.ieltspass.data.Settings;
import tt.lab.android.ieltspass.model.Lyrics;
import tt.lab.android.ieltspass.model.Sentence;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utilities {
	public static ConnectivityManager connectivityManager;
	private static final String TAG = Utilities.class.getName();
	private static Format f = new DecimalFormat(".#");

	public static void main(String args[]) {
		parseTime("03:02:01.83");
	}

	public static String formatFizeSize(long len) {
		String length;
		if (len >= 1024 * 1024) {
			length = f(((double) len) / 1024 / 1024) + "MB";
		} else if (len >= 1024) {
			length = len / 1024 + "KB";
		} else {
			length = len + "B";
		}
		return length;
	}

	private static String f(double d) {
		return f.format(d);
	}

	public static String getRecordingFileName() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	public static String getFormatedDate() {
		return getFormatedDate(new Date());
	}

	public static String getFormatedDate(Date date) {
		if (date == null)
			return null;
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	public static int parseTime(String time) {
		// tt.lab.android.ieltspass.Logger.i(TAG, "parseTime I: " + time);
		// 03:02:01.83
		String[] split1 = time.split("[.]");
		String hour = "0";
		String minute = "0";
		String second = "0";
		String millis = "0";

		String[] split2 = split1[0].split(":");
		if (split2.length == 2) {// 02:01
			minute = split2[0];
			second = split2[1];
		} else if (split2.length == 3) {// 01:02:03
			hour = split2[0];
			minute = split2[1];
			second = split2[2];
		}
		if (split1.length == 2) {// 01:02:03.83
			millis = split1[1];
		}
		int millisceonds = Integer.parseInt(hour) * 60 * 60 * 1000 + Integer.parseInt(minute) * 60 * 1000
				+ Integer.parseInt(second) * 1000 + Integer.parseInt(millis) * 10;

		// tt.lab.android.ieltspass.Logger.i(TAG, "parseTime O: " + millisceonds);
		return millisceonds;
	}

	public static String formatTime(int timeInt) {
		String s = formatTimeSecond(timeInt);
		return s;
	}

	public static String formatTimeSecond(int timeInt) {
		String str = "";
		int hour = 0;
		int minute = 0;
		int second = 0;

		second = timeInt / 1000;

		if (second >= 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute >= 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		str = (minute > 9 ? "" + minute : "0" + minute) + ":" + (second > 9 ? "" + second : "0" + second);
		if (hour > 0) {
			str = (hour > 9 ? "" + hour : "0" + hour) + ":" + str;
		}
		return str;
	}

	public static String formatTimeMillis(int timeInt) {
		String str = "";
		int hour = 0;
		int minute = 0;
		int second = 0;
		int millis = 0;
		// 11123
		millis = timeInt % 1000;
		millis /= 10;
		second = timeInt / 1000;

		if (second >= 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute >= 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		str = (minute > 9 ? "" + minute : "0" + minute) + ":" + (second > 9 ? "" + second : "0" + second) + "."
				+ (millis > 9 ? "" + millis : "0" + millis);
		if (hour > 0) {
			str = (hour > 9 ? "" + hour : "0" + hour) + ":" + str;
		}
		return str;
	}

	public static Lyrics parseLyrics(String lyricsName) {
		Lyrics lyrics = new Lyrics();
		try {
			String path = Constants.LISTENING_LYRICS_PATH + "/" + lyricsName;
			InputStream is = Constants.assetManager.open(path);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			String s = null;
			Sentence previousSentence = null;
			// int index = 0;
			while ((s = bufferedReader.readLine()) != null) {
				try {
					String ss = s.trim();
					if (ss.length() != 0 && ss.startsWith("[")) {
						// [02:01.83]我却受控在你手里
						// [02:06.44]
						String time = ss.substring(ss.indexOf("[") + 1, ss.indexOf("]"));
						String text = ss.substring(ss.indexOf("]") + 1).trim();
						Sentence sentence = new Sentence();
						sentence.setIndex(lyrics.getSentenceCount());
						sentence.setRaw(ss);
						sentence.setStart(Utilities.parseTime(time));
						sentence.setTime(time.substring(0, time.indexOf(".")));// remove milliseconds
						sentence.setText(text);
						if (previousSentence != null) {
							previousSentence.setNextSentence(sentence);
						}

						lyrics.addSentence(sentence);
						previousSentence = sentence;
					}
				} catch (Exception e) {
					Logger.e(TAG, "parseLyrics E: " + e.getMessage());
				}
			}
			is.close();
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.i(TAG, "parseLyrics E: " + e);
		}
		return lyrics;
	}

	/**
	 * Search a point. This is not accurate, as it can only find sentences once a second.
	 * 
	 * @param cp
	 * @return
	 */
	public static Sentence findSentenceHash(Lyrics lyrics, int cp) {
		String formatedTime = Utilities.formatTime(cp);
		return lyrics.getSentence(formatedTime);
	}

	// public static int c=0;
	/**
	 * Search a range.
	 * 
	 * @param cp
	 * @return
	 */
	public static Sentence findSentenceSimple(List<Sentence> sentences, int cp) {

		Sentence s = null;
		boolean b = false;
		for (int i = 0; i < sentences.size(); i++) {
			Sentence sentence = sentences.get(i);
			// /c=i;
			if (sentence.getStart() <= cp && cp < sentence.getEnd()) {
				s = sentence;
				b = true;
				break;
			}
		}

		// tt.lab.android.ieltspass.Logger.i(TAG, "findSentenceSimple: "+b+", "+c);
		return s;
	}

	/**
	 * Search a range.
	 * 
	 * @param cp
	 * @return
	 */
	public static Sentence findSentenceBinary(List<Sentence> sentences, int cp) {

		// c++;
		Sentence s = null;
		if (sentences.size() > 0) {

			int middle = sentences.size() / 2;
			// tt.lab.android.ieltspass.Logger.i(TAG, "findSentenceBinary: "+c+", "+middle+", "+cp);
			Sentence middleSentence = sentences.get(middle);
			// tt.lab.android.ieltspass.Logger.i(TAG, "findSentenceBinary: "+middleSentence.getStart()+" ~ "+middleSentence.getEnd());
			if (cp < middleSentence.getStart()) {
				// tt.lab.android.ieltspass.Logger.i(TAG, "findSentenceBinary: left "+(sentences.size()>1));
				if (sentences.size() > 1) {
					s = findSentenceBinary(sentences.subList(0, middle), cp);
				}

			} else if (cp == middleSentence.getStart()) {
				// tt.lab.android.ieltspass.Logger.i(TAG, "findSentenceBinary: middle a");
				s = sentences.get(middle);
			} else if (cp > middleSentence.getStart() && cp < middleSentence.getEnd()) {
				// tt.lab.android.ieltspass.Logger.i(TAG, "findSentenceBinary: middle b");
				s = sentences.get(middle);

				// } else if(middle==0){
				// tt.lab.android.ieltspass.Logger.i(TAG, "findSentenceBinary: middle c");
				// s =sentences.get(middle);
			} else {
				// tt.lab.android.ieltspass.Logger.i(TAG, "findSentenceBinary: right "+(sentences.size()>1));
				if (sentences.size() > 1) {
					s = findSentenceBinary(sentences.subList(middle, sentences.size()), cp);
				}

			}
		}
		return s;
	}

	public static boolean isNetworkConnected() {
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	public static boolean isWifiConnected() {

		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	public static boolean isMobileConnected() {
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	public static int getConnectedType() {
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return networkInfo.getType();
		}
		return -1;
	}

	/**
	 * TODO
	 * 
	 * @param picurl
	 * @return
	 */
	public static String getTinyPic(String imagePath, String picurl) {
		// tt.lab.android.ieltspass.Logger.i(TAG, "getTinyPic I "+picurl);
		String url = picurl;
		try {
			if (picurl != null) {
				String filename = picurl.substring(picurl.lastIndexOf("/") + 1);
				File file = new File(imagePath + "/" + filename);
				if (file.exists()) {
					url = file.getAbsolutePath();
				} else {
					url = Constants.SERVER_URL+picurl;
				}
			}
		} catch (Exception e) {
			Logger.e(TAG, "getTinyPic E:" + e);
		}
		// tt.lab.android.ieltspass.Logger.i(TAG, "getTinyPic O "+url);
		return url;
	}

	public static void ensurePath(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static String formatTimeLong(long millis) {
		Date date = new Date(millis);
		return getFormatedDate(date);
	}

	public static boolean isFileExist(String absFileName) {
		return new File(absFileName).exists();
	}
	public static List<String> readFile(String absFileName) {
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(absFileName)));
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {
				list.add(s);
			}
			bufferedReader.close();
		} catch (Exception e) {
			Logger.e(TAG, "readFile E: " + e);
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<String> readAsset(String absFileName) {
		List<String> list = new ArrayList<String>();
		try {
			InputStream inputStream = Constants.assetManager.open(absFileName);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {
				list.add(s);
			}
			bufferedReader.close();
		} catch (Exception e) {
			Logger.e(TAG, "readFile E: " + e);
			e.printStackTrace();
		}
		return list;
	}
	public static void writeFile(String absFileName, String s) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(absFileName);
			fileOutputStream.write(s.getBytes());
			//PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(absFileName)));
			//writer.println(s);
			///writer.close();
			fileOutputStream.close();
		} catch (Exception e) {
			Logger.e(TAG, "writeFile E: " + e);
			e.printStackTrace();
		}
	}

	public static String getSql(String sqlFile) {
		Logger.i(TAG, "getSql sqlFile " + sqlFile);
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = Constants.assetManager.open("sqls/" + sqlFile + ".sql");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {
				sb.append("\n " + s);
			}
			is.close();
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(TAG, "getSql E: " + e.getMessage());
		}
		String sql = sb.toString();
		Logger.i(TAG, "getSql sql " + sql);
		return sql;
	}

	public static void upZip(String zipFile) {
		try {
			
			ZipFile zfile = new ZipFile(Settings.getDownloadPathStatic()+"/"+zipFile);
			String folderPath = Settings.getStorageStatic();
			Enumeration zList = zfile.entries();
			ZipEntry zipEntry = null;
			byte[] buf = new byte[1024];
			while (zList.hasMoreElements()) {
				zipEntry = (ZipEntry) zList.nextElement();
				if (zipEntry.isDirectory()) {
					Logger.i("upZipFile", "ze.getName() = " + zipEntry.getName());
					String dirstr = folderPath + "/" +zipEntry.getName();
					Logger.i("upZipFile", "str = " + dirstr);
					Utilities.ensurePath(dirstr);
				} else {
					Logger.i("upZipFile", "ze.getName() = " + zipEntry.getName());
					OutputStream os = new FileOutputStream(folderPath + "/" + zipEntry.getName());
					InputStream is = zfile.getInputStream(zipEntry);
					int readLen = 0;
					while ((readLen = is.read(buf)) != -1) {
						os.write(buf, 0, readLen);
					}
					is.close();
					os.close();
				}
			}
			zfile.close();
			Logger.i("upZipFile", "finishssssssssssssssssssss");

		} catch (Exception e) {
			Logger.e(TAG, "upZip: " + e);
		}
	}
}
