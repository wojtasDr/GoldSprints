package GS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JPanel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JTextField;

import eu.hansolo.steelseries.gauges.Radial;

import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.DefaultComboBoxModel;
import eu.hansolo.steelseries.tools.KnobType;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.PointerType;
import eu.hansolo.steelseries.tools.ColorDef;
import eu.hansolo.steelseries.tools.ForegroundType;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import net.miginfocom.swing.MigLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.Box;
import java.awt.Panel;
import javax.swing.JSeparator;
import java.awt.Canvas;
import javax.swing.LayoutStyle.ComponentPlacement;

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
	private Radial c1Gauge;
	private Radial c2Gauge;
	private final JPanel panel_1 = new JPanel();
	private final JPanel panel_2 = new JPanel();
	
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
		frame.setBounds(100, 100, 582, 726);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//comPorts ComboBox
		this.fillInComboBoxWithCommPorts();
		
		//Radial c1Gauge
		JPanel panel = new JPanel();
		panel.setBounds(80, 250, 400, 400);
		frame.getContentPane().add(panel);

		c1Gauge = new Radial();
		c1Gauge.setUnitString("m");
		c1Gauge.setTitle("");
		c1Gauge.setLcdVisible(false);
		c1Gauge.setLedVisible(false);
		c1Gauge.setOpaque(false);

		c2Gauge = new Radial();
		c2Gauge.setPointerColor(ColorDef.BLUE);
		c2Gauge.setUnitString("");
		c2Gauge.setTitle("");
		//c2Gauge.setTickmarkColorFromThemeEnabled(false);
		//c2Gauge.setBorder(null);
		c2Gauge.setBackgroundVisible(false);
		c2Gauge.setTickmarksVisible(false);
		c2Gauge.setTicklabelsVisible(false);
		c2Gauge.setMinorTickmarkVisible(false);
		//c2Gauge.setLabelColorFromThemeEnabled(false);
		c2Gauge.setLcdVisible(false);
		c2Gauge.setLedVisible(false);
		c2Gauge.setFrameVisible(false);
		//c2Gauge.setTitleAndUnitFontEnabled(false);

		c2Gauge.setCustomLayerVisible(false);
		//c2Gauge.setCustomLcdUnitFontEnabled(false);
		//c2Gauge.setCustomTickmarkLabelsEnabled(false);
		//c2Gauge.setDigitalFont(false);
		//c2Gauge.setDoubleBuffered(false);
		c2Gauge.setForegroundVisible(false);
		c2Gauge.setFrame3dEffectVisible(false);
		c2Gauge.setMajorTickmarkVisible(false);
		c2Gauge.setOpaque(false);
		c2Gauge.setRangeOfMeasuredValuesVisible(false);
		c2Gauge.setSection3DEffectVisible(false);

		frame.getContentPane().add(panel);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(c2Gauge, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(c1Gauge, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
		);
		gl_panel.setVerticalGroup(				
			gl_panel.createParallelGroup(Alignment.LEADING)
			.addComponent(c2Gauge, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
			.addComponent(c1Gauge, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
		);
		panel.setLayout(gl_panel);
		panel_1.setBounds(25, 13, 297, 188);
		
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		commPorts.setBounds(35, 15, 96, 22);
		panel_1.add(commPorts);
		commConnectButton.setBounds(156, 14, 97, 25);
		panel_1.add(commConnectButton);
		connStatus.setBounds(12, 53, 271, 113);
		panel_1.add(connStatus);
		panel_2.setBounds(331, 13, 200, 94);
		
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		btnStart.setBounds(12, 13, 176, 25);
		panel_2.add(btnStart);
		distanceComboBox.setBounds(127, 51, 61, 31);
		panel_2.add(distanceComboBox);
		
		
		//Distance ComboBox
		distanceComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c1Gauge.setMaxValue((Integer) distanceComboBox.getSelectedItem());
				c2Gauge.setMaxValue((Integer) distanceComboBox.getSelectedItem());
			}
		});
		
		distanceComboBox.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {50, 100, 150, 200, 250, 300, 350, 400, 450, 500}));
		distanceComboBox.setSelectedIndex(5);
		
		JLabel lblNewLabel = new JLabel("Distance [m]:");
		lblNewLabel.setBounds(12, 51, 78, 25);
		panel_2.add(lblNewLabel);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(334, 108, 200, 94);
		frame.getContentPane().add(panel_3);
		panel_3.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Velocity1:");
		lblNewLabel_1.setBounds(12, 13, 99, 21);
		panel_3.add(lblNewLabel_1);
		
		//Velocity Label
		velocity = new JLabel("0.0 km/h");
		velocity.setBounds(114, 7, 74, 33);
		panel_3.add(velocity);
		
		JLabel lblVelocity = new JLabel("Velocity2:");
		lblVelocity.setBounds(12, 54, 56, 16);
		panel_3.add(lblVelocity);
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		// Button Start/Stop
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
						
						c1Gauge.setValue(totalDistance);
						c2Gauge.setValue(totalDistance);
					}
				}
			}
		});

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
					
					totalDistance = 0.0f;
					distance = 0.0f;
					numberOfRevolutions = 0;
					
					c1Gauge.setValue(totalDistance);
					c2Gauge.setValue(totalDistance);
				}
			}
		});
	}
	
	public void update(Observable o, Object arg) {
		numberOfRevolutions = rxTxComm.getRevolutions();
		distance = u.countDistance(numberOfRevolutions);
		totalDistance = u.countTotalDistance(distance, totalDistance);
		System.out.println("Total Distance: " + totalDistance + " m");
		
		System.out.println("Distance " + (Integer)distanceComboBox.getSelectedItem());
		if(totalDistance > 0){
		if(totalDistance <= (Integer)distanceComboBox.getSelectedItem() || totalDistance <= (Integer)distanceComboBox.getSelectedItem()){
			c1Gauge.setValue(totalDistance);
			c2Gauge.setValue(totalDistance +10);
		}
//		else {
//			rxTxComm.write("F");
//			rxTxComm.deleteObserver(GsGui.this);
//			rxTxComm.removeListener();
//			btnStart.setText("Start");
//			velocity.setText("0.0 km/h");
//
//			totalDistance = 0.0f;
//			distance = 0.0f;
//			numberOfRevolutions = 0;
//			
//			c1Gauge.setValue(totalDistance);
//			c2Gauge.setValue(totalDistance);
//		}
		}

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
