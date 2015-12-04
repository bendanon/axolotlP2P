package main;

import org.jivesoftware.smack.util.Base64;
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

import java.util.HashMap;
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

    private HashMap<String, SecureSessionContext> sessions;

    public SecureParty(String email)
    {
        sessions = new HashMap<>();
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

    public String CreateKeyExchangeMessage(String peer)
    {
        //Create new ephemeral key
        ECKeyPair ephemeralPair = Curve.generateKeyPair();

        //Get an id for the prekey for this peer
        int prekeyId = peer.hashCode();
        PreKeyRecord record = new PreKeyRecord(prekeyId, ephemeralPair);

        // remove the old prekey in case we already had a conversation
        store.removePreKey(prekeyId);

        //Store the new prekey
        store.storePreKey(prekeyId, record);

        return KeyExchangeUtil.serialize(new PreKeyBundle(numericId, numericId, prekeyId,
                ephemeralPair.getPublicKey(), getSignedPrekeyId(), signedPair.getPublicKey(),
                signedPreKeySignature, identityKeyPair.getPublicKey()));
    }

    public void ConsumeKeyExchangeMessage(String peerEmail, String keyExchangeMessage)
    {
        //Create a session builder
        AxolotlAddress remoteAddress = new AxolotlAddress(peerEmail, peerEmail.hashCode());
        SessionBuilder builder = new SessionBuilder(store, remoteAddress);

        try {

            //Process the counterpart prekey
            builder.process(KeyExchangeUtil.deserialize(keyExchangeMessage));
            SecureSessionContext ctx = new SecureSessionContext();
            ctx.setSessionCipher(new SessionCipher(store, remoteAddress));
            sessions.put(peerEmail,ctx);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UntrustedIdentityException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String peer, String plaintext)
    {
        return Base64.encodeBytes(sessions.get(peer).getSessionCipher().encrypt(plaintext.getBytes()).serialize());
    }

    public byte[] decryptPreKeyMessage(String peer, String ciphertext)
    {
        byte[] plaintext = null;

        try {
            plaintext =  sessions.get(peer).getSessionCipher()
                    .decrypt(new PreKeyWhisperMessage(Base64.decode(ciphertext)));
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


    public String decrypt(String peer, String ciphertext)
    {
        byte[] plaintext = null;

        try {

            //Try to parse it as WhisperMessage
            plaintext = sessions.get(peer).getSessionCipher()
                    .decrypt(new WhisperMessage(Base64.decode(ciphertext)));

        } catch (InvalidMessageException e) {

            //We failed to parse it as WhisperMessage, maybe its PPreKeyWhisperMessage
            plaintext = decryptPreKeyMessage(peer, ciphertext);

        } catch (LegacyMessageException e) {
            e.printStackTrace();
        } catch (DuplicateMessageException e) {
            e.printStackTrace();
        } catch (NoSessionException e) {
            e.printStackTrace();
        }

        return new String(plaintext);
    }
}
