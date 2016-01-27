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

    private MessageRecord(String plain, int messageIndex, byte[] hashOnHistory, String peer)
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

    /**
     * Deeply compares repliedMessageRecord with this message record.
     * Use this method to make sure the conversations match
     * @param repliedMessageRecord
     * @return
     */
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

    /**
     * Generates the next message record, according to this one.
     * This message is required for creating the next since hashOnHistory
     * is built upon the existing conversation
     * @param plaintext
     * @param messageIndex
     * @return
     */
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
