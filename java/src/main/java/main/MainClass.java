package main;

import Security.FingerprintWG;
import Security.PersistentTrustStore;
import Security.SecureParty;//import org.jivesoftware.smack.XMPPException;

import java.security.KeyStoreException;

/**
 * Created by ben on 28/11/15.
 */

public class MainClass {
    public static void main(String[] args)
    {
        SecureParty party1 = null;
        SecureParty party2 = null;
        SecureParty party3 = null;

        String ksPath = "/home/ben/Desktop/keystore";

        PersistentTrustStore store1 = null;
        PersistentTrustStore store2 = null;
        PersistentTrustStore store3 = null;

        try {
            store1 = new PersistentTrustStore(String.format("%s/%s.ks", ksPath, "party1"), "pass");
            store2 = new PersistentTrustStore(String.format("%s/%s.ks", ksPath, "party2"), "pass");
            store3 = new PersistentTrustStore(String.format("%s/%s.ks", ksPath, "party3"), "pass");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        party1 = new SecureParty("party1", store1, new FingerprintWG());
        party2 = new SecureParty("party2", store2, new FingerprintWG());
        party3 = new SecureParty("party2", store3, new FingerprintWG());

        if(party1.consumeKeyExchangeMessage("party2", party2.createKeyExchangeMessage("party1")))
        {

                System.out.println("party1 started trusted conversation with party2");

        }
        party2.consumeKeyExchangeMessage("party1", party1.createKeyExchangeMessage("party2"));
        party2.consumeKeyExchangeMessage("party3", party3.createKeyExchangeMessage("party2"));
        party3.consumeKeyExchangeMessage("party2", party2.createKeyExchangeMessage("party3"));

        if(party1.consumeIdentityWitness("party2", party2.generateWitness()))
        {
            System.out.println("party1 now trusts party2");
        }

        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));
        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));
        System.out.println(party1.decrypt("party2", party2.encrypt("party1", "Hi party1!")));
        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));
        System.out.println(party1.decrypt("party2", party2.encrypt("party1", "Hi party1!")));
        System.out.println(party1.decrypt("party2", party2.encrypt("party1", "Hi party1!")));
        System.out.println(party1.decrypt("party2", party2.encrypt("party1", "Hi party1!")));
        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));
        System.out.println(party2.decrypt("party1", party1.encrypt("party2", "Hi party2!")));

        System.out.println(party3.decrypt("party2", party2.encrypt("party3", "Hi party3!")));
        System.out.println(party2.decrypt("party3", party3.encrypt("party2", "Hi party2!")));



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
