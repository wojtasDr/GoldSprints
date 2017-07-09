package GS;

import java.awt.EventQueue;

import javax.swing.JFrame;

import gnu.io.CommPortIdentifier;

import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class GsGui {

	private JFrame frame;

	private RxTxCommunication rxTxComm;
	
	private HashMap<String,CommPortIdentifier> availableCommPorts = new HashMap<String,CommPortIdentifier> ();
	
	private  String connectionStatus = "";
	
	//GUI elements
	private final JComboBox<String> commPorts = new JComboBox<String>();
	private JButton commConnectButton = new JButton("Connect");
	private final JLabel connStatus = new JLabel("");
	private final JButton btnStart = new JButton("Start");
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GsGui window = new GsGui();
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
	public GsGui() {
		rxTxComm = new RxTxCommunication();
		
		this.initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//Initialize GUI objects
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//comPorts ComboBox
		this.fillInComboBoxWithCommPorts();
		commPorts.setBounds(29, 36, 188, 22);
		frame.getContentPane().add(commPorts);
		
		//commConnectButton
		commConnectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (rxTxComm.getConnected() == false){
					String selectedPort = (String) commPorts.getSelectedItem();
					CommPortIdentifier selectedPortIdentifier = (CommPortIdentifier) availableCommPorts.get(selectedPort);
					connectionStatus = rxTxComm.connectWithCommPort(selectedPortIdentifier);
					commConnectButton.setText("Disconnect");
					btnStart.setText("Start");
					connStatus.setText(connectionStatus);
				} else {
					rxTxComm.write("F");
					connectionStatus = rxTxComm.disconnect();
					commConnectButton.setText("Connect");
					btnStart.setText("Start");
					connStatus.setText(connectionStatus);
				}
			}
		});
		commConnectButton.setBounds(40, 100, 97, 25);
		frame.getContentPane().add(commConnectButton);

		//Connection Status label
		connStatus.setBounds(149, 100, 271, 51);
		frame.getContentPane().add(connStatus);
		
		//Button Start/Stop
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (rxTxComm.getConnected() == true) {
					if (rxTxComm.getEventListenerFlag() == false) {
						if (rxTxComm.initIOStream() == true) {
							rxTxComm.addListener();
							rxTxComm.write("T");
							btnStart.setText("Stop");
						}
					} else {
						rxTxComm.write("F");
						rxTxComm.removeListener();
						btnStart.setText("Start");						
					}
				}
			}
		});
		
		btnStart.setBounds(182, 182, 97, 25);
		frame.getContentPane().add(btnStart);
		
	}
	
	/**
	 * GUI Helpers
	 */
	private void fillInComboBoxWithCommPorts(){
		availableCommPorts = rxTxComm.listAvailablePorts();
		
		for (Entry<String, CommPortIdentifier> port : availableCommPorts.entrySet())
		{
		    commPorts.addItem(port.getKey());
		}
	}
}
