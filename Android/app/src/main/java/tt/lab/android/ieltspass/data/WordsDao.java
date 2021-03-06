package tt.lab.android.ieltspass.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.model.Example;
import tt.lab.android.ieltspass.model.Explanation;
import tt.lab.android.ieltspass.model.ExplanationCategory;
import tt.lab.android.ieltspass.model.Familiarity;
import tt.lab.android.ieltspass.model.MemoryMethod;
import tt.lab.android.ieltspass.model.Pic;
import tt.lab.android.ieltspass.model.Word;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created with IntelliJ IDEA. User: hejind Date: 3/1/14 Time: 10:16 PM To change this template use File | Settings |
 * File Templates.
 */
public class WordsDao {
	Context context = null;
	private String TAG = WordsDao.class.getName();

	public WordsDao(Context context) {
		this.context = context;
	}

	public Word getSingleWordInfo(String word_vocabulary) {
		Word word = new Word();
		SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
		Cursor cursor = db.rawQuery("select * from words where word_vocabulary=?", new String[] { word_vocabulary });
		while (cursor.moveToNext()) {
			String BE_phonetic_symbol = cursor.getString(0);
			String AE_phonetic_symbol = cursor.getString(1);
			String BE_sound = cursor.getString(2);
			String AE_sound = cursor.getString(3);
			String is_listening = cursor.getString(4);
			String is_speaking = cursor.getString(5);
			String is_reading = cursor.getString(6);
			String is_writing = cursor.getString(7);
			int wordId = cursor.getInt(12);

			word.setWord_Id(wordId);
			word.setWord_vocabulary(word_vocabulary);
			word.setBE_phonetic_symbol(BE_phonetic_symbol);
			word.setAE_phonetic_symbol(AE_phonetic_symbol);
			word.setBE_sound(BE_sound);
			word.setAE_sound(AE_sound);
			word.setIs_listening(Integer.parseInt(is_listening));
			word.setIs_speaking(Integer.parseInt(is_speaking));
			word.setIs_reading(Integer.parseInt(is_reading));
			word.setIs_writing(Integer.parseInt(is_writing));

		}

		word.setExampleList(getExamplesForSingleWord(word.getWord_Id()));
		word.setPicList(getPicsForSingleWord(word.getWord_Id()));
		word.setMmList(getMmsForSingleWord(word.getWord_Id()));
		word.setExplanationList(getExplanationsForSingleWord(word.getWord_Id()));
		String familirity = getFamiliarity(word.getWord_Id()).getFamiliarity_class();
		word.setFamiliarity(familirity == null ? 0 : Integer.valueOf(familirity));

		return word;
	}

	public List<Example> getExamplesForSingleWord(int word_Id) {
		List<Example> exampleList = new ArrayList<Example>();
		SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
		Cursor cursor = db.rawQuery("select * from examples where word_Id=?", new String[] { String.valueOf(word_Id) });
		while (cursor.moveToNext()) {
			Example example = new Example();
			String sentence = cursor.getString(0);
			String cn_explanation = cursor.getString(1);
			String sentence_sound = cursor.getString(2);

			example.setSentence(sentence);
			example.setCn_explanation(cn_explanation);
			example.setSentence_sound(sentence_sound);
			exampleList.add(example);
		}
		cursor.close();
		return exampleList;
	}

	public List<Pic> getPicsForSingleWord(int word_Id) {

		List<Pic> picList = new ArrayList<Pic>();

		SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
		Cursor cursor = db.rawQuery("select * from pics where word_Id=?", new String[] { String.valueOf(word_Id) });
		while (cursor.moveToNext()) {
			Pic pic = new Pic();
			String tinyPic = cursor.getString(0);
			String normalPic = cursor.getString(1);
			boolean isPrimary = 1 == Integer.parseInt(cursor.getString(2));

			pic.setTinyPic(tinyPic);
			pic.setNormalPic(normalPic);
			pic.setPrimary(isPrimary);
			picList.add(pic);
		}
		cursor.close();
		return picList;
	}

	public List<MemoryMethod> getMmsForSingleWord(int word_Id) {
		List<MemoryMethod> mmList = new ArrayList<MemoryMethod>();

		SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
		Cursor cursor = db.rawQuery("select * from memory_methods where word_Id=?",
				new String[] { String.valueOf(word_Id) });
		while (cursor.moveToNext()) {
			MemoryMethod memoryMethod = new MemoryMethod();
			String memoryWay = cursor.getString(0);
			boolean isPrimary = 1 == Integer.parseInt(cursor.getString(1));

			memoryMethod.setMemoryWay(memoryWay);
			memoryMethod.setPrimary(isPrimary);
			mmList.add(memoryMethod);
		}
		cursor.close();
		return mmList;
	}

