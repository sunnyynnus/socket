package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author sunny
 *
 */
public class ChatClient {

	private final JFrame frame;
	private final JTextField textField;
	private final JTextArea messageArea ;
	private PrintWriter out =null;
	
	public ChatClient(){
		frame= new JFrame("ChatFrame");
		textField= new JTextField(40);
		messageArea = new JTextArea(8, 40);
		
		textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();
        
        textField.addActionListener((e) -> { 
        	out.println(textField.getText());
        	textField.setText("");
        });
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChatClient client= new ChatClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.connect();
	}
	
	private void connect(){
		String address= getServerAddress();
		int port= getPortNum();
		Socket socket= null;
		BufferedReader br= null;
		
		try {
			socket= new Socket(address, port);
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			while(true){
				String line= br.readLine();
				if(line==null){
					continue;
				}
				if(line.startsWith("EnterName")){
					out.println(getName());
				} else if(line.startsWith("NameAccepted")){
					textField.setEditable(true);
				} else if(line.startsWith("Message")){
					messageArea.append(line.substring(8) + "\n");
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				out.close();
				br.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getServerAddress(){
		return JOptionPane.showInputDialog(frame, "Enter IP address", "Chat Room", JOptionPane.PLAIN_MESSAGE);
	}
	
	private int getPortNum(){
		String num=JOptionPane.showInputDialog(frame, "Enter Port no. ", "Chat Room", JOptionPane.PLAIN_MESSAGE);
		try{
			return Integer.parseInt(num);
		} catch(Exception e){
			return -1;
		}
		
	}
	
	private String getName(){
		return JOptionPane.showInputDialog(frame, "Choose a user name", "Screen name selection", JOptionPane.PLAIN_MESSAGE);
	}

}
