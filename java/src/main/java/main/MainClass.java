package main;

import ChatCommons.IChatSender;
import org.whispersystems.libaxolotl.*;
import security.conversation.DecryptedPackage;
import security.conversation.HistoryDisagreement;
import security.management.SecureConversation;
import security.management.SecureParty;
import security.trust.concrete.FingerprintWG;
import security.trust.concrete.FingerprintWitness;
import security.trust.concrete.PersistentTrustStore;
import security.utils.HexHumanizer;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.*;


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
            store1 = new PersistentTrustStore(String.format("%s/%s.ks", ksPath, "party1"),
                    "pass", false);

            store2 = new PersistentTrustStore(String.format("%s/%s.ks", ksPath, "party2"),
                    "pass", false);

            store3 = new PersistentTrustStore(String.format("%s/%s.ks", ksPath, "party3"),
                    "pass", false);

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
            party3 = new SecureParty("party3", store3, new FingerprintWG());
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

        /*try {
            party1.revokeTrustedIdentity("party2");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }*/

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
            party1.consumeKeyExchangeMessage("party3", party3.createKeyExchangeMessage("party1"));
            party3.consumeKeyExchangeMessage("party1", party1.createKeyExchangeMessage("party3"));
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


            String witnessRaw = party2.generateWitness().serialize();
            //witnessRaw = witnessRaw.substring(0, witnessRaw.length() / 2 - 2);
            System.out.println(witnessRaw);

            String current =
                    new java.io.File("/home/ben/Projects/AppliedCrypto/SecureChatP2P/" +
                            "java/src/main/resources/64K_english_dict.dic").getCanonicalPath();

            System.out.println("Current dir:"+current);

            HexHumanizer h = null;
            String humanized = null;
            try {
                h = new HexHumanizer (current);
                humanized = h.humanize(witnessRaw);
                System.out.println(humanized);
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println(h.dehumanize(humanized));
            if(party1.consumeIdentityWitness("party2",
                    new FingerprintWitness(h.dehumanize(humanized))))
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

        final Map<String, String> messages = new HashMap<>();

        class SimpleSender implements IChatSender
        {

            @Override
            public void sendMessage(String peer, String message) {
                messages.put(peer, message);
            }
        }

        System.out.println("============");

        SecureConversation conv1 = new SecureConversation(party1, new SimpleSender());
        conv1.addPeer("party2");
        conv1.addPeer("party3");
        SecureConversation conv2 = new SecureConversation(party2, new SimpleSender());
        conv2.addPeer("party1");
        conv2.addPeer("party3");
        SecureConversation conv3 = new SecureConversation(party3, new SimpleSender());
        conv3.addPeer("party1");
        conv3.addPeer("party2");

        //conv3.sendMessage("derp!");

        conv1.sendMessage("Hi party2!");

        try {
            conv3.receiveMessage("party1", messages.get("party3"));
            DecryptedPackage dp = conv2.receiveMessage("party1", messages.get("party2"));

            System.out.println(display("party2", "party1", dp));

            conv1.sendMessage("Hi party2!!");
            conv3.receiveMessage("party1", messages.get("party3"));
            dp = conv2.receiveMessage("party1", messages.get("party2"));
            System.out.println(display("party2", "party1", dp));
            conv1.sendMessage("Hi party2!!!");
            //Party 3 missed!!
            dp = conv2.receiveMessage("party1", messages.get("party2"));
            System.out.println(display("party2", "party1", dp));

            System.out.println("============");
            conv2.sendMessage("Hi party1!");
            //Party 3 missed!!
            dp = conv1.receiveMessage("party2", messages.get("party1"));
            System.out.println(display("party1", "party2", dp));

            conv2.sendMessage("Hi party1!!");
            dp = conv1.receiveMessage("party2", messages.get("party1"));
            //Party 3 missed!!
            System.out.println(display("party1", "party2", dp));


            //After one is gone
            conv1.sendMessage("sup");
            dp = conv2.receiveMessage("party1", messages.get("party2"));
            //Party 3 missed!!
            System.out.println(display("party2", "party1", dp));

            conv2.sendMessage("sup");
            dp = conv1.receiveMessage("party2", messages.get("party1"));
            System.out.println(display("party1", "party2", dp));

            conv3.receiveMessage("party2", messages.get("party3"));

            conv3.sendMessage("HI");

            dp = conv1.receiveMessage("party3", messages.get("party1"));
            System.out.println(display("party1", "party3", dp));
            dp = conv2.receiveMessage("party3", messages.get("party2"));
            System.out.println(display("party2", "party3", dp));

            //party 3 missed indices 1,2 from party 2 and 3,4 from party 1
            //They can retransmit those messages for fixing the conversation

            conv2.retransmit(1);
            conv3.receiveMessage("party2", messages.get("party3"));
            System.out.println(display("party1","party2", conv1.receiveMessage("party2", messages.get("party1"))));
            conv2.retransmit(2);
            conv3.receiveMessage("party2", messages.get("party3"));

            conv1.retransmit(3);
            conv3.receiveMessage("party1", messages.get("party3"));
            conv1.retransmit(4);
            conv3.receiveMessage("party1", messages.get("party3"));

            conv3.sendMessage("fixed");

            dp = conv1.receiveMessage("party3", messages.get("party1"));
            System.out.println(display("party1", "party3", dp));
            dp = conv2.receiveMessage("party3", messages.get("party2"));
            System.out.println(display("party2", "party3", dp));

        } catch (InvalidKeyIdException e) {
            e.printStackTrace();
        } catch (NoSessionException e) {
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
        } catch (UntrustedIdentityException e) {
            e.printStackTrace();
        }

    }

    private static String display(String screen, String sender, DecryptedPackage dp)
    {
        String display = String.format("%s[%d/%d]:%s", sender, dp.getIndex(), dp.getLastChainIndex(), dp.getContent());
        System.out.println(display);

        ListIterator<HistoryDisagreement> hdlist = dp.getHistoryDisagreementIterator();

        boolean inconsistencyFlag = false;
        if(hdlist.hasNext())
        {
            inconsistencyFlag = true;
        }

        StringBuilder builder = new StringBuilder(1000);

        while(hdlist.hasNext())
        {
            HistoryDisagreement hd = hdlist.next();

            builder.append(String.format("The last message %s saw from %s is %d.",
                    sender, hd.getPeerName(), hd.getLastIndexPeerSaw()));

            builder.append(
                    String.format("(The content is %sconsistent with what you saw)%s", hd.isConsistentWithChain() ?
                                    "" : "in", System.getProperty("line.separator")));
        }

        if(inconsistencyFlag)
        {
            String part1 = String.format("You and %s are seeing different views of the conversation.", sender);

            return String.format("+++++%s%s%s%s+++++", System.getProperty("line.separator"),
                    part1, System.getProperty("line.separator"), builder.toString(),
                    System.getProperty("line.separator"));
        }

        return display;

    }
}
