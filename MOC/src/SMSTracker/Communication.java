package SMSTracker;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TimerTask;
import java.util.TooManyListenersException;


public class Communication  extends TimerTask {//Runnable{
	
	/**
	 * 
	 */
	public String main;

	CommPortIdentifier serialPortId;
	Enumeration enumComm;
	SerialPort serialPort;
	OutputStream outputStream;
	InputStream inputStream;
	Boolean serialPortGeoeffnet = false;

	int baudrate = 19200;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	String portName = "/dev/tty.usbserial-013920002147B";
	String atCommand;

		
    public Communication(String atCommand) {
    	this.atCommand = atCommand;
    }

	public void run()
    {
        if (oeffneSerialPort(portName) != true)
        	return;

		sendeSerialPort(atCommand);
			
    }
    
	boolean oeffneSerialPort(String portName)
	{
		Boolean foundPort = false;
		if (serialPortGeoeffnet != false) {
			System.out.println("Serialport bereits geöffnet");
			return true;
		}
		System.out.println("öffne Serialport");
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while(enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			if (portName.contentEquals(serialPortId.getName())) {
				foundPort = true;
				break;
			}
		}
		if (foundPort != true) {
			System.out.println("Serialport nicht gefunden: " + portName);
			return false;
		}
		try {
			serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", 500);
		} catch (PortInUseException e) {
			System.out.println("Port belegt");
		}
		try {
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			System.out.println("Keinen Zugriff auf OutputStream");
		}

		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
			System.out.println("Keinen Zugriff auf InputStream");
		}
		try {
			serialPort.addEventListener(new serialPortEventListener());
		} catch (TooManyListenersException e) {
			System.out.println("TooManyListenersException für Serialport");
		}
		serialPort.notifyOnDataAvailable(true);
	
		try {
			serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
		} catch(UnsupportedCommOperationException e) {
			System.out.println("Konnte Schnittstellen-Paramter nicht setzen");
		}
		
		serialPortGeoeffnet = true;
		return true;
	}

	void schliesseSerialPort()
	{

		if ( serialPortGeoeffnet == true) {
			System.out.println("Schliesse Serialport");

			serialPort.close();
			serialPortGeoeffnet = false;	

		} else {
			System.out.println("Serialport bereits geschlossen");
		}
		
	}
	
	void sendeSerialPort(String nachricht)
	{
		//System.out.println("Sende: " + nachricht);
		if (serialPortGeoeffnet != true)
			return;
		try {
			outputStream.write(nachricht.getBytes());
 
		} catch (IOException e) {
			System.out.println("Fehler beim Senden");
		}
	}
	
	void serialPortDatenVerfuegbar() {
		try {
			byte[] data = new byte[150];
			int num;
			while(inputStream.available() > 0) {
				try {
					Thread.sleep(2000);
					//System.out.println("Warten...");
				} catch (InterruptedException e) { 

				}
				num = inputStream.read(data, 0, data.length);
				//System.out.println(new String(data, 0, num)); /* Antwort */
				String antwort = new String(data, 13, 82);
				System.out.println(antwort);
				
				
				DBController db = DBController.getInstance();
				db.handleDB(antwort);
			}
			inputStream.reset();
		} catch (IOException e) {
			//System.out.println("Fehler beim Lesen empfangener Daten");
		}

		//schliesseSerialPort();
		
	}
	
	class serialPortEventListener implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			System.out.println("serialPortEventlistener");
			switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				serialPortDatenVerfuegbar();
				break;
			case SerialPortEvent.BI:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.FE:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			case SerialPortEvent.PE:
			case SerialPortEvent.RI:		
			default:

			}
		}
		
	}
	


	
}