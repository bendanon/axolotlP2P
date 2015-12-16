package security.trust;

import org.whispersystems.libaxolotl.IdentityKey;
import org.whispersystems.libaxolotl.IdentityKeyPair;
import org.whispersystems.libaxolotl.InvalidKeyException;
import org.whispersystems.libaxolotl.UntrustedIdentityException;
import org.whispersystems.libaxolotl.ecc.ECPublicKey;
import org.whispersystems.libaxolotl.state.IdentityKeyStore;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

/**
 * The trust store should contain:
 * 1.My identity key pair
 * 2.All trusted public identity keys of peers
 */
public interface ITrustStore {
    /**
     * Sets the owner's identity in this store
     * @param pair
     */
    void setIdentity(IdentityKeyPair pair) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException;

    /**
     * Retrive the identity key pair of the ITrustStore owner
     * @return
     */
    IdentityKeyPair getIdentity() throws KeyStoreException, InvalidKeyException, UnrecoverableEntryException, NoSuchAlgorithmException;

    /**
     * Inserts the public key of a trusted identity.
     *
     * IMPORTANT - Only call this when the user is aware and provided a witness
     * for the authenticity of pub and its link to the peer
     *
     * @param peer - The trusted peer
     * @param pub - Its related public identity key
     */
    void setTrustedIdentity(String peer, IdentityKey pub) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException;

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
    boolean isTrusted(String peer, ECPublicKey pub) throws UntrustedIdentityException, KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException;

}
