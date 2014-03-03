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


    public Word getSingleWordInfo(String word_vacabulary) {
        Word word = new Word();
        List<Example> exampleList = new ArrayList<Example>();

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
        Cursor cursor = db.rawQuery("select * from words where word_vacabulary=?", new String[]{word_vacabulary});
        while (cursor.moveToNext()) {
            String BE_phonetic_symbol = cursor.getString(0);
            String AE_phonetic_symbol = cursor.getString(1);
            String BE_sound = cursor.getString(2);
            String AE_sound = cursor.getString(3);
            String Cn_explanation = cursor.getString(4);
            String En_explanation = cursor.getString(5);
            word.setBE_phonetic_symbol(BE_phonetic_symbol);
            word.setAE_phonetic_symbol(AE_phonetic_symbol);
            word.setBE_sound(BE_sound);
            word.setAE_sound(AE_sound);
            word.setCn_explanation(Cn_explanation);
            word.setEn_explanation(En_explanation);
        }

        cursor = db.rawQuery("select * from examples where word_vacabulary=?", new String[]{word_vacabulary});
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
        db.close();
        word.setExampleList(exampleList);

        return word;
    }

    public List<Word> getWordList() {

        List<Word> wordList = new ArrayList<Word>();
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWordsDB();
        Cursor cursor = db.rawQuery("select word_vacabulary, BE_phonetic_symbol，Cn_explanation, category from words ", null);
        while (cursor.moveToNext()) {
            Word word = new Word();
            word.setWord_vacabulary(cursor.getString(0));
            word.setBE_phonetic_symbol(cursor.getString(1));
            word.setCn_explanation(cursor.getString(2));
            word.setCategory(cursor.getString(3));
            wordList.add(word);
        }
        return wordList;
    }
    
}



