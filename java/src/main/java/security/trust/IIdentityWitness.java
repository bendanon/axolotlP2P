package security.trust;

import org.whispersystems.libaxolotl.IdentityKey;

/**
 * Created by ben on 08/12/15.
 *
 * General interface for public identity key authentication
 * made for establishing trust with a peer
 *
 */
public interface IIdentityWitness {

    /**
     * Authenticates the given IdentityKey matches the data of the witness
     * @param sessionIdentityKey
     * @return
     */
    boolean authenticate(IdentityKey sessionIdentityKey);

    /**
     * Makes a displayable form of the witness witness
     * @return the string representing the fingerprint
     */
    String toString();
}
