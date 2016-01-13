package security.conversation;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by ben on 30/12/15.
 */
public class DecryptedPackage {
    private String peer;
    private List<HistoryDisagreement> hdList;
    private DecryptedMessage decryptedMessage;
    private int lastChainIndex;

    public DecryptedPackage(String peer, DecryptedMessage message,
                            int lastChainIndex, List<HistoryDisagreement> hdList) {
       this.peer = peer;
       this.decryptedMessage = message;
       this.hdList = hdList;
       this.lastChainIndex = lastChainIndex;
    }

    public String getContent() {
        return decryptedMessage.getContent();
    }

    public ListIterator<HistoryDisagreement> getHistoryDisagreementIterator()
    {
        return hdList.listIterator();
    }

    public int getLastChainIndex()
    {
        return lastChainIndex;
    }


    public int getIndex() {
        return decryptedMessage.getMessageIndex();
    }
}
