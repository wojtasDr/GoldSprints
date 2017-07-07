package rxTx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gui.GoldSprintGui;

public class UartHandler implements SerialPortEventListener{


    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;
    //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    //some ascii values for for certain things
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;


    private String logText = "";
    
    private boolean bConnected = false;
	
    private Enumeration<?> allPorts = null;
    
    private HashMap<String,CommPortIdentifier> portNames = new HashMap<String,CommPortIdentifier> ();
    
    private InputStream input = null;
    private OutputStream output = null;
    
    private GoldSprintGui gsWindow = null;
    
    public UartHandler(GoldSprintGui gsWindow)
    {
        this.gsWindow = gsWindow;
    }
    
    public void textString(){
    	
    }
	
	public HashMap<String,CommPortIdentifier> listAvailablePorts(){
		allPorts = CommPortIdentifier.getPortIdentifiers();
		while (allPorts.hasMoreElements()){
			CommPortIdentifier port = (CommPortIdentifier)allPorts.nextElement();
			
			if (port.getPortType() == CommPortIdentifier.PORT_SERIAL){
				gsWindow.allComPortsCombo.addItem(port.getName());
				portNames.put(port.getName(), port);
			}
		}
		return portNames;
	}
	
	public void connect()
    {
        String selectedPort = (String)gsWindow.allComPortsCombo.getSelectedItem();
        selectedPortIdentifier = (CommPortIdentifier)portNames.get(selectedPort);
        
        CommPort commPort = null;

        try
        {
            commPort = selectedPortIdentifier.open("TigerControlPanel", TIMEOUT);

            serialPort = (SerialPort)commPort;
            serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            
            setConnected(true);

            logText = selectedPort + " opened successfully.";
            gsWindow.connectionStatusLabel.setText(logText);

            //CODE ON SETTING BAUD RATE ETC OMITTED
            //XBEE PAIR ASSUMED TO HAVE SAME SETTINGS ALREADY

            //enables the controls on the GUI if a successful connection is made
            //gsWindow.keybindingController.toggleControls();
        }
        catch (PortInUseException e)
        {
            logText = selectedPort + " is in use. (" + e.toString() + ")";
            gsWindow.connectionStatusLabel.setText(logText);
            //gsWindow.txtLog.setForeground(Color.RED);
            //gsWindow.txtLog.append(logText + "\n");
        }
        catch (Exception e)
        {
            logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
            gsWindow.connectionStatusLabel.setText(logText);
            //gsWindow.txtLog.append(logText + "\n");
            //gsWindow.txtLog.setForeground(Color.RED);
        }
    }
	
	public void disconnect()
    {
        try
        {
            writeData(0, 0);

            serialPort.removeEventListener();
            serialPort.close();
            if (input != null){
                input.close();
            }

            if (output != null){
                output.close();
            }

            setConnected(false);
            //gsWindow.keybindingController.toggleControls();

            logText = "Disconnected.";
            gsWindow.connectionStatusLabel.setText(logText);
//            gsWindow.txtLog.setForeground(Color.red);
//            gsWindow.txtLog.append(logText + "\n");
        }
        catch (Exception e)
        {
            logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
            gsWindow.connectionStatusLabel.setText(logText);
//            gsWindow.txtLog.setForeground(Color.red);
//            gsWindow.txtLog.append(logText + "\n");
        }
    }
	
    public void serialEvent(SerialPortEvent evt) {
    	if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                byte singleData = (byte)input.read();

                if (singleData != NEW_LINE_ASCII)
                {
                    logText = new String(new byte[] {singleData});
                    gsWindow.commPortReadTextArea.append(logText);
                    System.out.println("logText");
                }
                else
                {
                	gsWindow.commPortReadTextArea.append("\n");
                	System.out.println("\n");
                }
            }
            catch (Exception e)
            {
                logText = "Failed to read data. (" + e.toString() + ")";
                //gsWindow.commPortReadTextArea.setForeground(Color.red);
                gsWindow.commPortReadTextArea.append(logText + "\n");
            }
        }
    }
    
    public boolean initIOStream()
    {
        //return value for whather opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            writeData(0, 0);
            
            successful = true;
            return successful;
        }
        catch (IOException e) {
            logText = "I/O Streams failed to open. (" + e.toString() + ")";
            //gsWindow.txtLog.setForeground(Color.red);
            gsWindow.commPortReadTextArea.append(logText + "\n");
            return successful;
        }
    }

    //starts the event listener that knows whenever data is available to be read
    //pre: an open serial port
    //post: an event listener for the serial port that knows when data is recieved
    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            logText = "Too many listeners. (" + e.toString() + ")";
            //gsWindow.txtLog.setForeground(Color.red);
            gsWindow.commPortReadTextArea.append(logText + "\n");
        }
    }
    
    
    
    public void writeData(int leftThrottle, int rightThrottle)
    {
        try
        {
            output.write(leftThrottle);
            output.flush();
            //this is a delimiter for the data
            output.write(DASH_ASCII);
            output.flush();
            
            output.write(rightThrottle);
            output.flush();
            //will be read as a byte so it is a space key
            output.write(SPACE_ASCII);
            output.flush();
        }
        catch (Exception e)
        {
            logText = "Failed to write data. (" + e.toString() + ")";
            //gsWindow.txtLog.setForeground(Color.red);
            gsWindow.commPortReadTextArea.append(logText + "\n");
        }
    }


    final public boolean getConnected()
    {
        return bConnected;
    }

    public void setConnected(boolean bConnected)
    {
        this.bConnected = bConnected;
    }
}

