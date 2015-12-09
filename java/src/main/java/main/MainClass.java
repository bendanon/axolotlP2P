package main;

import Security.ChatGroup;
import Security.FingerprintWG;
import Security.PersistentTrustStore;
import Security.SecureParty;//import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException;
import org.whispersystems.libaxolotl.*;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

/**
 * Created by ben on 28/11/15.
 */

public class MainClass {
    public static void main(String[] args)
    {
        SecureParty party1 = null;
        SecureParty party2 = null;
        SecureParty party3 = null;

        //Put any path here, just make sure the user running the app has read/write perms
        String ksPath = "/home/ben/Desktop/keystore";

        PersistentTrustStore store1 = null;
        PersistentTrustStore store2 = null;
        PersistentTrustStore store3 = null;

        try {
            store1 = new PersistentTrustStore(String.format("%s/%s.ks", ksPath, "party1"), "pass", false);
            store2 = new PersistentTrustStore(String.format("%s/%s.ks", ksPath, "party2"), "pass", false);
            store3 = new PersistentTrustStore(String.format("%s/%s.ks", ksPath, "party3"), "pass", false);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            party1 = new SecureParty("party1", store1, new FingerprintWG());
            party2 = new SecureParty("party2", store2, new FingerprintWG());
            party3 = new SecureParty("party2", store3, new FingerprintWG());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            if(party1.consumeKeyExchangeMessage("party2", party2.createKeyExchangeMessage("party1")))
            {
                System.out.println("party1 started trusted conversation with party2");
            }
            else
            {
                System.out.println("party1 started untrusted conversation with party2");
            }
            party2.consumeKeyExchangeMessage("party1", party1.createKeyExchangeMessage("party2"));
            party2.consumeKeyExchangeMessage("party3", party3.createKeyExchangeMessage("party2"));
            party3.consumeKeyExchangeMessage("party2", party2.createKeyExchangeMessage("party3"));
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UntrustedIdentityException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


        try {
            if(party1.consumeIdentityWitness("party2", party2.generateWitness()))
            {
                System.out.println("party1 now trusts party2");
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
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

        } catch (UntrustedIdentityException e) {
            e.printStackTrace();
        } catch (LegacyMessageException e) {
            e.printStackTrace();
        } catch (InvalidVersionException e) {
            e.printStackTrace();
        } catch (InvalidMessageException e) {
            e.printStackTrace();
        } catch (DuplicateMessageException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeyIdException e) {
            e.printStackTrace();
        } catch (NoSessionException e) {
            e.printStackTrace();
        }

        /*String username = "user2";
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

        String buddyJID = "user1";
        String buddyName = "user1";
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

        xmppManager.disconnect();*/

    }
}
