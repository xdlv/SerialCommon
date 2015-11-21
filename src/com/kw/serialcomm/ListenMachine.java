package com.kw.serialcomm;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;

import java.io.InputStream;

/**
 * Created by Administrator on 2015/10/18.
 */
public class ListenMachine {
    enum STATUS {START, STOP};
    STATUS status = STATUS.STOP;
    SerialPort serialPort;
    ReadThread.RxtxListener listener;
    ReadThread thread;
    ListenMachine(ReadThread.RxtxListener listener){
        this.listener = listener;
    }

    void start(String portName, int rate, int data, int stopBit, int parity) throws Exception {
        if (status == STATUS.START){
            return;
        }
        try {
            serialPort = connectSerialPort(portName, rate, data, stopBit, parity);
            InputStream inputStream = serialPort.getInputStream();
            thread = new ReadThread(inputStream,listener).initAndRun();
            status = STATUS.START;
        } catch (NoSuchPortException e) {
            throw new Exception("No such serial port:" + portName);
        }
    }

    private SerialPort connectSerialPort(String portName, int rate, int data, int stopBit, int parity) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()){
            throw new Exception("the serial port is currently used:" + portName);
        }
        CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
        if (!(commPort instanceof SerialPort)){
            throw new Exception("this port is not serial port: " + portName);
        }
        SerialPort retPort = (SerialPort) commPort;
        retPort.setSerialPortParams(rate, data, stopBit, parity);
        return retPort;
    }

    void stop(){
        thread.close();
        thread = null;
        serialPort.close();
        status = STATUS.STOP;
    }

    void write(byte[] buffer, String portName, int rate, int data, int stopBit, int parity) throws Exception {
        SerialPort serialPort = null;
        try {
            serialPort = connectSerialPort(portName,rate,data,stopBit,parity);
            serialPort.getOutputStream().write(buffer);
        } finally {
            if (serialPort != null){
                serialPort.close();
            }
        }

    }

}
