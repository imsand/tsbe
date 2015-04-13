package SMS;
		import gnu.io.*;

		import java.io.IOException;
		//import java.io.InputStream;
		import java.io.OutputStream;
import java.util.Enumeration;
		//import java.util.TooManyListenersException;

		//import OeffnenUndSenden.serialPortEventListener;

public class EinfachSenden implements Runnable {

			/**
			 * @param args
			 */
			public static void main(String[] args)
			{
				Runnable runnable = new EinfachSenden();
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
			// InputStream inputStream;
			Boolean serialPortGeoeffnet = false;

			int baudrate = 19200;
			int dataBits = SerialPort.DATABITS_8;
			int stopBits = SerialPort.STOPBITS_1;
			int parity = SerialPort.PARITY_NONE;
			String portName = "/dev/tty.usbserial-013920002092B";
			
			int secondsRuntime = 20;

			public void Sender()
			{
				System.out.println("Konstruktor: EinfachSenden");
			}
			
		    public void run()
		    {
		        Integer secondsRemaining = secondsRuntime;
		        if (oeffneSerialPort(portName) != true)
		        	return;
		        
			while (secondsRemaining > 0) {
				System.out.println("Sekunden verbleiben: " + secondsRemaining.toString() );
				secondsRemaining--;
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) { }
				sendeSerialPort("AT+CMGF=1\r\n");
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) { }
				sendeSerialPort("AT+CMGS=\"0795385277\"\r\n");	
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) { }
				sendeSerialPort("hallo bin Java\r\n");
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) { }
				sendeSerialPort("\032"); 
				}
			
			schliesseSerialPort();
		}
		    
			boolean oeffneSerialPort(String portName)
			{
				Boolean foundPort = false;
				if (serialPortGeoeffnet != false) {
					System.out.println("Serialport bereits geöffnet");
					return false;
				}
				System.out.println("Öffne Serialport");
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
		/*
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
		*/
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
					System.out.println("Schließe Serialport");
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
		}