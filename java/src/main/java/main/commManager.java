package main;

import org.jivesoftware.smack.XMPPException;

public interface commManager {
	//login user
    void userLogin(String username, String password) throws XMPPException;
    //set user status
    void setStatus(boolean available, String status);
    //disconnect
    void disconnect();
    //send message
    void sendMessage(String message, String buddyJID) throws XMPPException;
    // create the message listener
    void setMessageReciver();  
    // connect to user
    public void createEntry(String user, String name) throws Exception;
    
}
