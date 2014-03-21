package tt.lab.android.ieltspass.model;


import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hejind
 * Date: 2/28/14
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Word {
	private int word_Id;
	private String BE_phonetic_symbol;
	private String AE_phonetic_symbol;
	private String BE_sound;
	private String AE_sound;
	private String word_vocabulary;
	private String logo;
	private int is_listening=0;
	private int is_speaking=0;
	private int is_reading=0;
	private int is_writing=0;
    
    private String part_of_speech;
    private String explanation;
    private String tinyPic;
    private int familiarity;
    
    

	List<Example> exampleList=null;
    List<Pic> picList=null;
    List<MemoryMethod> mmList=null;
    List<Explanation> explanationList=null;
    String category;

	public int getWord_Id() {
		return word_Id;
	}

	public void setWord_Id(int word_Id) {
		this.word_Id = word_Id;
	}

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPart_of_speech() {
		return part_of_speech;
	}

	public void setPart_of_speech(String part_of_speech) {
		this.part_of_speech = part_of_speech;
	}


	public String getTinyPic() {
		return tinyPic;
	}

	public void setTinyPic(String tinyPic) {
		this.tinyPic = tinyPic;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

    public int getFamiliarity() {
        return familiarity;
    }

    public void setFamiliarity(int familiarity) {
        this.familiarity = familiarity;
    }
}
