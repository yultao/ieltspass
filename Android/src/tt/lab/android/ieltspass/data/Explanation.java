package tt.lab.android.ieltspass.data;

/**
 * Created with IntelliJ IDEA.
 * User: hejind
 * Date: 3/7/14
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class Explanation {
    int seq;
    String content;
    String part_of_speech;
    String category;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPart_of_speech() {
        return part_of_speech;
    }

    public void setPart_of_speech(String part_of_speech) {
        this.part_of_speech = part_of_speech;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
