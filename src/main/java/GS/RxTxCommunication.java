package GS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class RxTxCommunication extends Observable implements SerialPortEventListener {

	private InputStream is = null;
	private OutputStream os = null;

	final static int SPACE_ASCII = 32;
	final static int DASH_ASCII = 45;
	final static int NEW_LINE_ASCII = 10;

	final static int CONNECTIONTIMEOUT = 2000;
	private SerialPort serialPort = null;

	private Enumeration<?> allAvailablePorts = null;
	private HashMap<String, CommPortIdentifier> portNames = new HashMap<String, CommPortIdentifier>();

	private Boolean isConnected = false;
	private Boolean isEventListenerAvailable = false;
	private String connectionStatus = "";

	private byte[] receivedBuffer = new byte[1024];
	private int receivedBufferLen = -1;
	private String receivedData = "";
	private StringBuffer receivedRevolution;

	private Float sensor1Velocity = 0.0f;
	private List<Integer> sensor1Revolutions = new ArrayList<Integer>();

	private Utils u = new Utils();

	public HashMap<String, CommPortIdentifier> listAvailablePorts() {
		CommPortIdentifier port;
		allAvailablePorts = CommPortIdentifier.getPortIdentifiers();

		while (allAvailablePorts.hasMoreElements()) {
			port = (CommPortIdentifier) allAvailablePorts.nextElement();

			if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				portNames.put(port.getName(), port);
			}
		}

		return portNames;
	}

	public String connectWithCommPort(CommPortIdentifier selectedPortIdentifier) {
		CommPort commPort = null;
		
		try {
			commPort = selectedPortIdentifier.open("Arduino", CONNECTIONTIMEOUT);
			
			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			this.setConnected(true);
			connectionStatus = "Connected.";

		} catch (PortInUseException e) {
			connectionStatus = selectedPortIdentifier.getName() + " is in use. (" + e.toString() + ")";

		} catch (Exception e) {
			connectionStatus = "Failed to open " + selectedPortIdentifier.getName() + "(" + e.toString() + ")";

		}

		return connectionStatus;
	}

	public String disconnect() {
		try {
			
			if(getEventListenerFlag() == true){
				this.removeListener();
			}
			
			if (serialPort != null) {
				serialPort.close();
			}
			
			if (is != null) {
				is.close();
			}

			if (os != null) {
				os.close();
			}

			this.setConnected(false);

			connectionStatus = "Disconnected.";
		} catch (Exception e) {
			connectionStatus = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
			e.printStackTrace();
		}

		return connectionStatus;
	}

	public boolean initIOStream() {
		boolean successful = false;

		try {
			is = serialPort.getInputStream();
			//os = serialPort.getOutputStream();
			
			successful = true;
			return successful;
		} catch (IOException e) {
			System.out.println("I/O Streams failed to open. (" + e.toString() + ")");
			return successful;
		}
	}

	public void resetReceivedData() {
		receivedBuffer = new byte[1024];
		receivedBufferLen = -1;
		receivedData = "";
		receivedRevolution = new StringBuffer();
	}

	public void serialEvent(SerialPortEvent evt) {
		if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			receivedBuffer = new byte[1024];
			receivedBufferLen = -1;
			receivedData = "";
			receivedRevolution = new StringBuffer();
			try {
				while ((receivedBufferLen = this.is.read(receivedBuffer)) > -1 && getEventListenerFlag()) {
					receivedData = new String(receivedBuffer, 0, receivedBufferLen);
					if (!receivedData.isEmpty()) {
						receivedRevolution.append(receivedData);

						if (receivedRevolution.toString().contains("\n")) {
							sensor1Revolutions = u.collectData(receivedRevolution);
							sensor1Velocity = u.countVelocity(sensor1Revolutions);
							this.setSensor1Velocity(sensor1Velocity);
							receivedRevolution = new StringBuffer();
						}
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addListener() {
		try {
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			this.setEventListenerFlag(true);
		} catch (TooManyListenersException e) {
			System.out.println("Too many listeners. (" + e.toString() + ")");
		}
	}

	public void removeListener() {
		if (is != null) {
			serialPort.removeEventListener();
		}

		try {
			if (is != null) {
				is.close();
			}

			if (os != null) {
				os.close();
			}
		} catch (IOException e) {
			connectionStatus = "Failed to close streams " + "(" + e.toString() + ")";
			e.printStackTrace();
		}

		this.resetReceivedData();
		u.ResetData();
		this.setEventListenerFlag(false);
	}

	public void write(String send) {
		try {
			os = serialPort.getOutputStream();
			
			for (Byte b : send.getBytes()){
				os.write(b);
			}

			os.flush();

		} catch (IOException e) {
			System.out.println("Failed to write data. (" + e.toString() + ")");
		}
	}

	final public boolean getConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	final public boolean getEventListenerFlag() {
		return isEventListenerAvailable;
	}

	public void setEventListenerFlag(boolean isEventListenerAvailable) {
		this.isEventListenerAvailable = isEventListenerAvailable;
	}

	final public Float getSensor1Velocity() {
		return sensor1Velocity;
	}

	public void setSensor1Velocity(Float sensor1Velocity) {
		this.sensor1Velocity = sensor1Velocity;
		setChanged();
		notifyObservers(this.sensor1Velocity);
	}
}
