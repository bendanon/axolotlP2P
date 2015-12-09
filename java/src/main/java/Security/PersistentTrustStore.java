package Security;

import org.whispersystems.libaxolotl.IdentityKey;
import org.whispersystems.libaxolotl.IdentityKeyPair;
import org.whispersystems.libaxolotl.InvalidKeyException;
import org.whispersystems.libaxolotl.UntrustedIdentityException;
import org.whispersystems.libaxolotl.ecc.ECPublicKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

/**
 * Created by ben on 08/12/15.
 */
public class PersistentTrustStore implements ITrustStore{

    private final KeyStore ks;
    private final KeyStore.ProtectionParameter protParam;
    private String ksPassword;
    private String ksPath;


    public PersistentTrustStore(String path, String password) throws KeyStoreException {

        this.ks = KeyStore.getInstance("JCEKS");
        this.ksPassword = password;
        protParam = new KeyStore.PasswordProtection(ksPassword.toCharArray());
        this.ksPath = path;
        initializeKeyStore();
    }

    private void initializeKeyStore()
    {
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(ksPath);
            ks.load(fis, ksPassword.toCharArray());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {

            //This means the store does not yet exist, create one
            try {
                ks.load(null, ksPassword.toCharArray());
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            } catch (CertificateException e1) {
                e1.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void setIdentity(IdentityKeyPair pair) {
        storeKey("identity", pair.serialize());
    }

    @Override
    public IdentityKeyPair getIdentity() {

        try {
            if(ks.containsAlias("identity"))
            {
                KeyStore.SecretKeyEntry entry =
                        (KeyStore.SecretKeyEntry)ks.getEntry("identity", protParam);

                return new IdentityKeyPair(entry.getSecretKey().getEncoded());
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setTrustedIdentity(String peer, IdentityKey pub) {

        //make sure the peer doesn't insert itself as identity :)
        if(peer.equals("identity")) return;

        storeKey(peer, pub.serialize());
    }

    @Override
    public boolean isTrusted(String peer, ECPublicKey pub) throws UntrustedIdentityException{

        try {
            if(!ks.containsAlias(peer))
            {
                return false;
            }
            KeyStore.SecretKeyEntry entry =
                    (KeyStore.SecretKeyEntry)ks.getEntry(peer, protParam);

            if(!java.util.Arrays.equals(pub.serialize(), entry.getSecretKey().getEncoded()))
            {
                throw new UntrustedIdentityException(peer, new IdentityKey(pub));
            }

            return true;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void storeKey(String alias, byte[] serialized)
    {
        SecretKey sk = new SecretKeySpec(serialized, "");
        java.io.FileOutputStream fos = null;
        try {
            ks.setEntry(alias , (new KeyStore.SecretKeyEntry(sk)), protParam);
            fos = new java.io.FileOutputStream(ksPath);
            ks.store(fos, ksPassword.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
