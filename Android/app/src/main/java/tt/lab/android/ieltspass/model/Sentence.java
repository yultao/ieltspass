package tt.lab.android.ieltspass.model;


public class Sentence {
	private int index;
	private String raw;
	private String time;
	private String text;
	private int start;
	private int end=-1;
	
	private Sentence previousSentence;
	private Sentence nextSentence;
	
	private Object textView;
	
	public String getRaw() {
		return raw;
	}
	public void setRaw(String raw) {
		this.raw = raw;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
		
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end==-1?start+1000:end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public Sentence getNextSentence() {
		return nextSentence;
	}
	
	/**
	 * prev -> next
	 * prev <- next
	 * @param nextSentence
	 */
	public void setNextSentence(Sentence nextSentence) {
		this.nextSentence = nextSentence;
		if(this.nextSentence!=null){
			this.setEnd(nextSentence.getStart());
			this.nextSentence.setPreviousSentence(this);
		}
	}
	public Sentence getPreviousSentence() {
		return previousSentence;
	}
	private void setPreviousSentence(Sentence previousSentence) {
		this.previousSentence = previousSentence;
	}
	public Object getTextView() {
		return textView;
	}
	public void setTextView(Object textView) {
		this.textView = textView;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String toString(){
		return getIndex()+", "+getStart()+", "+getEnd()+", "+getText()+", "+getText()+", "+getTextView()+", "+getRaw();		
	}
	
}
