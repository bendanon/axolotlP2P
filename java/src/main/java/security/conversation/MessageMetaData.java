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
    private static final int META_TRAILER_SIZE = META_TRAILER.length();
    private static final int META_UNIT_SIZE = DigestUtil.HASH_SIZE +
            INDEX_SIZE + RepliedMessageRecord.SERIALIZED_NAME_SIZE;

    private final ByteBuffer serializedMetadata;

    private MessageMetaData(ByteBuffer serializedMetadata)
    {
        this.serializedMetadata = serializedMetadata;
    }
    public static synchronized MessageMetaData wrap(ByteBuffer serializedMetadata)
    {
        return new MessageMetaData(serializedMetadata);
    }

    public boolean hasNext()
    {
        return serializedMetadata.remaining() > 0;
    }

    public RepliedMessageRecord next()
    {
        RepliedMessageRecord record = new RepliedMessageRecord();
        record.deserialize(serializedMetadata);
        return record;
    }

    public static int calculateSerializedMetaSize(int peers)
    {
        return INDEX_SIZE + (peers* META_UNIT_SIZE);
    }

    public static String createMessageMetadata(int messageIndex, Map<String, MessageHistory> history, List<String> peers) {
        ByteBuffer buffer = ByteBuffer.allocate(calculateSerializedMetaSize(peers.size()));

        buffer.putInt(messageIndex);

        for(String peer : peers)
        {
            RepliedMessageRecord record = history.get(peer).getLastChainRecord().toRepliedMessageRecord();
            record.serialize(buffer);
        }

        return Base64.encodeBytes(buffer.array());
    }

}
