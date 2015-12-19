package ChatGUI;

import ChatCommons.INotifier;
import main.XmppManager;
import org.jivesoftware.smack.XMPPException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import  java.util.Date;
import java.text.SimpleDateFormat;
import org.whispersystems.libaxolotl.*;
import security.management.SecureParty;
import security.trust.concrete.FingerprintWG;
import security.trust.concrete.FingerprintWitness;
import security.trust.concrete.PersistentTrustStore;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;


/**
 * Created by Guy on 12/12/2015.
 */
public class ClientGUI extends JFrame implements ActionListener, INotifier
{
	private JLabel label;
	private JLabel labelKS;
	private JTextField tfMessage;
	private JTextField tfServer, tfPort;
	private JPasswordField tfPassword;
	private JTextField tfUser;
	private JButton login, logout, whoIsIn;
	private JTextArea ta;
	private boolean connected;
	private int defaultPort;
	private String defaultHost;
	private JList<String> lstUsers;
	private XmppManager xmppManager;
	private JTextField tfFingerPrint;
	private SecureParty party1 = null;
	private JTextField tfPathKS;
	private PersistentTrustStore store1 = null;

	ClientGUI(String host, int port)
	{
		super("Chat Client");
		defaultPort = port;
		defaultHost = host;

		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(6,1));
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

		northPanel.add(serverAndPort);
		northPanel.add(userAndPasswordPanel);

		labelKS = new JLabel("Enter your key store path directory", SwingConstants.CENTER);
		northPanel.add(labelKS);
		tfPathKS = new JTextField("C:\\ks");
		northPanel.add(tfPathKS);

		label = new JLabel("Login and then Enter your message below", SwingConstants.CENTER);
		northPanel.add(label);

		tfMessage = new JTextField("");
		tfMessage.setEnabled(false);
		tfMessage.setBackground(Color.WHITE);
		northPanel.add(tfMessage);

		add(northPanel, BorderLayout.NORTH);

		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,2));

		lstUsers = GetFriendsList();

		centerPanel.add(lstUsers);
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);
		whoIsIn = new JButton("Witness");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);

		tfFingerPrint = new JTextField("Your Finger Print...");
		tfFingerPrint.setEditable(false);
		tfFingerPrint.setBackground(Color.WHITE);
		JPanel southPanel = new JPanel(new GridLayout(2,2));
		JPanel buttonPanel = new JPanel(new GridLayout(1,3));

		buttonPanel.add(login);
		buttonPanel.add(logout);
		buttonPanel.add(whoIsIn);

		southPanel.add(tfFingerPrint);
		southPanel.add(buttonPanel);

		add(southPanel, BorderLayout.SOUTH);

	//	add(southPanel2,BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tfMessage.requestFocus();
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
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		tfMessage.removeActionListener(this);
		connected = false;
	}

	private JList GetFriendsList()
	{
		String	listUsers[] =
				{
						"user1",
						"user2",
						"user3",
				};

		return new JList<>(listUsers);
	}

	private void Logout()
	{
		xmppManager.disconnect();
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == login)
		{
			Login();
		}
		else if (o == logout)
		{
			Logout();
		}
		else if (o == whoIsIn)
		{
			//Todo: implement fingerprint
		}
		else if (connected)  // Send msg
		{
			try
			{
				String sendTo = lstUsers.getSelectedValue();

				xmppManager.sendMessage(tfMessage.getText(), sendTo, true);
				Thread.sleep(100);
				xmppManager.sendMessage(tfMessage.getText(), sendTo, false);

				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				append(dateFormat.format(new Date()) + " " + tfUser.getText() + ": " + tfMessage.getText() + "\n");
			} catch (XMPPException ex)
			{
				System.out.println("System Error");
				ex.printStackTrace();
				return;
			} catch (Exception ex) {
				System.out.println("System Error");
				ex.printStackTrace();
				return;
			}

			tfMessage.setText("");
			return;
		}
	}

	private void Login()
	{
		ChangeGUIWhenLoginPressed();

		String userName = tfUser.getText().trim();
		OpenXMPPConnection(userName,
				  			new String(tfPassword.getPassword()));

		try {
			store1 = new PersistentTrustStore(String.format("%s/%s.ks", tfPathKS.getText(), userName), "pass", false);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			party1 = new SecureParty(userName, store1, new FingerprintWG());
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableEntryException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		tfFingerPrint.setText(party1.generateWitness().serialize());
	}

	private void ChangeGUIWhenLoginPressed()
	{
		/*
		String portNumber = tfPort.getText().trim();
		try {
			int port = Integer.parseInt(portNumber);
			//TODO: Send the port from the GUI to XmppManager
		} catch (Exception en) {
			return;
		}*/

		tfMessage.setText("");
		label.setText("Enter your message below");
		tfMessage.setEnabled(true);
		connected = true;

		login.setEnabled(false);
		logout.setEnabled(true);
		whoIsIn.setEnabled(true);
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		tfUser.setEditable(false);
		tfPassword.setEditable(false);
		tfPathKS.setEditable(false);

		tfMessage.addActionListener(this);
	}

	private void OpenXMPPConnection(String userName, String password)
	{
		try
		{
			xmppManager = XmppManager.createManager(tfServer.getText());
			xmppManager.addNotifier(this);
			xmppManager.userLogin(userName, password);

			System.out.println("Connected User Name is: " + userName);

			ConnectedWithFriends(userName);

		}
		catch (XMPPException e)
		{
			e.printStackTrace();
			connectionFailed();
		}
	}

	private void ConnectedWithFriends(String userName) throws XMPPException
	{
		if (userName.equals("user1"))
		{
			xmppManager.connectToFriend("user2");
		}
		else
		{
			xmppManager.connectToFriend("user1");
		}
	}

	public static void main(String[] args)
	{
		new ClientGUI("dell", 5222);
	}

	public void ReceiveMessage(String from, String Message, boolean isKeyMessage)
	{
		from = from.split("@")[0];
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

		if (!isKeyMessage)
		{
			append(dateFormat.format(new Date()) + " " + from + ": " + Message + "\n");
		}
		else
		{
			append(dateFormat.format(new Date()) + "received key message " + from + ": " + Message + "\n");
		}
	}
}