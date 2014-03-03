package tt.lab.android.ieltspass.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import tt.lab.android.ieltspass.model.lyrics.Lyrics;
import tt.lab.android.ieltspass.model.lyrics.Sentence;

public class Utilities {
	private static final String TAG = Utilities.class.getName();

	public static void main(String args[]) {
		parseTime("03:02:01.83");
	}

	public static int parseTime(String time) {
		//Logger.i(TAG, "parseTime I: " + time);
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

		//Logger.i(TAG, "parseTime O: " + millisceonds);
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
			//int index = 0;
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
						sentence.setTime(time.substring(0, time.indexOf(".")));//remove milliseconds
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
	
	/**
	 * Search a point.
	 * This is not accurate, as it can only find sentences once a second. 
	 * @param cp
	 * @return
	 */
	public static Sentence findSentenceHash(Lyrics lyrics, int cp) {
		String formatedTime = Utilities.formatTime(cp);
		return lyrics.getSentence(formatedTime);
	}
	//public static int c=0;
	/**
	 * Search a range.
	 * @param cp
	 * @return
	 */
	public static Sentence findSentenceSimple(List<Sentence> sentences, int cp) {
		
		Sentence s = null;
		boolean b = false;
		for (int i = 0; i < sentences.size(); i++) {
			Sentence sentence = sentences.get(i);
			///c=i;
			if (sentence.getStart() <= cp && cp < sentence.getEnd()){
				s= sentence;
				b=true;
				break;
			}
		}
		
		//Logger.i(TAG, "findSentenceSimple: "+b+", "+c);
		return s;
	}
	
	/**
	 * Search a range.
	 * @param cp
	 * @return
	 */
	public static Sentence findSentenceBinary(List<Sentence> sentences, int cp) {
		 
		//c++;
		Sentence s = null;
		int middle = sentences.size()/2;
		//Logger.i(TAG, "findSentenceBinary: "+c+", "+middle+", "+cp);
		Sentence middleSentence = sentences.get(middle);
		//Logger.i(TAG, "findSentenceBinary: "+middleSentence.getStart()+" ~ "+middleSentence.getEnd());
		if(cp<middleSentence.getStart()){
			//Logger.i(TAG, "findSentenceBinary: left "+(sentences.size()>1));
			if(sentences.size()>1){
				s = findSentenceBinary(sentences.subList(0, middle),cp);	
			}
			
		} else if(cp==middleSentence.getStart()){
			//Logger.i(TAG, "findSentenceBinary: middle a");
			s =sentences.get(middle);
		} else if(cp>middleSentence.getStart() && cp<middleSentence.getEnd()){
			//Logger.i(TAG, "findSentenceBinary: middle b");
			s =sentences.get(middle);
			
		//} else if(middle==0){
			//Logger.i(TAG, "findSentenceBinary: middle c");
			//s =sentences.get(middle);
		} else {
			//Logger.i(TAG, "findSentenceBinary: right "+(sentences.size()>1));
			if(sentences.size()>1){
				s = findSentenceBinary(sentences.subList(middle, sentences.size()),cp);	
			}
			
		}
		
		return s;
	}
}
