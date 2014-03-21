package tt.lab.android.ieltspass.model;

import java.util.Date;

public class Familiarity {
	private String familiarity_class;
	private int word_Id;
	private Date create_time;
	private Date update_time;
	private String user_name;
	
	public String getFamiliarity_class() {
		return familiarity_class;
	}
	public void setFamiliarity_class(String familiarity_class) {
		this.familiarity_class = familiarity_class;
	}	
	public int getWord_Id() {
		return word_Id;
	}
	public void setWord_Id(int word_Id) {
		this.word_Id = word_Id;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
}
