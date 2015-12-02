package xmppMessages;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;


public class XmppMessageListener implements MessageListener {
	private String lastMessage;
	private String lastMessageSender;
	
	public String getLastMessage(){
		return lastMessage;
	}
	
	public String getLastMessageSender(){
		return lastMessageSender;
	}
	public void printLastMessage(){
		System.out.println(String.format("Received new message '%1$s' from %2$s", lastMessage, lastMessageSender));
	}
    public void processMessage(Chat chat, Message message) {
    	lastMessageSender = message.getFrom();
    	lastMessage = message.getBody();
    	printLastMessage();
    }
    
}