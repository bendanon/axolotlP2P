package security.utils;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import org.jivesoftware.smack.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ben on 16/12/15.
 */
public class StrongPassword {
    static final String SALT="P2PAXOLOTOLCHAT";
    static final int ITERATIONS=1000;
    static final int PASSWORD_LENGTH_BITS =512;
    static final int BYTE_SIZE=8;
    static final String ALGORITHM="PBKDF2WithHmacSHA1";

    /**
     * generates amount strong passwords from password
     * @param password
     * @param amount
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static List<String> generate(String password, int amount) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        ArrayList<String> list = new ArrayList<>();
        char[] chars = password.toCharArray();
        byte[] salt = SALT.getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, PASSWORD_LENGTH_BITS*amount);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = skf.generateSecret(spec).getEncoded();
        ByteBuffer byteBuffer = ByteBuffer.wrap(hash);

        for(int i = 0 ; i < amount; i++) {
            byte[] singlePassword = new byte[PASSWORD_LENGTH_BITS / BYTE_SIZE];
            byteBuffer.get(singlePassword, 0, PASSWORD_LENGTH_BITS / BYTE_SIZE);
            list.add(Base64.encodeBytes(singlePassword, 0, PASSWORD_LENGTH_BITS / BYTE_SIZE));
        }

        return list;
    }
}
