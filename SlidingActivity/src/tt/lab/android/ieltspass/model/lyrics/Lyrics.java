package tt.lab.android.ieltspass.model.lyrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lyrics {

	private List<Sentence> sentences = new ArrayList<Sentence>();
	private Map<String, Sentence> sentenceMap = new HashMap<String,Sentence>();
	
	public int getSentenceCount(){
		return this.sentences.size();
	}
	public void addSentence(Sentence sentence){
		this.sentences.add(sentence);
		this.sentenceMap.put(sentence.getTime(), sentence);
	}
	public Sentence getSentence(String time) {
		return this.sentenceMap.get(time);
	}
	public Sentence getSentence(int index) {
		return this.sentences.get(index);
	}
	
	public List<Sentence> getSentences() {
		return sentences;
	}

	public Sentence getScrollTopSentence(Sentence sentence){
		int index = sentence.getIndex() - 5;
		if (index <= 0) {
			index = 0;
		}
		
		return this.getSentence(index);
	}
	public Sentence getScrollBottomSentence(Sentence sentence){
		int index = sentence.getIndex() + 5;
		if (index >= this.getSentenceCount()) {
			index = this.getSentenceCount() - 1;
		}
		return this.getSentence(index);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(Sentence sentence: this.sentences){
			sb.append(sentence+"\n");
		}
		return sb.toString();
	}
}
