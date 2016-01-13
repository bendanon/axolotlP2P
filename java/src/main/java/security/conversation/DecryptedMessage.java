package security.conversation;

/**
 * Created by ben on 01/01/16.
 */
public class DecryptedMessage {


    private int messageIndex;
    private String content;

    public DecryptedMessage(int messageIndex, String content) {
        this.messageIndex = messageIndex;
        this.content = content;

    }
    public int getMessageIndex() {
        return messageIndex;
    }
    public String getContent() {
        return content;
    }



}
