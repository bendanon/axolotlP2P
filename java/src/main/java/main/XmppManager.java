package main;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.nio.charset.Charset;
//git
import ChatCommons.IChatSender;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
//import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;
import ChatCommons.ICommManager;
import ChatCommons.eMessageType;
import ChatCommons.INotifier;
import main.FriendStatus;

public class XmppManager implements ICommManager {

	private static final int packetReplyTimeout = 500; // ms
	private static final int statusFields = 5; // ms
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

	public FriendStatus[] getBuddiesStats(){
		System.out.println(String.format("getting buddy list..."));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();

		System.out.println("There are " + entries.size() + " buddy(ies):");
		String user;
		Presence presence;
		FriendStatus[] friendsStatus = new FriendStatus[entries.size()];
		int userIndex = 0;
		for(RosterEntry r:entries)
		{
			user = r.getUser();
			try {
				presence = roster.getPresence(user);
				friendsStatus[userIndex] = new FriendStatus();
				friendsStatus[userIndex].setUser(user);
				friendsStatus[userIndex].setName(r.getName());
				if(presence.isAvailable()){
					friendsStatus[userIndex].setStatus(presence.getStatus());
					friendsStatus[userIndex].setMode(presence.getMode().toString());
					friendsStatus[userIndex].setType(presence.getType().toString());
				}
				else{
					friendsStatus[userIndex].setStatus("unavailable");
					friendsStatus[userIndex].setMode("unavailable");
					friendsStatus[userIndex].setType("unavailable");
				}
			}
			catch(java.lang.NullPointerException e){
				System.out.println("error in getting friends stats");
				e.printStackTrace();
			}
			System.out.println("user: "+friendsStatus[userIndex].getUser());
			System.out.println("name: "+friendsStatus[userIndex].getName());
			System.out.println("status :" + friendsStatus[userIndex].getStatus());
			System.out.println("mode :" + friendsStatus[userIndex].getMode());
			System.out.println("type :" + friendsStatus[userIndex].getType());
			userIndex++;
		}
		return friendsStatus;
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
		System.out.println("trying to connect");
		createMessageListenerThread();
		System.out.println("connected");

	}


	public void userLogin(String username, String password) throws XMPPException {
		if (connection!=null && connection.isConnected()) {
			connection.login(username, password);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setStatus(true, "online", Presence.Mode.chat);
			connectToDefaultFriends();
		}
	}

	public void setStatus(boolean available, String status,Mode mode) {
		Presence.Type type = available? Type.available: Type.unavailable;
		Presence presence = new Presence(type);
		presence.setStatus(status);
		presence.setMode(mode);
		connection.sendPacket(presence);

	}
	private void clearRoster(){
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for(RosterEntry r:entries)
		{
			try {
				roster.removeEntry(r);
			} catch (XMPPException e) {
				System.out.println("error in removing entry from roster");
				e.printStackTrace();
			}
		}
	}
	public void disconnect() {
		if (connection!=null && connection.isConnected()) {
			clearRoster();
			connection.disconnect();
		}
	}
	public void connectToFriend(String buddyName) throws XMPPException {
		String buddyJID = buddyName.concat("@".concat(server));
		if(!ChatMap.containsKey(buddyJID)){
			Chat chat = chatManager.createChat(buddyJID, messageListener);
			ChatMap.put(buddyJID, chat);
			System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", buddyJID, buddyName));
			Roster roster = connection.getRoster();
			roster.createEntry(buddyJID, buddyName, null);
		}
	}
	public void sendMessage(String message, String buddyName, eMessageType keyType) throws XMPPException {
		String buddyJID = buddyName.concat("@".concat(server));
		System.out.println(String.format("trying to send mesage '%1$s' to user %2$s", message, buddyJID));
		Chat chat = ChatMap.get(buddyJID);
		if(chat == null){
			System.out.println(String.format("cant find the requested chat"));
		}
		else{
			switch(keyType){
				case eKEY_FINISHED:
					message = XmppMessageListener.KEY_FINISHED_MESSAGE.concat("@".concat((message)));
					break;
				case eKEY_RESPONSE:
					message = XmppMessageListener.KEY_RESPONSE_MESSAGE.concat("@".concat((message)));
					break;
				case eKEY_START:
					message = XmppMessageListener.KEY_BEGIN_MESSAGE.concat("@".concat((message)));
					break;
				case eNORMAL:
					message = XmppMessageListener.NORMAL_MESSAGE.concat("@".concat((message)));
					break;
				case eWITNESS:
					message = XmppMessageListener.WITNESS_MESSAGE.concat("@".concat((message)));
					break;

			}


			chat.sendMessage(message);
			System.out.println(String.format("message sent"));
		}
	}

	private List<String> getFriends(){
		List<String> friendList = new ArrayList<>();
		String line;
		try (
				InputStream fis = new FileInputStream("/home/ben/Desktop/keystore/friends.txt");
				InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);
		) {
			while ((line = br.readLine()) != null) {
				friendList.add(line);
			}
		}
		catch(java.io.IOException ioExc){
			System.out.println(String.format("error in getting friends"));
		}
		return friendList;
	}
	private void connectToDefaultFriends(){
		List<String> friends = getFriends()  ;
		for (int i = 0; i < friends.size(); i++){
			try{
				System.out.println("connecting to: " + friends.get(i) );
				connectToFriend(friends.get(i));
			}
			catch (XMPPException e){
				System.out.println(String.format("error in connection to friends"));
			}
		}
	}
}
