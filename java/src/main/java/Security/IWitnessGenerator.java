package Security;

import org.whispersystems.libaxolotl.IdentityKey;

/**
 * Created by ben on 08/12/15.
 */
public interface IWitnessGenerator {
    IIdentityWitness generateWitness(IdentityKey identityKey);
}
