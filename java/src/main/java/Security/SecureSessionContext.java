package Security;

import org.whispersystems.libaxolotl.IdentityKey;

import org.whispersystems.libaxolotl.SessionCipher;


/**
 * Created by ben on 04/12/15.
 */
public class SecureSessionContext {
    private SessionCipher cipher;
    private IdentityKey sessionIdentityKey;

    public SecureSessionContext(SessionCipher cipher, IdentityKey sessionIdentityKey)
    {
        this.cipher = cipher;
        this.sessionIdentityKey = sessionIdentityKey;
    }

    public SessionCipher getSessionCipher() {
        return cipher;
    }

    public IdentityKey getSessionIdentityKey() {
        return sessionIdentityKey;
    }
}
