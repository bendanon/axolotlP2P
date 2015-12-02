package xmppMessages;

import org.jivesoftware.smack.XMPPException;

public class mainTest {

    public static void main(String[] args) throws Exception {
        
        String username = "user1";
        String password = "crypto";
        XmppManager xmppManager;
        try{
        	xmppManager = XmppManager.createManager();
        }
        catch (XMPPException e) {
			System.out.println("System Error");				
			e.printStackTrace();
			return;
		}
        xmppManager.userLogin(username, password);
        xmppManager.setStatus(true, "Hello everyone");
        
        String buddyJID = "user2";
        String buddyName = "user2";
        xmppManager.createEntry(buddyJID, buddyName);
        
        xmppManager.sendMessage("Hello mate", "user2@nb-michaelr");
        
	    boolean isRunning = true;
        
        while (isRunning) {
            Thread.sleep(50);
            xmppManager.sendMessage("Hello mate", "user2@nb-michaelr");
        }
        
        xmppManager.disconnect();
        
	    
	}
}
