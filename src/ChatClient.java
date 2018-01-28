import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
@SuppressWarnings("serial")
public class ChatClient extends JPanel{


	//UI

	JTextArea textArea;
	JScrollPane scrollPane;

	JTextArea inputBar;

	JButton sendButton;

	//writers
	public DatagramSocket writingSocket;


	//readers
	public Thread clientReader;
	public Runnable reader;
	public DatagramSocket readingSocket;
	public InetAddress ip;
	public byte[] readArray;


	public int PORT;
	public final int SERVER_PORT = 2000;

	boolean isRunning;

	public String userName = "";

	public ChatClient(){
		try {
			@SuppressWarnings("resource")
			ServerSocket s = new ServerSocket(0);
			PORT = s.getLocalPort(); //getting PORT id , special to each client
		} catch (Exception e) {e.printStackTrace();}
		start();
		panel();

	}


	void panel(){
		//basic frame
		JFrame frame = new JFrame("Chat Application");
		frame.add(this);
		frame.setPreferredSize(new Dimension(300,500));


		textArea = new JTextArea(25,20);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane);

		inputBar = new JTextArea(1,21);
		this.add(inputBar);

		sendButton = new JButton("Send");
		sendButton.addActionListener(e -> {
			sendToServer();
			inputBar.setText("");
		});

		this.add(sendButton);

		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				sendToServer(DataPackageTranslator.exitCode);
			}
		});


		String whatTheUserEntered = JOptionPane.showInputDialog("Please enter a username");
		if (whatTheUserEntered == "" || whatTheUserEntered == null) {
			userName = "Anon"+(int)(Math.random()*100);
			sendToServer(DataPackageTranslator.enterCode);

		}else{
			userName = whatTheUserEntered;
			sendToServer(DataPackageTranslator.enterCode);
		}

	}


	synchronized void start(){
		reader = () -> read();

		try{
			readingSocket = new DatagramSocket(PORT);  
			ip = InetAddress.getByName("127.0.0.1");  
			writingSocket = new DatagramSocket();
			readArray = new byte[1024];  
		}catch(Exception e) { e.printStackTrace(); } 

		clientReader = new Thread(reader);
		isRunning = true;

		clientReader.start();


	}

	void read(){
		while(isRunning){
			try{
				DatagramPacket readPacket = new DatagramPacket(readArray, 1024);  
				readingSocket.receive(readPacket);  
				String readData = new String(readPacket.getData(), 0, readPacket.getLength()); 
				DataPackageTranslator convertedData = new DataPackageTranslator(readData);
				//applying new text to area
				textArea.setText(textArea.getText().concat( "\n" + convertedData + "\n"));

				//sleeping thread
				Thread.sleep(1);
			}catch(Exception e) { e.printStackTrace(); }
		}
	}

	void sendToServer(){
		try{
			if(inputBar.getText().trim() != ""){ // anti spam
				if(isRunning){
					String builtPackage = PORT + DataPackageTranslator.nameParseCode + userName + DataPackageTranslator.messageParseCode + inputBar.getText();
					DatagramPacket writePackage = new DatagramPacket(builtPackage.getBytes(), builtPackage.length(), ip, SERVER_PORT);  
					writingSocket.send(writePackage); 
				}
			}
		}catch(Exception e) { e.printStackTrace(); }
	}

	void sendToServer(String notificationType){
		try{

			String builtPackage = PORT + DataPackageTranslator.nameParseCode + userName + DataPackageTranslator.messageParseCode + notificationType;
			DatagramPacket writePackage = new DatagramPacket(builtPackage.getBytes(), builtPackage.length(), ip, SERVER_PORT);  
			writingSocket.send(writePackage); 
		}catch(Exception e) { }
	}



}
