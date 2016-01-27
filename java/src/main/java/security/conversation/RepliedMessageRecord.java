package security.conversation;

import security.utils.DigestUtil;

import java.nio.ByteBuffer;

/**
 * Created by ben on 01/01/16.
 */
public class RepliedMessageRecord {
    private static final String PEER_NAME_SEPARATOR = "\n";
    public static final int SERIALIZED_NAME_SIZE = 20 + PEER_NAME_SEPARATOR.length();


    private int messageIndex;
    private byte[] chainHash;
    private String peer;

    public RepliedMessageRecord()
    {
        this.chainHash = new byte[DigestUtil.HASH_SIZE];
    }
    public RepliedMessageRecord(int messageIndex, byte[] hashOnHistory, String peer)
    {
        this.messageIndex = messageIndex;
        this.chainHash = hashOnHistory;
        this.peer = peer;
    }

    public int getMessageIndex()
    {
        return messageIndex;
    }

    public byte[] getChainHash()
    {
        return chainHash;
    }

    public String getRepliedPeer() {
        return peer;
    }

    /**
     * Serializes this message record into buffer.
     * @param buffer
     */
    public void serialize(ByteBuffer buffer)
    {
        //put the first field - peer name, add separator
        buffer.put(peer.getBytes());
        buffer.put(PEER_NAME_SEPARATOR.getBytes());

        //pad the peer name to SERIALIZED_NAME_SIZE
        byte[] padding = new byte[SERIALIZED_NAME_SIZE - (peer.length() + PEER_NAME_SEPARATOR.length())];
        buffer.put(padding);

        //put the second field, the hash
        buffer.put(chainHash);

        //put the third field, messageIndex
        buffer.putInt(messageIndex);
    }

    /**
     * Deserializes the message record from buffer
     * @param buffer
     */
    public void deserialize(ByteBuffer buffer)
    {
        byte[] paddedName = new byte[SERIALIZED_NAME_SIZE];

        //get first field - peer name
        buffer.get(paddedName);

        //Clear the padding and separator
        peer = new String(paddedName).split(PEER_NAME_SEPARATOR)[0];

        //get the second field - the hash on history
        buffer.get(chainHash);

        //get the third field - message index
        messageIndex = buffer.getInt();
    }
}
