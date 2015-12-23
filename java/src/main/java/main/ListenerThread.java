package main;

import java.util.ArrayList;
import java.util.List;
import ChatCommons.INotifier;

public class ListenerThread extends Thread {
	private XmppMessageListener messageListener;
	private List<INotifier> listOfNotifiers;
    
    public void AddNotifier(INotifier notifier){
    	listOfNotifiers.add(notifier);
    }
	
	public ListenerThread(XmppMessageListener listener){
		messageListener = listener;
		listOfNotifiers = new ArrayList<INotifier>();
	}
	
    public void run(){
    	while(true){
    		synchronized(messageListener){ 
				try {
					System.out.println(String.format("WAITING MESSAGE"));
					messageListener.wait();					
					System.out.println(String.format("MESSAGE ARRIVED"));
					for(INotifier notifier : listOfNotifiers){
						notifier.RecieveMessage(messageListener.getLastMessageSender(), messageListener.getLastMessage(),messageListener.getMessageType());
					}
					messageListener.printLastMessage();
				} 
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
	    	}
	    	System.out.println(String.format("WAITING NEXT MESSAGE"));
	    }
    }

}
