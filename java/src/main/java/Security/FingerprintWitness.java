package Security;

import org.whispersystems.libaxolotl.IdentityKey;

/**
 * Created by ben on 08/12/15.
 */
public class FingerprintWitness implements IIdentityWitness {
    private String fingerprint;
    public FingerprintWitness(String fingerprint)
    {
        this.fingerprint = fingerprint;
    }

    @Override
    public boolean validate(IdentityKey sessionIdentityKey) {
        return fingerprint.equals(sessionIdentityKey.getFingerprint());
    }
}
