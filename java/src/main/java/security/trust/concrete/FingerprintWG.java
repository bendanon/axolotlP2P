package security.trust.concrete;

import org.whispersystems.libaxolotl.IdentityKey;
import security.trust.IIdentityWitness;
import security.trust.IWitnessGenerator;

/**
 * Created by ben on 08/12/15.
 *
 * Concrete IWitnessGenerator that produces a fingerprint as a witness
 * for public identity key authenticity
 *
 */
public class FingerprintWG implements IWitnessGenerator {
    @Override
    public IIdentityWitness generateWitness(IdentityKey identityKey) {
        //We ignore the first 3 bytes which are constant for implementation reasons
        return new FingerprintWitness(identityKey.getFingerprint().substring(3));
    }
}
