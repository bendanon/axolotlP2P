package security.conversation;

import org.jivesoftware.smack.util.Base64;
import security.management.SecureParty;
import security.utils.DigestUtil;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ben on 01/01/16.
 */
public class MessageMetaData {
    private static final int INDEX_SIZE = 4; //int
    public static final String META_TRAILER = "</meta>";
    private static final int META_UNIT_SIZE = DigestUtil.HASH_SIZE +
            INDEX_SIZE + RepliedMessageRecord.SERIALIZED_NAME_SIZE;

    private final ByteBuffer serializedMetadata;

    /**
     * Private CTOR, use only wrap function to create a MessageMetaData instance
     * @param serializedMetadata
     */
    private MessageMetaData(ByteBuffer serializedMetadata)
    {
        this.serializedMetadata = serializedMetadata;
    }

    /**
     * Wraps a buffer of serialized metadata for serialization
     * @param serializedMetadata
     * @return
     */
    public static synchronized MessageMetaData wrap(ByteBuffer serializedMetadata)
    {
        return new MessageMetaData(serializedMetadata);
    }

    /**
     * Verifies there are still serialized records in the internal buffer
     * @return
     */
    public boolean hasNext()
    {
        return serializedMetadata.remaining() > 0;
    }

    /**
     * Deserializes the next serialized record on the internal buffer
     * @return
     */
    public RepliedMessageRecord next()
    {
        RepliedMessageRecord record = new RepliedMessageRecord();
        record.deserialize(serializedMetadata);
        return record;
    }

    /**
     * Calculates the space required for storing serialized metadata for peers amount
     * @param peers
     * @return
     */
    public static int calculateSerializedMetaSize(int peers)
    {
        return INDEX_SIZE + (peers* META_UNIT_SIZE);
    }

    /**
     * Creates a message metadata based on the last message record from every peer in history
     * @param messageIndex
     * @param history
     * @return
     */
    public static String createMessageMetadata(int messageIndex, Map<String, MessageHistory> history) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateSerializedMetaSize(history.keySet().size()));

        buffer.putInt(messageIndex);

        //Get the last record for every peer
        for(String peer : history.keySet())
        {
            RepliedMessageRecord record = history.get(peer).getLastRecord().toRepliedMessageRecord();
            record.serialize(buffer);
        }

        return Base64.encodeBytes(buffer.array());
    }

}
