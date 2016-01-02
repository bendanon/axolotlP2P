package security.conversation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ben on 01/01/16.
 */
public class MessageHistory {

    private final List<MessageRecord> records;
    private int lastChainIndex;

    public MessageHistory(String peer)
    {
        records = new ArrayList<>();
        lastChainIndex = 0;
        MessageRecord first = new MessageRecord(peer);
        records.add(first);
    }

    public void insert(String plaintext, int messageIndex)
    {
        if(messageIndex == lastChainIndex+1)
        {
            lastChainIndex++;
        }

        MessageRecord next = records.get(records.size()-1).nextRecord(plaintext, messageIndex);
        records.add(next);
    }

    public MessageRecord getLastChainRecord()
    {
        return records.get(lastChainIndex);
    }

}
