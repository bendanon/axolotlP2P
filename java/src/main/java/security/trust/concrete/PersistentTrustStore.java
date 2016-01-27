package security.trust.concrete;

import org.whispersystems.libaxolotl.IdentityKey;
import org.whispersystems.libaxolotl.IdentityKeyPair;
import org.whispersystems.libaxolotl.InvalidKeyException;
import org.whispersystems.libaxolotl.UntrustedIdentityException;
import org.whispersystems.libaxolotl.ecc.ECPublicKey;
import org.whispersystems.libaxolotl.state.IdentityKeyStore;
import security.trust.ITrustStore;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Enumeration;

/**
 * Created by ben on 08/12/15.
 */
public class PersistentTrustStore implements ITrustStore {

    private final KeyStore keyStore;
    private final KeyStore.ProtectionParameter protParam;
    private String ksPassword;
    private String ksPath;
    private boolean createNewStore;

    public PersistentTrustStore(String path, String password, boolean createNewStore) throws KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException {

        this.keyStore = KeyStore.getInstance("JCEKS");
        this.ksPassword = password;
        this.createNewStore = createNewStore;
        protParam = new KeyStore.PasswordProtection(ksPassword.toCharArray());
        this.ksPath = path;
        initializeKeyStore();
    }

    private void initializeKeyStore() throws IOException, CertificateException, NoSuchAlgorithmException {

        if (this.createNewStore) {

            //Creates a new key store
            keyStore.load(null, ksPassword.toCharArray());

        } else {

            //Load an existing key store
            java.io.FileInputStream fis = new java.io.FileInputStream(ksPath);
            keyStore.load(fis, ksPassword.toCharArray());
            fis.close();
        }
    }

    /**
     * Sets the owner's identity in this store
     * @param pair
     */
    @Override
    public void setIdentity(IdentityKeyPair pair) throws CertificateException,
                                                         NoSuchAlgorithmException,
                                                         KeyStoreException,
                                                         IOException {
        storeKey("identity", pair.serialize());
    }

    /**
     * Retrive the identity key pair of the ITrustStore owner
     * @return
     */
    @Override
    public IdentityKeyPair getIdentity() throws KeyStoreException,
            InvalidKeyException, UnrecoverableEntryException, NoSuchAlgorithmException {

        KeyStore.SecretKeyEntry entry =
                (KeyStore.SecretKeyEntry) keyStore.getEntry("identity", protParam);

        if(null == entry)
        {
            return null;
        }

        return new IdentityKeyPair(entry.getSecretKey().getEncoded());
    }

    /**
     * Inserts the public key of a trusted identity.
     *
     * IMPORTANT - Only call this when the user is aware and provided a witness
     * for the authenticity of pub and its link to the peer
     *
     * @param peer - The trusted peer
     * @param pub - Its related public identity key
     */
    @Override
    public void setTrustedIdentity(String peer, IdentityKey pub) throws CertificateException,
                                                                        NoSuchAlgorithmException,
                                                                        KeyStoreException,
                                                                        IOException {

        //make sure the peer doesn't insert itself as identity :)
        if(peer.equals("identity")) return;

        storeKey(peer, pub.serialize());
    }

    /**
     * Meant to be used in case peer notifies the user that his device was stolen /
     * private identity key lost. Calling this removed the peer from trust store
     *
     * @param peer
     */
    @Override
    public void RevokeTrustedIdentity(String peer) throws KeyStoreException {
        if(keyStore.containsAlias(peer))
        {
            keyStore.deleteEntry(peer);
        }
    }

    /**
     * Check if peer is trusted with this public key
     *
     * @param peer - The peer in question
     * @param pub - The public key the peer provided for the session
     * @return
     * @throws UntrustedIdentityException - In case the peer is trusted with another identity key -
     * this means someone is trying to steal peer's identity!
     *
     */
    @Override
    public boolean isTrusted(String peer, ECPublicKey pub) throws UntrustedIdentityException,
                                                                  KeyStoreException,
                                                                  UnrecoverableEntryException,
                                                                  NoSuchAlgorithmException {

        if(!keyStore.containsAlias(peer))
        {
            return false;
        }
        KeyStore.SecretKeyEntry entry =
                (KeyStore.SecretKeyEntry) keyStore.getEntry(peer, protParam);

        if(!java.util.Arrays.equals(pub.serialize(), entry.getSecretKey().getEncoded()))
        {
            throw new UntrustedIdentityException(peer, new IdentityKey(pub));
        }

        return true;
    }

    /**
     *
     * @param alias
     * @param serialized
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private void storeKey(String alias, byte[] serialized) throws KeyStoreException,
                                                                    CertificateException,
                                                                    NoSuchAlgorithmException,
                                                                    IOException {
        SecretKey secretKey = new SecretKeySpec(serialized, "");
        keyStore.setEntry(alias , (new KeyStore.SecretKeyEntry(secretKey)), protParam);

        java.io.FileOutputStream fos = new java.io.FileOutputStream(ksPath);
        keyStore.store(fos, ksPassword.toCharArray());
        fos.close();
    }
}
