package Security;

import org.whispersystems.libaxolotl.IdentityKey;

/**
 * Created by ben on 08/12/15.
 *
 * General interface for witness generators.
 * Its job is to generate a witness based on the identity key provided.
 * Concrete classes will provide the algorithm.
 *
 */
public interface IWitnessGenerator {
    IIdentityWitness generateWitness(IdentityKey identityKey);
}
