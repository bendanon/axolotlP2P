package Security;

import org.whispersystems.libaxolotl.SessionBuilder;
import org.whispersystems.libaxolotl.SessionCipher;
import org.whispersystems.libaxolotl.ecc.ECKeyPair;

/**
 * Created by ben on 04/12/15.
 */
public class SecureSessionContext {
    private SessionCipher cipher;

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
}
