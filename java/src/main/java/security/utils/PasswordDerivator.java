package security.utils;

import org.jivesoftware.smack.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ben on 09/12/15.
 */
public class PasswordDerivator {
    private MessageDigest digest ;
    private String password;
    public PasswordDerivator(String hashAlgorithm, String password) throws NoSuchAlgorithmException {
        digest = MessageDigest.getInstance("SHA-256");
        this.password = password;
    }

    public String getPasswordDerivative(int index)
    {
        return Base64.encodeBytes(digest.digest(String.format("%s%d",password,index).getBytes()));
    }
}
