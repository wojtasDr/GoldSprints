package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import rxTx.UartHandler;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GoldSprintGui {

	private JFrame frame;
	
	private JButton btnConnect;
	private JButton btnDisconnect;
	public JComboBox allComPortsCombo;
	public JLabel connectionStatusLabel;
	private JLabel allAvailableComPorts;
	public JTextArea commPortReadTextArea;
	
	private UartHandler uartHandler;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GoldSprintGui window = new GoldSprintGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GoldSprintGui() {
		this.uartHandler = new UartHandler(this);
		
		initialize();
		uartHandler.listAvailablePorts();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 964, 654);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//Connect/Disconnect button
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(12, 83, 97, 25);
		frame.getContentPane().add(btnConnect);
		
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uartHandler.connect();
		        if (uartHandler.getConnected() == true)
		        {
		        	//btnConnect.setText("Disconnect");
		            if (uartHandler.initIOStream() == true)
		            {
		            	uartHandler.initListener();
		            }
		        }
			}
		});
		
//		btnConnect.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				//uartHandler.connect();
//				if (uartHandler.getConnected() == false) {
//					uartHandler.connect();
//					btnConnect.setText("Disconnect");
////					if (uartHandler.initIOStream() == true)
////		            {
////						uartHandler.initListener();
////		            }
//					
//				} else {
//					uartHandler.disconnect();
//					btnConnect.setText("Connect");
//				}
//			}
//		});
		
		//Disconnect button
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setBounds(12, 121, 97, 25);
		frame.getContentPane().add(btnDisconnect);
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uartHandler.disconnect();
			}
		});
		
		//Connection status label
		connectionStatusLabel = new JLabel("");
		connectionStatusLabel.setBounds(121, 83, 499, 80);
		frame.getContentPane().add(connectionStatusLabel);

		//Comm ports label
		allAvailableComPorts = new JLabel("Available Com Ports:");
		allAvailableComPorts.setBounds(12, 25, 125, 25);
		frame.getContentPane().add(allAvailableComPorts);
		
		//Comm ports combobox
		allComPortsCombo = new JComboBox();
		allComPortsCombo.setBounds(149, 26, 133, 25);
		frame.getContentPane().add(allComPortsCombo);
		
		//Comm data read text area
		commPortReadTextArea = new JTextArea();
		commPortReadTextArea.setBounds(162, 219, 499, 187);
		frame.getContentPane().add(commPortReadTextArea);
		

		allComPortsCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
			}
		});
	}
}
