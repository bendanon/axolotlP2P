package security.management;

import ChatCommons.IChatSender;

import java.util.List;

/**
 * Created by ben on 09/12/15.
 */
public class ChatGroup {

    private List<String> peers;
    private String groupName;
    private SecureParty secureParty;
    private IChatSender sender;

    public ChatGroup(SecureParty secureParty, String groupName, IChatSender sender)
    {
        this.groupName = groupName;
        this.secureParty = secureParty;
        this.sender = sender;
    }

    public boolean addPeer(String peer)
    {
        if(!secureParty.isSessionInitialized(peer))
        {
            return false;
        }

        peers.add(peer);
        return true;
    }

    public void removePeer(String peer)
    { peers.remove(peer);  }

    public void sendMessage (String message)
    {
        for(String peer : peers)
        {
            sender.sendMessage(peer, secureParty.encrypt(peer, message));
        }
    }
}
