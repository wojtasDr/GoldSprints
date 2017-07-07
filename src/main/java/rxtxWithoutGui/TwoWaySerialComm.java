package rxtxWithoutGui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import TigerControlPanel.GUI;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

public class TwoWaySerialComm
{
	private List<Integer> sensor1Revolutions = new ArrayList<Integer>();
	private int measurementCounter = 0;
	public void countVelocity(){
		//this.collectData(revolutions);
		
	}
	
	public List<Integer> collectData(String revolutions){
		Float velocity = new Float(0);
		System.out.println("Rev: " + revolutions);
		System.out.println("Rev number:" + revolutions.substring(revolutions.indexOf(":")+1, revolutions.length()).trim());

		Integer revNumber = Integer.parseInt(revolutions.substring(revolutions.indexOf(":") + 1, revolutions.length()).trim());

		if(sensor1Revolutions.size() == 5){
			sensor1Revolutions.remove(measurementCounter);
		}
		
		sensor1Revolutions.add(measurementCounter, revNumber);

		measurementCounter++;
		if (measurementCounter == 5){
			measurementCounter = 0;
		}
		
		System.out.println("Rev list: " + sensor1Revolutions.toString());
		
		if (sensor1Revolutions.size() >= 5) {
			for (Integer rev : sensor1Revolutions) {
				velocity = velocity + (rev * 2.1f * 3.6f);
			}
			velocity = velocity / 5;
		}
		System.out.println("V: " + velocity + "km/h");
		
		return sensor1Revolutions;
	}
	
    public TwoWaySerialComm()
    {
        super();
    }

    
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                
                (new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out))).start();  
            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    /** */
    public static class SerialReader implements Runnable 
    {
        InputStream in;
        TwoWaySerialComm o;
        public SerialReader ( InputStream in )
        {
        	
            this.in = in;
        }
        
        public void run ()
        {    
        	TwoWaySerialComm o = new TwoWaySerialComm();
            byte[] buffer = new byte[1024];
            int len = -1;
            String sb = "";
            String receivedRevolution = "";
            try
            {
				while ((len = this.in.read(buffer)) > -1) {
					sb = new String(buffer, 0, len);
					if(!sb.isEmpty()){
						
						receivedRevolution += sb; 
						
						if(receivedRevolution.contains("\n")){
							//System.out.print(receivedRevolution);
							o.collectData(receivedRevolution);
							receivedRevolution = "";	
						} 
					}

				}
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }

    /** */
    public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {                
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
    
    public static void main ( String[] args )
    {
        try
        {
            (new TwoWaySerialComm()).connect("COM5");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
