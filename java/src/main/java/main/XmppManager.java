package main;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import ChatCommons.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
//import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import java.util.HashMap;

public class XmppManager implements ICommManager
{
	private static final int packetReplyTimeout = 500; // millis

	private final int PORT = 5222;
	private String server;
	private int port;
	private static boolean created = false;
	private static XmppManager Manager;
	private ConnectionConfiguration config;
	private XMPPConnection connection;
	private ListenerThread listener;
	private ChatManager chatManager;
	private XmppMessageListener messageListener;
	private HashMap<String,Chat> ChatMap;



	public void getBuddyList(){
		System.out.println(String.format("getting buddy list"));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();



		System.out.println("\n\n" + entries.size() + " buddy(ies):");
		for(RosterEntry r:entries)
		{
			System.out.println("presence :" + roster.getPresenceResource(r.getUser()));
			System.out.println("status : "+r.getStatus());
			System.out.println("name: "+r.getName());

		}

	}

	private XmppManager(String xmppServer){
		server = xmppServer;
		port = PORT;
		ChatMap = new HashMap<String,Chat>();
	}


	static public XmppManager createManager(String xmppServer) throws XMPPException {
		if(!created){
			created = true;
			Manager = new XmppManager(xmppServer);
			try {
				Manager.init();
			}
			catch (XMPPException e) {
				Manager = null;
				created = false;
				throw e;
			}
		}
		return Manager;
	}

	public void createMessageListenerThread(){
		listener = new ListenerThread(messageListener);
		listener.start();
	}

	public void addNotifier(INotifier notifier){
		listener.AddNotifier(notifier);
	}

	private void init() throws XMPPException {

		System.out.println(String.format("Initializing connection to server %1$s port %2$d", server, port));

		SmackConfiguration.setPacketReplyTimeout(packetReplyTimeout);

		config = new ConnectionConfiguration(server, port);
		config.setSASLAuthenticationEnabled(false);
		config.setSecurityMode(SecurityMode.disabled);

		connection = new XMPPConnection(config);
		connection.connect();

		System.out.println("Connected: " + connection.isConnected());

		chatManager = connection.getChatManager();
		setMessageReciver();
	}

	private void setMessageReciver(){
		messageListener = XmppMessageListener.createXmppMessageListener();
		createMessageListenerThread();
	}


	public void userLogin(String username, String password) throws XMPPException {
		if (connection!=null && connection.isConnected()) {
			connection.login(username, password);
		}
	}

	public void setStatus(boolean available, String status) {

		Presence.Type type = available? Type.available: Type.unavailable;
		Presence presence = new Presence(type);
		presence.setStatus(status);
		connection.sendPacket(presence);

	}

	public void disconnect() {
		if (connection!=null && connection.isConnected()) {
			Roster roster = connection.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for(RosterEntry r:entries)
			{
				try {
					roster.removeEntry(r);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			connection.disconnect();
		}
	}
	public void setChat(String buddyJID, String name) throws XMPPException {
		// System.out.println(String.format("Sending message '%1$s' to user %2$s", message, buddyJID));
		if(!ChatMap.containsKey(buddyJID)){
			Chat chat = chatManager.createChat(buddyJID, messageListener);
			ChatMap.put(buddyJID, chat);
			System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", buddyJID, name));
			Roster roster = connection.getRoster();
			roster.createEntry(buddyJID, name, null);
		}
	}
	public void sendMessage(String message, String buddyJID) throws XMPPException {
		System.out.println(String.format("trying to send mesage '%1$s' to user %2$s", message, buddyJID));
		Chat chat = ChatMap.get(buddyJID);
		if(chat == null){
			System.out.println(String.format("cant find the requested chat"));
		}
		else{
			chat.sendMessage(message);
			System.out.println(String.format("message sent"));
		}
	}
}
