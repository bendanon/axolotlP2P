package Security;

import org.whispersystems.libaxolotl.IdentityKey;
import org.whispersystems.libaxolotl.IdentityKeyPair;
import org.whispersystems.libaxolotl.UntrustedIdentityException;
import org.whispersystems.libaxolotl.ecc.ECPublicKey;

/**
 * The trust store should contain:
 * 1.My identity key pair
 * 2.My signed pre-key
 * 3.All public identity keys of peers from previous conversations
 */
public interface ITrustStore {

    /**
     * Sets my identity in this store
     * @param pair
     */
    void setIdentity(IdentityKeyPair pair);

    IdentityKeyPair getIdentity();

    void setTrustedIdentity(String peer, IdentityKey pub);

    boolean isTrusted(String peer, ECPublicKey pub) throws UntrustedIdentityException;

}
