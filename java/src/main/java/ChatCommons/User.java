package ChatCommons;

/**
 * Created by Guy on 24/12/2015.
 */
public class User
{
    private String userName;
    private eUserStatus userStatus;

    public User(String name, eUserStatus status)
    {
        this.userName = name;
        this.userStatus = status;
    }

    public eUserStatus GetUserStatus()
    {
        return this.userStatus;
    }

    public String GetUserName()
    {
        return this.userName;
    }

    public void SetUserStatus(eUserStatus newStatus)
    {
        this.userStatus = newStatus;
    }
}
