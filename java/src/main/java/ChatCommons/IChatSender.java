package ChatCommons;

import org.jivesoftware.smack.XMPPException;

/**
 * Created by ben on 09/12/15.
 */
public interface IChatSender {

    void sendMessage(String peer, String Message);
}
