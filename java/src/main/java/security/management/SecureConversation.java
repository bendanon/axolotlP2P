package security.management;

import ChatCommons.IChatSender;
import org.jivesoftware.smack.util.Base64;
import org.whispersystems.libaxolotl.*;
import security.conversation.*;

import java.nio.ByteBuffer;
import java.util.*;

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
        conversationHistory.put(peer, new MessageHistory(peer));
        peers.add(peer);

        return true;
    }

    public ListIterator<String> getPeerIterator()
    {
        return peers.listIterator();
    }


    public void removePeer(String peer)
    {
        conversationHistory.remove(peer);
        peers.remove(peer);
    }

    public int sendMessage (String content)
    {
        MessageHistory myHistory = conversationHistory.get(secureParty.getOwner());
        int index = myHistory.getLastChainRecord().getMessageIndex() + 1;
        myHistory.insert(content, index);

        sendMessage(content, index);

        return index;
    }

    private void sendMessage(String content, int index)
    {
        String metadata = MessageMetaData.createMessageMetadata(index, conversationHistory);

        String fullMessage = String.format("%s%s%s", metadata, MessageMetaData.META_TRAILER, content);

        for(String peer : peers)
        {
            if(secureParty.isSessionInitialized(peer)) {
                sender.sendMessage(peer, secureParty.encrypt(peer, fullMessage));
            }
        }
    }

    public DecryptedPackage receiveMessage (String peer, String ciphertext)
            throws InvalidKeyIdException, NoSessionException, LegacyMessageException,
            InvalidVersionException, InvalidMessageException, DuplicateMessageException,
            InvalidKeyException, UntrustedIdentityException {

        return processPlaintext(peer, secureParty.decrypt(peer, ciphertext));
    }

    public void retransmit(int index)
    {
        MessageHistory myHistory = conversationHistory.get(secureParty.getOwner());
        String messageContent = myHistory.getIndex(index).getMessageContent();
        sendMessage(messageContent, index);
    }


    private DecryptedPackage processPlaintext(String sender, String plaintext) {

        //Split the message to metadata (0) and content (1)
        String[] plainParts = plaintext.split(MessageMetaData.META_TRAILER);

        ByteBuffer serializedMeta = ByteBuffer.wrap(Base64.decode(plainParts[0]));

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
            RepliedMessageRecord hisLastRecord = mmd.next();

            //The last recorded message arriving in order from replied peer
            MessageRecord myLastRecord =
                    conversationHistory.get(hisLastRecord.getRepliedPeer()).getLastRecord();

            MessageHistory peerHistory = conversationHistory.get(hisLastRecord.getRepliedPeer());

            if(!myLastRecord.compare(hisLastRecord))
            {
                hdList.add(new HistoryDisagreement(hisLastRecord.getRepliedPeer(),
                        hisLastRecord.getMessageIndex(), peerHistory.isConsistentWithChain(hisLastRecord)));
            }
        }

        int lastChainIndex = conversationHistory.get(sender).getLastChainRecord().getMessageIndex();

        return new DecryptedPackage(message, lastChainIndex, hdList);
    }



}
