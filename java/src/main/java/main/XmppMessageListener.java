package main;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

//secure construction for message listener
public class XmppMessageListener implements MessageListener {
	private String lastMessage;
	private String lastMessageSender;
	private static XmppMessageListener Listener;
	private static boolean created = false;

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
		synchronized(Listener){
			String messageChecker = message.getBody();
			if (messageChecker == null){
				return;
			}
			lastMessageSender = message.getFrom();
			lastMessage = messageChecker;

			Listener.notifyAll();
		}
	}

	private XmppMessageListener(){}
	public static XmppMessageListener createXmppMessageListener(){
		if(created == false){
			Listener = new XmppMessageListener();
			return Listener;
		}
		else{
			Listener = new XmppMessageListener();
			return Listener;
		}
	}
}