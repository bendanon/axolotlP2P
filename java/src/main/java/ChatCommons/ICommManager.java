package ChatCommons;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
//git
import java.util.List;
import main.*;
public interface ICommManager {

    //login user
    void userLogin(String userName, String password) throws XMPPException;

    //disconnect
    void disconnect();

    //send message
    public void sendMessage(String message, String buddyName, ChatCommons.eMessageType keyType) throws XMPPException;

    //get online users
    //List<String> getOnlineUsers();

    //add notifier to message listener
    void addNotifier(INotifier notifier);

    //creates entry to friend and creates chat session
    void connectToFriend(String buddyName) throws XMPPException;

    //status functions:
    /*
    	Type -- value type is one of the following values:
	    	available -- (Default) indicates the user is available to receive messages.
	    	unavailable -- the user is unavailable to receive messages.
	    	subscribe -- request subscription to recipient's presence.
	    	subscribed -- grant subscription to sender's presence.
	    	unsubscribe -- request removal of subscription to sender's presence.
	    	unsubscribed -- grant removal of subscription to sender's presence.
	    	error -- the presence packet contains an error message.

    	Status -- free-form text describing a user's presence (i.e., gone to lunch).

    	Mode -- one of five presence modes: available (the default), chat, away, xa (extended away), and dnd (do not disturb).
    */
    //get stats of the friends you currently!! connected to
    //each string array has the following fields: user=0,name,status,mode,type.
    FriendsStatus[] getBuddiesStats();

    //set user status
    //	example: xmppManager.setStatus(true, "Hello everyone",Presence.Mode.available);
    //when connecting status will be set by default, the method is given in case someone wants to update his status
    void setStatus(boolean available, String status,Mode mode);
}
