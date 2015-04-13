package SMS;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;


public class SIM_Ready  implements Runnable{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Runnable runnable = new SIM_Ready();
		new Thread(runnable).start();
		System.out.println("main finished");
	}
	
	/**
	 * 
	 */

	CommPortIdentifier serialPortId;
	Enumeration enumComm;
	SerialPort serialPort;
	OutputStream outputStream;
	InputStream inputStream;
	Boolean serialPortGeoeffnet = false;
	Boolean sms = true;

	int baudrate = 19200;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	String portName = "COM7";

	public SIM_Ready()
	{
		System.out.println("Konstruktor: EinfachSenden");
	}
	

	public void Sender()
	{
		System.out.println("Konstruktor: EinfachSenden");
	}
	
    public void run()
    {

        if (oeffneSerialPort(portName) != true)
        	return;
        
        while (sms = true) {
		System.out.println("AT");
		try {
			Thread.sleep(10);
		} catch(InterruptedException e) { }
		sendeSerialPort("AT\r\n");
		try {
			Thread.sleep(10000);
			System.out.println("Warten 10 Sekunden");
		} catch(InterruptedException e) { }
        }	
}
    
	boolean oeffneSerialPort(String portName)
	{
		Boolean foundPort = false;
		if (serialPortGeoeffnet != false) {
			System.out.println("Serialport bereits geöffnet");
			return false;
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
			serialPort = (SerialPort) serialPortId.open("Ã–ffnen und Senden", 500);
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
			System.out.println("TooManyListenersException fÃ¼r Serialport");
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
		try {
			Thread.sleep(1000);
			System.out.println("Warten...");
		} catch (InterruptedException e) {

		}
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
		System.out.println("Sende: " + nachricht);
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
				num = inputStream.read(data, 0, data.length);
				System.out.println(new String(data, 0, num)); /* Antwort */
				
			}
		} catch (IOException e) {
			System.out.println("Fehler beim Lesen empfangener Daten");
		}
		schliesseSerialPort();
		
	}
	
	class serialPortEventListener implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			//System.out.println("serialPortEventlistener");
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