package tt.lab.android.ieltspass.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hejind
 * Date: 3/1/14
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordsDao {
    Context context = null;

    public WordsDao(Context context) {
        this.context = context;
    }


    public Word getSingleWordInfo(String word_vocabulary) {
        Word word = new Word();
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
        Cursor cursor = db.rawQuery("select * from words where word_vocabulary=?", new String[]{word_vocabulary});
        while (cursor.moveToNext()) {
            String BE_phonetic_symbol = cursor.getString(0);
            String AE_phonetic_symbol = cursor.getString(1);
            String BE_sound = cursor.getString(2);
            String AE_sound = cursor.getString(3);
            String is_listening = cursor.getString(4);
            String is_speaking = cursor.getString(5);
            String is_reading = cursor.getString(6);
            String is_writing = cursor.getString(7);

            word.setBE_phonetic_symbol(BE_phonetic_symbol);
            word.setAE_phonetic_symbol(AE_phonetic_symbol);
            word.setBE_sound(BE_sound);
            word.setAE_sound(AE_sound);
            word.setIs_listening(Integer.parseInt(is_listening));
            word.setIs_speaking(Integer.parseInt(is_speaking));
            word.setIs_reading(Integer.parseInt(is_reading));
            word.setIs_writing(Integer.parseInt(is_writing));

        }

        word.setExampleList(getExamplesForSingleWord(word_vocabulary));
        word.setPicList(getPicsForSingleWord(word_vocabulary));
        word.setMmList(getMmsForSingleWord(word_vocabulary));
        word.setExplanationList(getExplanationsForSingleWord(word_vocabulary));

        return word;
    }

    private List<Example> getExamplesForSingleWord(String word_vocabulary) {
        List<Example> exampleList = new ArrayList<Example>();
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
        Cursor cursor = db.rawQuery("select * from examples where word_vocabulary=?", new String[]{word_vocabulary});
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

    private List<Pic> getPicsForSingleWord(String word_vocabulary) {

        List<Pic> picList = new ArrayList<Pic>();

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
        Cursor cursor = db.rawQuery("select * from pics where word_vocabulary=?", new String[]{word_vocabulary});
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

    private List<MemoryMethod> getMmsForSingleWord(String word_vocabulary) {
        List<MemoryMethod> mmList = new ArrayList<MemoryMethod>();

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
        Cursor cursor = db.rawQuery("select * from memory_methods where word_vocabulary=?", new String[]{word_vocabulary});
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

    private List<Explanation> getExplanationsForSingleWord(String word_vocabulary) {
        List<Explanation> explanationList = new ArrayList<Explanation>();
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
        Cursor cursor = db.rawQuery("select * from explanations where word_vocabulary=?", new String[]{word_vocabulary});
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

    public List<Word> getWordList() {
        //this variable should be set as parameter
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

}



