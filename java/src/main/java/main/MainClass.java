package main;

import org.whispersystems.libaxolotl.state.PreKeyBundle;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by ben on 28/11/15.
 */

public class MainClass {
    public static void main(String[] args)
    {
        SecureParty party1 = new SecureParty("party1");
        SecureParty party2 = new SecureParty("party2");

        PreKeyBundle party1Bundle = party1.GeneratePrekey();
        PreKeyBundle party2Bundle = party2.GeneratePrekey();

        party1.StartSession("party2", party2Bundle);
        party2.StartSession("party1", party1Bundle);

        System.out.println(new String(party2.receiveMessage(party1.sendSecure("Hi Bob!".getBytes()))));
        System.out.println(new String(party1.receiveMessage(party2.sendSecure("Hi Alice!".getBytes()))));
        System.out.println(new String(party1.receiveMessage(party2.sendSecure("Hi Alice!".getBytes()))));
        System.out.println(new String(party2.receiveMessage(party1.sendSecure("Hi Bob!".getBytes()))));
        System.out.println(new String(party2.receiveMessage(party1.sendSecure("Hi Bob!".getBytes()))));
        System.out.println(new String(party1.receiveMessage(party2.sendSecure("Hi Alice!".getBytes()))));
        System.out.println(new String(party1.receiveMessage(party2.sendSecure("Hi Alice!".getBytes()))));
        System.out.println(new String(party1.receiveMessage(party2.sendSecure("Hi Alice!".getBytes()))));
        System.out.println(new String(party2.receiveMessage(party1.sendSecure("Hi Bob!".getBytes()))));
        System.out.println(new String(party1.receiveMessage(party2.sendSecure("Hi Alice!".getBytes()))));
        System.out.println(new String(party1.receiveMessage(party2.sendSecure("Hi Alice!".getBytes()))));
        System.out.println(new String(party1.receiveMessage(party2.sendSecure("Hi Alice!".getBytes()))));
        System.out.println(new String(party1.receiveMessage(party2.sendSecure("Hi Alice!".getBytes()))));
        System.out.println(new String(party2.receiveMessage(party1.sendSecure("Hi Bob!".getBytes()))));
        System.out.println(new String(party2.receiveMessage(party1.sendSecure("Hi Bob!".getBytes()))));
        System.out.println(new String(party2.receiveMessage(party1.sendSecure("Hi Bob!".getBytes()))));


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


    }
}
