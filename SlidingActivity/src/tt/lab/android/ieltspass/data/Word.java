package tt.lab.android.ieltspass.data;

public class Word {
	private String title;
	private String phoneticSymbol;
	private String category;
	private String date;
	private String explanation;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPhoneticSymbol() {
		return phoneticSymbol;
	}
	public void setPhoneticSymbol(String phoneticSymbol) {
		this.phoneticSymbol = phoneticSymbol;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	public String toString(){
		return title+" "+ phoneticSymbol+" "+explanation;
	}
}