	public List<Explanation> getExplanationsForSingleWord(int word_Id) {
		List<Explanation> explanationList = new ArrayList<Explanation>();
		SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
		Cursor cursor = db.rawQuery("select * from explanations where word_Id=? order by seq, category",
				new String[] { String.valueOf(word_Id) });
		while (cursor.moveToNext()) {
			Explanation explanation = new Explanation();
			int seq = Integer.parseInt(cursor.getString(0));
			String content = cursor.getString(1);
			String part_of_speech = cursor.getString(2);
			String category = cursor.getString(3);

			explanation.setSeq(seq);
			explanation.setContent(content);
			explanation.setPart_of_speech(part_of_speech);
			explanation.setCategory(category);
			explanationList.add(explanation);
		}
		cursor.close();
		return explanationList;
	}

	public List<Explanation> getExplanationsForSingleWordByCategory(int word_Id, ExplanationCategory category) {
		List<Explanation> explanationList = new ArrayList<Explanation>();
		SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
		Cursor cursor = db.rawQuery("select * from explanations where word_Id=? and category=?", new String[] {
				String.valueOf(word_Id), category.toString().toLowerCase() });
		while (cursor.moveToNext()) {
			Explanation explanation = new Explanation();
			int seq = Integer.parseInt(cursor.getString(0));
			String content = cursor.getString(1);
			String part_of_speech = cursor.getString(2);

			explanation.setSeq(seq);
			explanation.setContent(content);
			explanation.setPart_of_speech(part_of_speech);
			explanation.setCategory(category.toString());
			explanationList.add(explanation);
		}
		cursor.close();
		return explanationList;
	}

