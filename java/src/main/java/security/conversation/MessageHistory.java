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

        //We use a default first entry for implementation reasons
        MessageRecord first = new MessageRecord(peer);
        records.add(first);
    }

    /**
     * Inserts the new message into its ordered place in history
     * @param plaintext the plain text of the message
     * @param messageIndex the index of that message
     */
    public void insert(String plaintext, int messageIndex)
    {
        int listIndexForNewMessage = 0;
        for(MessageRecord record : records)
        {
            //This means we already have this message
            if(record.getMessageIndex() == messageIndex) { return; }

            //This means listIndexForNewMessage now contains the correct list location for the new message
            if(record.getMessageIndex() > messageIndex) { break; }

            listIndexForNewMessage++;
        }

        //Add the new record to the list
        records.add(listIndexForNewMessage, records.get(listIndexForNewMessage-1).nextRecord(plaintext,messageIndex));

        //Re-calculate the hashes from the new record (NOT inclusive)
        //until the end of the list
        reCalcHashes(listIndexForNewMessage);

        lastChainIndex = reCalcLastChainIndex();
    }

    /**
     * Updates the last chain index.
     * Shoud be used after repairing the chain
     * @return The new last chain index
     */
    private int reCalcLastChainIndex() {
        for(int i = 0 ; i < records.size(); i++)
        {
            if(records.get(i).getMessageIndex() != i)  { return i-1; }
        }
        return records.size() - 1;
    }

    /**
     * Re-calculates the hashes from prevIndex to the end.
     * Should be used for repairing the conversation history
     * @param prevIndex The index after which the method starts
     */
    private void reCalcHashes(int prevIndex)
    {
        MessageRecord prev = records.get(prevIndex);

        for(int i = prevIndex+1; i < records.size(); i++)
        {
            //Get the record to be re-calculated
            MessageRecord current = records.get(i);

            //Re-calculate the current record and add it in the same index
            records.set(i,prev.nextRecord(current.getMessageContent(), current.getMessageIndex()));

            //Advance prev
            prev = current;
        }
    }

    /**
     * @return the last index of the chain prefix before missing messages
     */
    public MessageRecord getLastChainRecord()
    {
        return records.get(lastChainIndex);
    }

    /**
     * @return the last record in the list
     */
    public MessageRecord getLastRecord()
    {
        return records.get(records.size()-1);
    }

    /**
     * Verifies if the last record some peer knows is consistent some prefix
     * of the history chain this history instance holds
     * @param hisLastRecord the last record held by the peer
     * @return true if consistent
     */
    public boolean isConsistentWithChain(RepliedMessageRecord hisLastRecord) {
        for(MessageRecord record : records)
        {
            if(record.compare(hisLastRecord))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param index desired index value
     * @return the message record with specified message index value
     */
    public MessageRecord getIndex(int index)
    {
        for(MessageRecord record : records)
        {
            if(record.getMessageIndex() == index)
            {
                return record;
            }
        }
        return null;
    }

}
