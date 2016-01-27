package security.conversation;

/**
 * Created by ben on 01/01/16.
 */
public class HistoryDisagreement {
    private String peerName;
    private int lastIndexPeerSaw;
    private boolean isConsistentWithChain;


    public HistoryDisagreement(String peerName, int lastIndexPeerSaw, boolean isConsistentWithChain) {

        this.peerName = peerName;
        this.lastIndexPeerSaw = lastIndexPeerSaw;
        this.isConsistentWithChain = isConsistentWithChain;
    }

    public String getPeerName() {
        return peerName;
    }

    public int getLastIndexPeerSaw() {
        return lastIndexPeerSaw;
    }

    public boolean isConsistentWithChain() {
        return isConsistentWithChain;
    }
}
