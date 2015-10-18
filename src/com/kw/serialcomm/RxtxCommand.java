package com.kw.serialcomm;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class RxtxCommand {

	interface StreamListener{
		void listen(String result);
	}
	StreamListener listener = null;
	
	
	ReadThread readThread;
	
	class ReadThread extends Thread{
		boolean close = false;
		InputStream inputStream = null;
		public ReadThread(InputStream inputStream) {
			this.inputStream = inputStream;
		}
		public void run(){
			byte[] buffer = new byte[2048];
			int count = 0;
			while (!close){
				try {
					if (inputStream.available() > 0){
						count = inputStream.read(buffer);
						if (count < 0){
							System.out.println("Thread exit for input stream exit");
							return;
						}
						if (listener!= null){
							listener.listen(new String(buffer,0,count));
						}
					} else {
						Thread.sleep(200);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		ReadThread init(){
			setName("ReadThread");
			return this;
		}
	}
	
	
	void startListen(String portName, int rate,int dataBit, int stopBit, int parity) throws Exception{
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() ) {
            throw new Exception(portName + " is currently used!");
        }
        CommPort commPort = portIdentifier.open(RxtxCommand.class.getName(),2000);
        if (commPort instanceof SerialPort){
        	 SerialPort serialPort = (SerialPort) commPort;
             serialPort.setSerialPortParams(57600,dataBit,stopBit,parity);
             
        } else {
        	throw new Exception(portName + " is not serial port type.");
        }
	}
	
	static List<SerialPort> getCurrentNotUsedPort(){
		Enumeration enumeration = CommPortIdentifier.getPortIdentifiers();
		List<SerialPort> ports = new ArrayList<SerialPort>();
		while (enumeration.hasMoreElements()){
			CommPortIdentifier portIdentifier = (CommPortIdentifier)enumeration.nextElement();
			if (portIdentifier.isCurrentlyOwned()){
				System.out.println(portIdentifier.getCurrentOwner() + " is used currently.");
				continue;
			}
			
		}
		return ports;
	}
}
