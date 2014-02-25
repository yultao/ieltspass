package tt.lab.android.ieltspass.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;

public class Database {
	private static Map<String, Word> words = new HashMap<String, Word>();
	private static final String DATA_PATH="IELTS_PASS";
	private static final String DATA_NAME="word.txt";
	static {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
			String targetDir = sdDir + "/" + DATA_PATH +"/";
			File dir = new File(targetDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String wordFilePath = targetDir + DATA_NAME;
			File wordFile = new File(wordFilePath);
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(wordFile)));
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
				bufferedReader.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Map<String, Word> getWords() {
		return words;
	}
}
