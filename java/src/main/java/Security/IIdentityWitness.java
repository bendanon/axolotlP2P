package Security;

import org.whispersystems.libaxolotl.IdentityKey;

/**
 * Created by ben on 08/12/15.
 */
public interface IIdentityWitness {

    /**
     * Validates the given IdentityKey matches the data of the witness
     * @param sessionIdentityKey
     * @return
     */
    boolean validate(IdentityKey sessionIdentityKey);
}
