package tt.lab.android.ieltspass.model;

/**
 * Created with IntelliJ IDEA.
 * User: hejind
 * Date: 3/4/14
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pic {
    String tinyPic=null;
    String normalPic=null;
    boolean isPrimary=false;

    public String getTinyPic() {
        return tinyPic;
    }

    public void setTinyPic(String tinyPic) {
        this.tinyPic = tinyPic;
    }

    public String getNormalPic() {
        return normalPic;
    }

    public void setNormalPic(String normalPic) {
        this.normalPic = normalPic;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}
