package main;

import org.whispersystems.libaxolotl.*;
import org.whispersystems.libaxolotl.ecc.Curve;
import org.whispersystems.libaxolotl.ecc.ECKeyPair;
import org.whispersystems.libaxolotl.protocol.PreKeyWhisperMessage;
import org.whispersystems.libaxolotl.protocol.WhisperMessage;
import org.whispersystems.libaxolotl.state.AxolotlStore;
import org.whispersystems.libaxolotl.state.PreKeyBundle;
import org.whispersystems.libaxolotl.state.PreKeyRecord;
import org.whispersystems.libaxolotl.state.SignedPreKeyRecord;
import org.whispersystems.libaxolotl.state.impl.InMemoryAxolotlStore;

import java.util.List;

/**
 * Created by ben on 28/11/15.
 */
public class SecureParty
{
    //Similar for all sessions
    private String email;
    private int numericId;
    private IdentityKeyPair identityKeyPair;
    private ECKeyPair signedPair;
    private byte[] signedPreKeySignature;
    private AxolotlStore store;

    //Maintained per session
    private SessionBuilder builder;
    private SessionCipher cipher;
    private ECKeyPair ephemeralPair;

    public SecureParty(String email)
    {
        this.email = email;
        this.numericId = email.hashCode();
        InitializeStore(email);
    }

    protected AxolotlStore GenerateKeyStore(String email)
    {
        //Create the identity keys
        ECKeyPair ecPair = Curve.generateKeyPair();
        IdentityKey idKey = new IdentityKey(ecPair.getPublicKey());
        IdentityKeyPair idPair = new IdentityKeyPair(idKey, ecPair.getPrivateKey());

        //Create an in-memory Axolotl store (non-persistent)
        return new InMemoryAxolotlStore(idPair, numericId);
    }

    private int getSignedPrekeyId()
    {
        return (email + "signed").hashCode();
    }

    private void InitializeStore(String email)
    {
        store = GenerateKeyStore(email);

        identityKeyPair = store.getIdentityKeyPair();

        List<SignedPreKeyRecord> signedPrekeys = store.loadSignedPreKeys();
        if(signedPrekeys.isEmpty())
        {
            signedPair = Curve.generateKeyPair();
            try {
                signedPreKeySignature = Curve.calculateSignature(identityKeyPair.getPrivateKey(),
                        signedPair.getPublicKey().serialize());

                SignedPreKeyRecord record = new SignedPreKeyRecord(getSignedPrekeyId(),
                        0, signedPair, signedPreKeySignature);
                store.storeSignedPreKey(getSignedPrekeyId(), record);

            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        else
        {
            signedPair = signedPrekeys.get(0).getKeyPair();
        }
    }

    public PreKeyBundle GetKeyExchangeMessageFor(String peerEmail)
    {
        //Create new ephemeral key
        ephemeralPair = Curve.generateKeyPair();

        //Get an id for the prekey for this peer
        int prekeyId = peerEmail.hashCode();
        PreKeyRecord record = new PreKeyRecord(prekeyId, ephemeralPair);

        // remove the old prekey in case we already had a conversation
        store.removePreKey(prekeyId);

        //Store the new prekey
        store.storePreKey(prekeyId, record);


        return new PreKeyBundle(numericId, numericId, prekeyId,
                ephemeralPair.getPublicKey(), getSignedPrekeyId(), signedPair.getPublicKey(),
                signedPreKeySignature, identityKeyPair.getPublicKey());
    }

    public void StartSession(String peerEmail, PreKeyBundle bundle)
    {
        //Create a session builder
        AxolotlAddress remoteAddress = new AxolotlAddress(peerEmail, peerEmail.hashCode());
        builder = new SessionBuilder(store, remoteAddress);

        try {

            //Process the counterpart prekey
            builder.process(bundle);
            cipher = new SessionCipher(store, remoteAddress);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UntrustedIdentityException e) {
            e.printStackTrace();
        }
    }

    public byte[] sendSecure(byte[] plaintext)
    {
        return cipher.encrypt(plaintext).serialize();
    }

    public byte[] recievePreKeyMessage(byte[] ciphertext)
    {
        byte[] plaintext = null;

        try {
            plaintext =  cipher.decrypt(new PreKeyWhisperMessage(ciphertext));
        } catch (InvalidMessageException e) {
            e.printStackTrace();
        } catch (InvalidVersionException e) {
            e.printStackTrace();
        } catch (DuplicateMessageException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UntrustedIdentityException e) {
            e.printStackTrace();
        } catch (InvalidKeyIdException e) {
            e.printStackTrace();
        } catch (LegacyMessageException e) {
            e.printStackTrace();
        }

        return plaintext;
    }


    public byte[] receiveMessage(byte[] ciphertext)
    {
        byte[] plaintext = null;

        try {

            //Try to parse it as WhisperMessage
            plaintext = cipher.decrypt(new WhisperMessage(ciphertext));

        } catch (InvalidMessageException e) {

            //We failed to parse it as WhisperMessage, maybe its PPreKeyWhisperMessage
            plaintext = recievePreKeyMessage(ciphertext);

        } catch (LegacyMessageException e) {
            e.printStackTrace();
        } catch (DuplicateMessageException e) {
            e.printStackTrace();
        } catch (NoSessionException e) {
            e.printStackTrace();
        }


        return plaintext;
    }
}
