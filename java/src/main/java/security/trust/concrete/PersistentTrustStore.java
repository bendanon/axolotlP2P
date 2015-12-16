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


    @Override
    public void syncIdentityKeystore(IdentityKeyStore identityKeyStore) throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, InvalidKeyException {
        Enumeration <String> iter = keyStore.aliases();
        while(iter.hasMoreElements())
        {
            String alias = iter.nextElement();

            if(alias.equals("identity")) continue;

            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, protParam);
            identityKeyStore.saveIdentity(alias, new IdentityKey(entry.getSecretKey().getEncoded(), 0));
        }
    }

    @Override
    public void setIdentity(IdentityKeyPair pair) throws CertificateException,
                                                         NoSuchAlgorithmException,
                                                         KeyStoreException,
                                                         IOException {
        storeKey("identity", pair.serialize());
    }

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

    @Override
    public void setTrustedIdentity(String peer, IdentityKey pub) throws CertificateException,
                                                                        NoSuchAlgorithmException,
                                                                        KeyStoreException,
                                                                        IOException {

        //make sure the peer doesn't insert itself as identity :)
        if(peer.equals("identity")) return;

        storeKey(peer, pub.serialize());
    }

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
