package security.management;

import ChatCommons.IChatSender;
import org.whispersystems.libaxolotl.*;
import security.conversation.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ben on 09/12/15.
 */
public class SecureConversation {

    private final Map<String, MessageHistory> conversationHistory;
    private final List<String> peers;
    private final SecureParty secureParty;
    private final IChatSender sender;

    public SecureConversation(SecureParty secureParty, IChatSender sender)
    {
        this.secureParty = secureParty;
        this.sender = sender;
        this.conversationHistory = new HashMap<>();
        peers = new ArrayList<>();
        conversationHistory.put(secureParty.getOwner(), new MessageHistory(secureParty.getOwner()));
    }

    public boolean addPeer(String peer)
    {
        if(!secureParty.isSessionInitialized(peer))
        {
            return false;
        }
        conversationHistory.put(peer, new MessageHistory(peer));
        peers.add(peer);

        return true;
    }

    public void removePeer(String peer)
    {
        conversationHistory.remove(peer);
        peers.remove(peer);
    }

    public void sendMessage (String message)
    {
        MessageHistory myHistory = conversationHistory.get(secureParty.getOwner());
        int index = myHistory.getLastChainRecord().getMessageIndex() + 1;

        String metadata =
                MessageMetaData.createMessageMetadata(index, conversationHistory, peers);

        String fullMessage = metadata + message;

        for(String peer : peers)
        {
            sender.sendMessage(peer, secureParty.encrypt(peer, fullMessage));
        }

        myHistory.insert(message, index);
    }

    public DecryptedPackage receiveMessage (String peer, String ciphertext)
            throws InvalidKeyIdException, NoSessionException, LegacyMessageException,
            InvalidVersionException, InvalidMessageException, DuplicateMessageException,
            InvalidKeyException, UntrustedIdentityException {

        return processPlaintext(peer, secureParty.decrypt(peer, ciphertext));
    }

    private DecryptedPackage processPlaintext(String sender, String plaintext) {

        //Split the message to metadata (0) and content (1)
        String[] plainParts = plaintext.split(MessageMetaData.META_TRAILER);

        ByteBuffer serializedMeta = ByteBuffer.wrap(plainParts[0].getBytes());

        //The message index would be the first field in the message
        int messageIndex = serializedMeta.getInt();

        //As mentioned, the content is the second part of the message
        String content = plainParts[1];

        //Insert this message to the peer history
        conversationHistory.get(sender).insert(content, messageIndex);

        DecryptedMessage message = new DecryptedMessage(messageIndex,content);

        MessageMetaData mmd = MessageMetaData.wrap(serializedMeta);

        List<HistoryDisagreement> hdList = new ArrayList<>();

        while(mmd.hasNext())
        {
            RepliedMessageRecord hisRecord = mmd.next();

            //The last recorded message arriving in order from replied peer

            MessageRecord myRecord = conversationHistory.get(hisRecord.getRepliedPeer()).getLastChainRecord();

            if(!myRecord.compare(hisRecord))
            {
                hdList.add(new HistoryDisagreement(hisRecord.getRepliedPeer(),
                        hisRecord.getMessageIndex(), myRecord.getMessageIndex()));
            }
        }

        int lastChainIndex = conversationHistory.get(sender).getLastChainRecord().getMessageIndex();

        return new DecryptedPackage(sender, message, lastChainIndex, hdList);
    }



}
