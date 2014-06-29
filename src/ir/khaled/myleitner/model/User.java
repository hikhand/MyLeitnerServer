package ir.khaled.myleitner.model;

/**
 * Created by khaled.bakhtiari on 5/2/2014.
 */
public class User {
    public static final int NO_USER = -1;
    public int id;
    public String firstName;
    public String lastName;
    public String nickName;
    public String displayName;
    public String picture;
    public Biography biography;
    public Device device;

    public static int getUserId(String udk) {
        return NO_USER;
    }
}