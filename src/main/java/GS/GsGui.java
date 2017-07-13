package GS;

import java.awt.EventQueue;

import javax.swing.JFrame;

import gnu.io.CommPortIdentifier;

import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.DefaultComboBoxModel;

public class GsGui implements Observer{

	private float totalDistance = 0.0f;
	private float distance = 0.0f;
	private Integer numberOfRevolutions = 0;
	
	private JFrame frame;

	private RxTxCommunication rxTxComm;
	private Utils u;
	
	private HashMap<String,CommPortIdentifier> availableCommPorts = new HashMap<String,CommPortIdentifier> ();
	
	private  String connectionStatus = "";
	
	//GUI elements
	private final JComboBox<String> commPorts = new JComboBox<String>();
	private JButton commConnectButton = new JButton("Connect");
	private final JLabel connStatus = new JLabel("");
	private final JButton btnStart = new JButton("Start");
	private JLabel velocity = new JLabel("0.0");
	private JComboBox<Integer> distanceComboBox = new JComboBox();
	private final JProgressBar distanceProgressBar = new JProgressBar();
	
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
		u = new Utils();
		this.initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//Initialize GUI objects
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 812, 572);
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
					rxTxComm.deleteObserver(GsGui.this);
					commConnectButton.setText("Connect");
					btnStart.setText("Start");
					velocity.setText("0.0 km/h");
					connStatus.setText(connectionStatus);
				}
			}
		});
		commConnectButton.setBounds(40, 100, 97, 25);
		frame.getContentPane().add(commConnectButton);

		//Connection Status label
		connStatus.setBounds(149, 100, 271, 51);
		frame.getContentPane().add(connStatus);
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		//Button Start/Stop
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (rxTxComm.getConnected() == true) {
					if (rxTxComm.getEventListenerFlag() == false) {
						if (rxTxComm.initIOStream() == true) {
							rxTxComm.addObserver(GsGui.this);
							rxTxComm.addListener();
							rxTxComm.write("T");
							btnStart.setText("Stop");
						}
					} else {
						rxTxComm.write("F");
						rxTxComm.deleteObserver(GsGui.this);
						rxTxComm.removeListener();
						btnStart.setText("Start");	
						velocity.setText("0.0 km/h");
						
						totalDistance = 0.0f;
						distance = 0.0f;
						numberOfRevolutions = 0;
					}
				}
			}
		});
		
		btnStart.setBounds(40, 179, 97, 25);
		frame.getContentPane().add(btnStart);
		
		//Velocity Label
		velocity = new JLabel("0.0 km/h");
		velocity.setBounds(54, 281, 74, 33);
		frame.getContentPane().add(velocity);
		
		//Distance ProgressBar
		distanceProgressBar.setBounds(56, 468, 376, 22);
		frame.getContentPane().add(distanceProgressBar);
		
		
		//Distance ComboBox
		distanceComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				distanceProgressBar.setMaximum((Integer) distanceComboBox.getSelectedItem());

				System.out.println(distanceProgressBar.getMaximum());
			}
		});
		
		distanceComboBox.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {50, 100, 150, 200, 250, 300, 350, 400, 450, 500}));
		distanceComboBox.setSelectedIndex(5);
		
		distanceComboBox.setBounds(54, 373, 48, 33);
		frame.getContentPane().add(distanceComboBox);
	}
	
	public void update(Observable o, Object arg) {
		numberOfRevolutions = rxTxComm.getRevolutions();
		//System.out.println("Revs number: " + numberOfRevolutions);
		distance = u.countDistance(numberOfRevolutions);
		totalDistance = u.countTotalDistance(distance, totalDistance);
		System.out.println("Total Distance: " + totalDistance + " m");
		//System.out.println("Vupdate: " + rxTxComm.getSensor1Velocity().toString());
		velocity.setText(String.format(Locale.ROOT, "%.1f", Double.parseDouble(rxTxComm.getSensor1Velocity().toString())) + " km/h");

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
