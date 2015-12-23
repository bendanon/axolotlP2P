package ChatCommons;

/**
 * Created by Guy on 23/12/2015.
 */

public class User {

    private String userName;
    private eUserStatus userStatus;

    public User(String name, eUserStatus status)
    {
        this.userName = name;
        this.userStatus = status;
    }

    public String GetName()
    {
        return userName;
    }

    public void SetStatus(eUserStatus status)
    {
        userStatus = status;
    }

    public eUserStatus GetUserStatus()
    {
        return userStatus;
    }
}
