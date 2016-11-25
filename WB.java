import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

import javax.sound.midi.Receiver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class WB extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
    private String name;
    private Socket socket;
    DataReceiver receiver;
    DataSender sender;
    JTextArea txtrConnectedInputName;
    boolean isFirst = true;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WB frame = new WB();
					frame.setVisible(true);
					frame.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WB() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(12, 5, 310, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		txtrConnectedInputName = new JTextArea();
		txtrConnectedInputName.setText("Connected.\r\nInput name");
		txtrConnectedInputName.setEditable(false);
		txtrConnectedInputName.setBounds(12, 36, 410, 204);
		contentPane.add(txtrConnectedInputName);
		
		JButton btnNewButton = new JButton("Àü¼Û");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					
					if(isFirst) {
						name = textField.getText();
						sender.sendMessage(name);
					}
					
					else {
						sender.sendMessage(textField.getText());
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				textField.setText("");
			}
		});
		btnNewButton.setBounds(325, 4, 97, 23);		
		contentPane.add(btnNewButton);
		
		JRootPane rootPane = SwingUtilities.getRootPane(btnNewButton); 
		rootPane.setDefaultButton(btnNewButton);
	}
	
    public void start() {

        try {
            socket = new Socket("127.0.0.1", 8000);

            receiver = new DataReceiver(socket);
            sender = new DataSender(socket);

            receiver.start();
            sender.start();
        }

        catch(IOException e) {
            e.printStackTrace();
        }

    }
    
    class DataReceiver extends Thread{
        Socket socket;
        DataInputStream in;

        public DataReceiver(Socket socket) {
            this.socket = socket;

            try {
                in = new DataInputStream(socket.getInputStream());
            }

            catch(IOException e) {
                e.printStackTrace();
            }

        }

        public void run() {

            while(in != null) {

                try {
                    txtrConnectedInputName.append("\n" + in.readUTF());
                }

                catch(IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }
    
    class DataSender extends Thread{
        Socket socket;
        DataOutputStream out;

        public DataSender(Socket socket) {
            this.socket = socket;

            try {
                out = new DataOutputStream(socket.getOutputStream());
            }

            catch(IOException e) {
                e.printStackTrace();
            }

        }
        
        public void sendMessage(String data) throws IOException {
        	
        	if(isFirst) {
        		out.writeUTF(name);
        		isFirst = false;
        	}
        	
            if (data.equals("exit"))
                System.exit(0);

            else {
                out.writeUTF("[" + name + "] : " + data);
            }
            
        }

    }
    
}
