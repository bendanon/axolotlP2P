package main;

import org.whispersystems.libaxolotl.state.PreKeyBundle;

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

    }
}
