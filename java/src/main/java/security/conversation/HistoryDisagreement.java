package security.conversation;

/**
 * Created by ben on 01/01/16.
 */
public class HistoryDisagreement {
    private String peerName;
    private int index;
    private int confirmedIndex;

    public HistoryDisagreement(String peerName, int index, int lastChainIndex) {
        this.peerName = peerName;
        this.index = index;
        this.confirmedIndex = lastChainIndex;
    }

    public String getPeerName() {
        return peerName;
    }

    public int getIndex() {
        return index;
    }

    public int getConfirmedIndex() {
        return confirmedIndex;
    }
}
