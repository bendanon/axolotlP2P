package Security;

import org.whispersystems.libaxolotl.IdentityKey;

import org.whispersystems.libaxolotl.SessionCipher;


/**
 * Created by ben on 04/12/15.
 */
public class SecureSessionContext {
    private SessionCipher cipher;
    private IdentityKey sessionIdentityKey;


    public SecureSessionContext()
    {
        cipher = null;
    }

    public void setSessionCipher(SessionCipher cipher)
    {
        this.cipher = cipher;
    }
    public SessionCipher getSessionCipher()
    {
        return cipher;
    }

    public void setSessionIdentityKey(IdentityKey sessionIdentityKey) {
        this.sessionIdentityKey = sessionIdentityKey;
    }

    public IdentityKey getSessionIdentityKey() {
        return sessionIdentityKey;
    }
}
