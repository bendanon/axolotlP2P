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
        System.out.println("*****getting" + messageIndex + "****");
        for(MessageRecord record : records) {
            System.out.print(record.getMessageIndex() + ",");
        }
        System.out.println("--->");
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


        for(MessageRecord record : records) {
            System.out.print(record.getMessageIndex() + ",");
        }
        System.out.println();
        System.out.println("*****");
    }

    private int reCalcLastChainIndex() {
        for(int i = 0 ; i < records.size(); i++)
        {
            if(records.get(i).getMessageIndex() != i)  { return i-1; }
        }
        return records.size() - 1;
    }

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

    public MessageRecord getLastChainRecord()
    {
        return records.get(lastChainIndex);
    }

    public MessageRecord getLastRecord()
    {
        return records.get(records.size()-1);
    }

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
