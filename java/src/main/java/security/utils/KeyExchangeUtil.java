package security.utils;

import org.jivesoftware.smack.util.Base64;
import org.whispersystems.libaxolotl.IdentityKey;
import org.whispersystems.libaxolotl.InvalidKeyException;
import org.whispersystems.libaxolotl.ecc.ECPublicKey;
import org.whispersystems.libaxolotl.state.PreKeyBundle;

import java.nio.ByteBuffer;

/**
 * Created by ben on 04/12/15.
 */
public class KeyExchangeUtil {
    static final int KEY_EXCH_MSG_SIZE = 200;
    static final int SIGNATURE_SIZE = 64;
    static final int SERIALIZED_EC_PUBLIC_SIZE = 33;

    public static String serialize(PreKeyBundle bundle)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(KEY_EXCH_MSG_SIZE);

        //Client identification
        byteBuffer.putInt(bundle.getRegistrationId());
        byteBuffer.putInt(bundle.getDeviceId());

        //Ephemeral key
        byteBuffer.putInt(bundle.getPreKeyId());
        byteBuffer.put(bundle.getPreKey().serialize());

        //Signed prekey
        byteBuffer.putInt(bundle.getSignedPreKeyId());
        byteBuffer.put(bundle.getSignedPreKey().serialize());
        byteBuffer.put(bundle.getSignedPreKeySignature());

        //Identity key
        byteBuffer.put(bundle.getIdentityKey().serialize());

        return Base64.encodeBytes(byteBuffer.array());
    }

    public static PreKeyBundle deserialize(String base64SerializedBundle)
    {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.decode(base64SerializedBundle));

        //Client Identification
        int registrationId = byteBuffer.getInt();
        int deviceId = byteBuffer.getInt();

        //Ephemeral key
        int preKeyId = byteBuffer.getInt();
        ECPublicKey preKeyPublic = getECPublicKey(byteBuffer);

        //Signed prekey
        int signedPreKeyId = byteBuffer.getInt();
        ECPublicKey signedPreKeyPublic = getECPublicKey(byteBuffer);
        byte[] signedPreKeySignature = new byte[SIGNATURE_SIZE];
        byteBuffer.get(signedPreKeySignature);

        //Identity key
        IdentityKey identityKey = getIdentityKey(byteBuffer);

        return new PreKeyBundle(registrationId,deviceId,preKeyId,preKeyPublic,
                   signedPreKeyId,signedPreKeyPublic,signedPreKeySignature,identityKey);
    }

    private static ECPublicKey getECPublicKey(ByteBuffer byteBuffer)
    {
        ECPublicKey preKeyPublic = null;
        try {
            preKeyPublic = new IdentityKey(byteBuffer.array(), byteBuffer.position()).getPublicKey();
            byteBuffer.position(byteBuffer.position()+SERIALIZED_EC_PUBLIC_SIZE);
            return preKeyPublic;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return preKeyPublic;
    }

    private static IdentityKey getIdentityKey(ByteBuffer byteBuffer)
    {
        IdentityKey identityKey = null;
        try {
            identityKey = new IdentityKey(byteBuffer.array(), byteBuffer.position());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return identityKey;
    }
}
