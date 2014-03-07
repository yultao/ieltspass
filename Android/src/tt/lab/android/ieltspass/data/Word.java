package tt.lab.android.ieltspass.data;


import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hejind
 * Date: 2/28/14
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Word {
    String BE_phonetic_symbol;
    String AE_phonetic_symbol;
    String BE_sound;
    String AE_sound;
    String word_vocabulary;
    String logo;
    int is_listening=0;
    int is_speaking=0;
    int is_reading=0;
    int is_writing=0;
    List<Example> exampleList=null;
    List<Pic> picList=null;
    List<MemoryMethod> mmList=null;
    List<Explanation> explanationList=null;

    public List<Example> getExampleList() {
        return exampleList;
    }

    public void setExampleList(List<Example> exampleList) {
        this.exampleList = exampleList;
    }

    public String getWord_vocabulary() {
        return word_vocabulary;
    }

    public void setWord_vocabulary(String word_vocabulary) {
        this.word_vocabulary = word_vocabulary;
    }

    public String getBE_phonetic_symbol() {
        return BE_phonetic_symbol;
    }

    public void setBE_phonetic_symbol(String BE_phonetic_symbol) {
        this.BE_phonetic_symbol = BE_phonetic_symbol;
    }

    public String getAE_phonetic_symbol() {
        return AE_phonetic_symbol;
    }

    public void setAE_phonetic_symbol(String AE_phonetic_symbol) {
        this.AE_phonetic_symbol = AE_phonetic_symbol;
    }

    public String getBE_sound() {
        return BE_sound;
    }

    public void setBE_sound(String BE_sound) {
        this.BE_sound = BE_sound;
    }

    public String getAE_sound() {
        return AE_sound;
    }

    public void setAE_sound(String AE_sound) {
        this.AE_sound = AE_sound;
    }

    public int getIs_listening() {
        return is_listening;
    }

    public void setIs_listening(int is_listening) {
        this.is_listening = is_listening;
    }

    public int getIs_speaking() {
        return is_speaking;
    }

    public void setIs_speaking(int is_speaking) {
        this.is_speaking = is_speaking;
    }

    public int getIs_reading() {
        return is_reading;
    }

    public void setIs_reading(int is_reading) {
        this.is_reading = is_reading;
    }

    public int getIs_writing() {
        return is_writing;
    }

    public void setIs_writing(int is_writing) {
        this.is_writing = is_writing;
    }

    public List<Pic> getPicList() {
        return picList;
    }

    public void setPicList(List<Pic> picList) {
        this.picList = picList;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<MemoryMethod> getMmList() {
        return mmList;
    }

    public void setMmList(List<MemoryMethod> mmList) {
        this.mmList = mmList;
    }

    public List<Explanation> getExplanationList() {
        return explanationList;
    }

    public void setExplanationList(List<Explanation> explanationList) {
        this.explanationList = explanationList;
    }
}
