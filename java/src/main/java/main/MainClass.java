package main;

//import org.jivesoftware.smack.XMPPException;

/**
 * Created by ben on 28/11/15.
 */

public class MainClass {
    public static void main(String[] args)
    {
        SecureParty party1 = new SecureParty("party1");
        SecureParty party2 = new SecureParty("party2");

        party1.ConsumeKeyExchangeMessage("party2", party2.CreateKeyExchangeMessage("party1"));
        party2.ConsumeKeyExchangeMessage("party1", party1.CreateKeyExchangeMessage("party2"));

        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));
        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));
        System.out.println(party1.decrypt("party2", party2.encrypt("party1", "Hi party1!")));
        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));
        System.out.println(party1.decrypt("party2", party2.encrypt("party1", "Hi party1!")));
        System.out.println(party1.decrypt("party2", party2.encrypt("party1", "Hi party1!")));
        System.out.println(party1.decrypt("party2", party2.encrypt("party1", "Hi party1!")));
        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));
        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));


        /*
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
        try {
            xmppManager.userLogin(username, password);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        xmppManager.setStatus(true, "Hello everyone");

        String buddyJID = "user2";
        String buddyName = "user2";
        try {
            xmppManager.createEntry(buddyJID, buddyName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            xmppManager.sendMessage("Hello mate", "user2@ben-probook");
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        boolean isRunning = true;

        while (isRunning) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                xmppManager.sendMessage("Hello mate", "user2@ben-probook");
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }

        xmppManager.disconnect();
        */
    }
}
