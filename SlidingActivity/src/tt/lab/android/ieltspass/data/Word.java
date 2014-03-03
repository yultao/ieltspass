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
    String Cn_explanation;
    String En_explanation;
    String word_vacabulary;
    String pic;
    String category;
    List<Example> exampleList=null;

    public List<Example> getExampleList() {
        return exampleList;
    }

    public void setExampleList(List<Example> exampleList) {
        this.exampleList = exampleList;
    }

    public String getWord_vacabulary() {
        return word_vacabulary;
    }

    public void setWord_vacabulary(String word_vacabulary) {
        this.word_vacabulary = word_vacabulary;
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

    public String getCn_explanation() {
        return Cn_explanation;
    }

    public void setCn_explanation(String cn_explanation) {
        Cn_explanation = cn_explanation;
    }

    public String getEn_explanation() {
        return En_explanation;
    }

    public void setEn_explanation(String en_explanation) {
        En_explanation = en_explanation;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
