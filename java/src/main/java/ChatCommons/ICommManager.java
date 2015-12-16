package ChatCommons;

import org.jivesoftware.smack.XMPPException;

/**
 * Created by Guy on 16/12/2015.
 */
public interface ICommManager
{
    //login user
    public void userLogin(String username, String password) throws XMPPException;
    //set user status
    public void setStatus(boolean available, String status);
    //disconnect
    public void disconnect();
    //send message
    public void sendMessage(String message, String buddyJID) throws XMPPException;

    //creates entry to friend and chat session
    public void setChat(String buddyJID, String name) throws XMPPException;

    //waits for a new message, preferable to run on a separate thread
    public void getBuddyList();
}
