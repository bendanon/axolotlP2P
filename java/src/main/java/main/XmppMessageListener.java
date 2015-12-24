package main;
import ChatCommons.ICommManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
//git
//secure construction for message listener
public class XmppMessageListener implements MessageListener {

	public static final String KEY_BEGIN_MESSAGE = "KEY";
	public static final String NORMAL_MESSAGE = "NORMAL";
	public static final String WITNESS_MESSAGE = "WITNESS";
	public static final String KEY_RESPONSE_MESSAGE = "RESPONSE";
	public static final String KEY_FINISHED_MESSAGE = "FINISHED";

	private String lastMessage;
	private String lastMessageSender;
	private ICommManager.eMessageType messageType;
	private static XmppMessageListener Listener;
	private static boolean created = false;
	
	public ICommManager.eMessageType getMessageType(){
		return messageType;
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
			if(isKey.equals(KEY_BEGIN_MESSAGE)){
				messageType = ICommManager.eMessageType.eKEY_START;
			}
			else if(isKey.equals(KEY_FINISHED_MESSAGE)){
				messageType = ICommManager.eMessageType.eKEY_FINISHED;
			}
			else if(isKey.equals(KEY_RESPONSE_MESSAGE)){
				messageType = ICommManager.eMessageType.eKEY_RESPONSE;
			}
			else if(isKey.equals(NORMAL_MESSAGE)){
				messageType = ICommManager.eMessageType.eNORMAL;
			}
			else if(isKey.equals(WITNESS_MESSAGE)){
				messageType = ICommManager.eMessageType.eWITNESS;
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