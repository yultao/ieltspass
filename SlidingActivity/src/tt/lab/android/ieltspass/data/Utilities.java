package tt.lab.android.ieltspass.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import tt.lab.android.ieltspass.model.lyrics.Lyrics;
import tt.lab.android.ieltspass.model.lyrics.Sentence;

public class Utilities {
	private static final String TAG = Utilities.class.getName();

	public static void main(String args[]) {
		parseTime("03:02:01.83");
	}

	public static int parseTime(String time) {
		Logger.i(TAG, "parseTime I: " + time);
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

		Logger.i(TAG, "parseTime O: " + millisceonds);
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
			InputStream is = new FileInputStream(Constants.SD_PATH + "/" + Constants.AUDIO_PATH + "/" + lyricsName);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			String s = null;
			Sentence previousSentence = null;
			int index = 0;
			while ((s = bufferedReader.readLine()) != null) {
				try {
					String ss = s.trim();
					if (ss.length() != 0 && ss.startsWith("[")) {
						// [02:01.83]我却受控在你手里
						// [02:06.44]
						String time = ss.substring(ss.indexOf("[") + 1, ss.indexOf("]"));
						String text = ss.substring(ss.indexOf("]") + 1).trim();
						Sentence sentence = new Sentence();
						sentence.setIndex(index++);
						sentence.setRaw(ss);
						sentence.setStart(Utilities.parseTime(time));
						time = time.substring(0, time.indexOf("."));//remove milliseconds
						sentence.setTime(time);
						sentence.setText(text);
						if (previousSentence != null) {
							previousSentence.setNextSentence(sentence);
						}

						lyrics.addSentence(sentence);
						previousSentence = sentence;
					}
				} catch (Exception e) {
					Logger.i(TAG, "parseLyrics E: " + e.getMessage());
				}
			}
			is.close();
			bufferedReader.close();

		} catch (Exception e) {
			e.printStackTrace();
			Logger.i(TAG, "parseLyrics Exception: " + e.getMessage());
		}
		return lyrics;
	}
}
