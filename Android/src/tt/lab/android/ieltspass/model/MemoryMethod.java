package tt.lab.android.ieltspass.model;

/**
 * Created with IntelliJ IDEA.
 * User: hejind
 * Date: 3/4/14
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemoryMethod {
    String MemoryWay=null;
    boolean isPrimary=false;

    public String getMemoryWay() {
        return MemoryWay;
    }

    public void setMemoryWay(String memoryWay) {
        MemoryWay = memoryWay;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}
