package ChatCommons;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;

import java.util.List;

public interface ICommManager {
    enum eMessageType {eNORMAL,eKEY_START,eKEY_RESPONSE,eKEY_FINISHED,eWITNESS}
    //login user
    void userLogin(String userName, String password) throws XMPPException;

    //disconnect
    void disconnect();

    //send message
    void sendMessage(String message, String buddyName, eMessageType messageType) throws XMPPException;

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
    void getBuddiesStats();

    //set user status
    //	example: xmppManager.setStatus(true, "Hello everyone",Presence.Mode.available);
    void setStatus(boolean available, String status,Mode mode);
}
