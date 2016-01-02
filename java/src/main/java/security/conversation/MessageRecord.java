package security.conversation;

import security.utils.DigestUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by ben on 30/12/15.
 */
public class MessageRecord {

    private int messageIndex;
    private byte[] chainHash;
    private String peer;
    private String messageContent;

    public MessageRecord(String peer)
    {
        this.chainHash = new byte[DigestUtil.HASH_SIZE];
        this.peer = peer;
    }

    public MessageRecord(String plain, int messageIndex, byte[] hashOnHistory, String peer)
    {
        this.messageIndex = messageIndex;
        this.chainHash = hashOnHistory;
        this.messageContent = plain;
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

    public boolean compare(RepliedMessageRecord repliedMessageRecord) {

        return repliedMessageRecord.getMessageIndex() == this.getMessageIndex() &&
                Arrays.equals(repliedMessageRecord.getChainHash(), chainHash);
    }

    public String getPeer() {
        return peer;
    }

    public RepliedMessageRecord toRepliedMessageRecord()
    {
        return new RepliedMessageRecord(messageIndex, chainHash, peer);
    }

    public MessageRecord nextRecord(String plaintext, int messageIndex)
    {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[plaintext.length() + chainHash.length]);
        buffer.put(plaintext.getBytes());
        buffer.put(chainHash);
        return new MessageRecord(plaintext, messageIndex, DigestUtil.digest(buffer.array()), peer);
    }

    public String getMessageContent() {
        return messageContent;
    }
}
