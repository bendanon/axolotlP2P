package security.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ben on 01/01/16.
 */
public class DigestUtil {

    public static final int HASH_SIZE = 32;
    public static final String DIGEST_ALGORITHM = "SHA-256";

    /**
     * Uses the DIGEST_ALGORITHM to digest the input.
     * Returns the digestes result of size HASH_SIZE
     * @param input
     * @return
     */
    public static synchronized byte[] digest(byte[] input)
    {
        byte[] digested = null;
        try {
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
            digested = md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digested;
    }
}