	public int getWordListCount(String query, int familiarityClass) {
		Logger.i(TAG, "\n");
		long t1 = System.currentTimeMillis();
		int count = 0;
		String sql = Utilities.getSql("getWordListCount");
		String familiarityClause = getFamiliarityClause(familiarityClass);
		sql = String.format(sql, familiarityClause);

		Logger.i(TAG, "formated sql: " + sql);
		Cursor cursor = null;
		try {
			SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
			cursor = db.rawQuery(sql, new String[] { query + "%" });

			if (cursor.moveToNext()) {
				count = Integer.parseInt(cursor.getString(0));
			}
		} catch (Exception e) {
			Logger.e(TAG, "getWordListCount, E: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		long t2 = System.currentTimeMillis();
		Logger.i(TAG, "query: " + query + ", count: " + count + ", time elapsed: " + (t2 - t1) + " ms.\n");

		return count;
	}

	private String getFamiliarityClause(int familiarityClass) {
		String familiarityClause = "";
		if (familiarityClass != 0) {
			familiarityClause = "and f.familiarity_class = " + familiarityClass;
		}
		return familiarityClause;
	}

	public List<Word> getWordList(String query, int familiarityClass, String orderBy, String order, int count,
								  int offset) {
		Logger.i(TAG, "getWordList: I\n");
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		long m1 = memoryInfo.availMem;
		long t1 = System.currentTimeMillis();

		List<Word> wordList = new ArrayList<Word>();
		Cursor cursor = null;
		try {

			String sql = Utilities.getSql("getWordList");
			String familiarityClause = getFamiliarityClause(familiarityClass);
			sql = String.format(sql, familiarityClause, orderBy, order);
			Logger.i(TAG, "formated sql: " + sql);
			SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
			cursor = db.rawQuery(sql, new String[] { query + "%", String.valueOf(count), String.valueOf(offset) });

			while (cursor.moveToNext()) {
				int i = 0;
				String word_vocabulary = cursor.getString(i++);
				String BE_phonetic_symbol = cursor.getString(i++);
				String BE_sound = cursor.getString(i++);
				String is_listening = cursor.getString(i++);
				String is_speaking = cursor.getString(i++);
				String is_reading = cursor.getString(i++);
				String is_writing = cursor.getString(i++);
				String part_of_speech = cursor.getString(i++);
				String explanation = cursor.getString(i++);
				String tinyPic = cursor.getString(i++);
				String familiarity = cursor.getString(i++);
				familiarity = familiarity == null ? "0" : familiarity;
				Word word = new Word();
				word.setWord_vocabulary(word_vocabulary);
				word.setBE_phonetic_symbol(BE_phonetic_symbol);
				word.setBE_sound(BE_sound);
				word.setIs_listening(Integer.parseInt(is_listening));
				word.setIs_speaking(Integer.parseInt(is_speaking));
				word.setIs_reading(Integer.parseInt(is_reading));
				word.setIs_writing(Integer.parseInt(is_writing));
				word.setTinyPic(tinyPic);
				word.setExplanation(explanation);
				word.setFamiliarity(Integer.parseInt(familiarity));

				wordList.add(word);

			}
		} catch (Exception e) {
			Logger.e(TAG, "getWordList, E: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		long t2 = System.currentTimeMillis();
		activityManager.getMemoryInfo(memoryInfo);
		long m2 = memoryInfo.availMem;

		Logger.i(TAG, "getWordList O: query: " + query + ", orderby: " + orderBy + ", order: " + order + ", limit: " + count
				+ ", offset: " + offset + ", words retrived: " + wordList.size() + ", time elapsed: " + (t2 - t1)
				+ " ms, memory consumed: " + (m1 - m2) / 1024 + "k.\n");
		return wordList;
	}

	public List<Word> getWordList1() {
		List<Word> wordList = new ArrayList<Word>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {

			String sql = "select" + " w.word_vocabulary, w.BE_phonetic_symbol,"
					+ " w.is_listening, w.is_speaking, w.is_reading, w.is_writing, "
					+ " e.content, p.tinyPic, w.word_id"
					+ " from words w, explanations e, pics p " + " where e.word_Id = w.word_Id"
					+ " and p.word_Id = w.word_Id";

			db = DataBaseHelper.getInstance(context).getWordsDB();
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int i = 0;
				String word_vocabulary = cursor.getString(i++);
				String BE_phonetic_symbol = cursor.getString(i++);
				String BE_sound = cursor.getString(i++);
				String is_listening = cursor.getString(i++);
				String is_speaking = cursor.getString(i++);
				String is_reading = cursor.getString(i++);
				String is_writing = cursor.getString(i++);
				String content = cursor.getString(i++);
				String tinyPic = cursor.getString(i++);
				int word_Id = cursor.getInt(i++);
				Word word = new Word();
				word.setWord_Id(word_Id);
				word.setWord_vocabulary(word_vocabulary);
				word.setBE_phonetic_symbol(BE_phonetic_symbol);
				word.setBE_sound(BE_sound);
				word.setIs_listening(Integer.parseInt(is_listening));
				word.setIs_speaking(Integer.parseInt(is_speaking));
				word.setIs_reading(Integer.parseInt(is_reading));
				word.setIs_writing(Integer.parseInt(is_writing));

				wordList.add(word);

			}
		} catch (Exception e) {
			Logger.e(TAG, "getWordList, E: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			/*
			 * if(db!=null) db.close();
			 */
		}
		return wordList;
	}

	public List<Word> getWordList2() {
		// this variable should be set as parameter
		int count = 50;

		List<Word> wordList = new ArrayList<Word>();
		SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
		Cursor cursor = db.rawQuery("select word_vocabulary from words limit " + count, null);
		while (cursor.moveToNext()) {
			String word_vocabulary = cursor.getString(0);
			wordList.add(getSingleWordInfo(word_vocabulary));
		}
		return wordList;
	}

	public Familiarity getFamiliarity(int word_Id) {
		Familiarity familirity = new Familiarity();
		SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
		Cursor cursor = db.rawQuery("select * from Familiarity where word_Id=?", new String[] { String.valueOf(word_Id) });
		while (cursor.moveToNext()) {

			String familiarity_class = cursor.getString(0);
			Date create_time = new Date(cursor.getLong(2));
			Date update_time = new Date(cursor.getLong(3));
			String user_name = cursor.getString(4);

			familirity.setFamiliarity_class(familiarity_class);
			familirity.setWord_Id(word_Id);
			familirity.setCreate_time(create_time);
			familirity.setUpdate_time(update_time);
			familirity.setUser_name(user_name);
		}

		return familirity;
	}

	public void updateFamiliar(int word_Id, int familiar) {
		Familiarity familirity = getFamiliarity(word_Id);
		SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
		ContentValues cv = new ContentValues();
		cv.put("familiarity_class", familiar);
		cv.put("update_time", (new Date()).getTime());
		if (familirity.getWord_Id() == 0) {
			cv.put("word_Id", word_Id);
			cv.put("create_time", (new Date()).getTime());
			db.insert("familiarity", null, cv);
		}
		else {
			String[] args = {String.valueOf(word_Id)};
			db.update("familiarity", cv, "word_Id=?",args);
		}
	}

}
