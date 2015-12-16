package ChatGUI;

import ChatCommons.INotifier;
import main.XmppManager;
import main.XmppMessageListener;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import  java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Guy on 12/12/2015.
 */
public class ClientGUI extends JFrame implements ActionListener, INotifier {

	private static final long serialVersionUID = 1L;
	// "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField tf;

	// to hold the server address an the port number
	private JTextField tfServer, tfPort;

	// to hold Password
	private JPasswordField tfPassword;
	private JTextField tfUser;

	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn;
	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
	// the default port number
	private int defaultPort;
	private String defaultHost;

	private JList<String> lstUsers;

	private XmppManager xmppManager;

	// Constructor connection receiving a socket number
	ClientGUI(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(4,1));
		// the server name anmd the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.LEFT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));

		// User name and password

		JPanel userAndPasswordPanel = new JPanel(new GridLayout(1,5,1,3));
		// the two JTextField with default value for server address and port number
		tfUser = new JTextField("user1");
		tfPassword = new JPasswordField("crypto");
		tfPassword.setHorizontalAlignment(SwingConstants.LEFT);

		userAndPasswordPanel.add(new JLabel("User Name:  "));
		userAndPasswordPanel.add(tfUser);
		userAndPasswordPanel.add(new JLabel("Password:  "));
		userAndPasswordPanel.add(tfPassword);
		userAndPasswordPanel.add(new JLabel(""));

		// adds the Server an port field to the GUI
		northPanel.add(serverAndPort);

		northPanel.add(userAndPasswordPanel);

		// the Label and the TextField
		label = new JLabel("Login and then Enter your message below", SwingConstants.CENTER);
		northPanel.add(label);

		tf = new JTextField("");
		tf.setEnabled(false);
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);

		add(northPanel, BorderLayout.NORTH);

		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,2));

		String	listUsers[] =
				{
						"user1",
						"user2",
						"user3",
				};

		lstUsers = new JList<>(listUsers);

		centerPanel.add(lstUsers);
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		whoIsIn = new JButton("Witness");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);		// you have to login before being able to Who is in

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Enter your username below");
		tf.setText("Anonymous");
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		tf.removeActionListener(this);
		connected = false;
	}
		
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if (o == logout) {
			xmppManager.disconnect();
			return;
		}
		// if it the who is in button
		if (o == whoIsIn) {
			//client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
			return;
		}

		// ok it is coming from the JTextField
		if (connected) {
			// just have to send the message
			//client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));

			try
			{
				String sendTo = lstUsers.getSelectedValue() + "@guy-pc";

				xmppManager.sendMessage(tf.getText(), sendTo);
				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				append(dateFormat.format(new Date()) + " " + tfUser.getText() +": " + tf.getText() +"\n");
			}
			catch (XMPPException ex)
			{
				System.out.println("System Error");
				ex.printStackTrace();
				return;
			}
			catch (Exception ex)
			{
				System.out.println("System Error");
				ex.printStackTrace();
				return;
			}

			tf.setText("");
			return;
		}

		if (o == login) {
			// ok it is a connection request
			String username = tfUser.getText().trim();
			// empty username ignore it
			if (username.length() == 0)
				return;
			// empty serverAddress ignore it
			String server = tfServer.getText().trim();
			if (server.length() == 0)
				return;
			// empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			if (portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			} catch (Exception en) {
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
		//	client = new Client(server, port, username, this);
			// test if we can start the Client
		//	if (!client.start())
			//	return;
			tf.setText("");
			label.setText("Enter your message below");
			tf.setEnabled(true);
			connected = true;

			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			tfUser.setEditable(false);
			tfPassword.setEditable(false);
			// Action listener for when the user enter a message
			tf.addActionListener(this);

			OpenXMPPConnection(username, new String(tfPassword.getPassword()));
		}
	}

	private void OpenXMPPConnection(String userName, String password)
	{
		try{
			xmppManager = XmppManager.createManager("guy-pc");

			xmppManager.addNotifier(this);
		}
		catch (XMPPException e) {
			System.out.println("System Error");
			e.printStackTrace();
			return;
		}
		try {
			xmppManager.userLogin(userName, password);

			System.out.println("User Name is: " + userName);

			if (userName.equals("user1"))
			{
				xmppManager.setChat("user2@guy-pc","user2");
			}
			else
			{
				xmppManager.setChat("user1@guy-pc","user1");
			}

		} catch (XMPPException e) {
			e.printStackTrace();
		}


	}

	// to start the whole thing the server
	public static void main(String[] args)
	{
		new ClientGUI("localhost", 1500);
	}


	public void RecieveMessage(String from, String Message)
	{
		from = from.split("@")[0];
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		append(dateFormat.format(new Date()) + " " + from +": " + Message + "\n");
	}
}