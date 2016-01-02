package security.conversation;

/**
 * Created by ben on 01/01/16.
 */
public class HistoryDisagreement {
    private String peerName;
    private int index;
    private int lastChainIndex;

    public HistoryDisagreement(String peerName, int index, int lastChainIndex) {
        this.peerName = peerName;
        this.index = index;
        this.lastChainIndex = lastChainIndex;
    }

    public String getPeerName() {
        return peerName;
    }

    public int getIndex() {
        return index;
    }

    public int getLastChainIndex() {
        return lastChainIndex;
    }
}
