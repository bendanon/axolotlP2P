package main;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

//secure construction for message listener
public class XmppMessageListener implements MessageListener {
	public static final String IS_KEY_MESSAGE = "KEY";
	public static final String NOT_KEY_MESSAGE = "NORMAL";
	private String lastMessage;
	private String lastMessageSender;
	private boolean messageIsKey;
	private static XmppMessageListener Listener;
	private static boolean created = false;

	public boolean getMessageType(){
		return messageIsKey;
	}

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
			String isKey = messageChecker.split("@")[0];
			if(isKey.equals(IS_KEY_MESSAGE)){
				messageIsKey = true;
			}
			else if(isKey.equals(NOT_KEY_MESSAGE)){
				messageIsKey = false;
			}
			else{
				System.out.println("error in receiving message");
			}
			lastMessage = messageChecker.split("@")[1];
			lastMessageSender = message.getFrom().split("@")[0];

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