package com.kw.serialcomm;

import gnu.io.SerialPort;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SwingDemoMain {
    private JTextField portTextField;
    ListenMachine machine;

    public SwingDemoMain(final JFrame frame) {
        machine = new ListenMachine(new ReadThread.StreamListener() {
            StringBuffer buffer = new StringBuffer();
            @Override
            public void listen(String result) {
                receivedTextArea.append(result + "\n");
                buffer.append(result);
                if (buffer.length() > 500) {
                    String content = buffer.toString();
                    buffer.setLength(0);
                    int index = 0;
                    int index2 = 0;
                    while( (index = content.indexOf("$GPGGA")) > -1){
                        index2 = content.indexOf("*",index);
                        if (index2 > -1 && index2 + 3 <= content.length() ){
                            SerialUtil.writeContentToFile("result.txt"
                                    ,content.substring(index,index2 + 3) + "\r\n");
                            content = content.substring(index2);
                        }
                    }
                    //buffer.append(content);
                }
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startButton.getText().equals("Stop")) {
                    machine.stop();
                    startButton.setText("Start Listening");
                    triggleUI(true);
                    return;
                }

                if (portTextField.getText().isEmpty()) {
                    SerialUtil.showMessageDialog(frame, "Serial Port must be assigned");
                    return;
                }
                String portName = portTextField.getText().trim();
                int rate = Integer.parseInt(rateComboBox.getSelectedItem().toString());
                int data = Integer.parseInt(dataComboBox.getSelectedItem().toString());
                int stopBit = Integer.parseInt(stopComboBox.getSelectedItem().toString());

                try {
                    machine.start(portName, rate, data, stopBit, SerialPort.PARITY_NONE);
                    startButton.setText("Stop");
                    triggleUI(false);
                } catch (Exception e1) {
                    SerialUtil.showMessageDialog(frame, e1.getMessage());
                }
            }
        });
        final Pattern pattern = Pattern.compile("#\\{char=(\\d+)\\}");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (portTextField.getText().isEmpty()) {
                    SerialUtil.showMessageDialog(frame, "Serial Port must be assigned");
                    return;
                }
                String content = sendTextArea.getText();
                if (content.isEmpty()) {
                    return;
                }
                String portName = portTextField.getText().trim();
                int rate = Integer.parseInt(rateComboBox.getSelectedItem().toString());
                int data = Integer.parseInt(dataComboBox.getSelectedItem().toString());
                int stopBit = Integer.parseInt(stopComboBox.getSelectedItem().toString());
                //process the #{char:1}
                StringBuffer buffer = new StringBuffer();
                int index = 0;
                Matcher matcher = pattern.matcher(content);
                while (matcher.find()){
                    buffer.append(content.substring(index, matcher.start()));
                    buffer.append((char)Byte.parseByte(matcher.group(1)));
                    index = matcher.end();
                }
                buffer.append(content.substring(index));
                try {
                    machine.write(buffer.toString().getBytes(), portName, rate, data, stopBit, SerialPort.PARITY_NONE);
                } catch (Exception e1) {
                    SerialUtil.showMessageDialog(frame, e1.getMessage());
                }
            }
        });
    }

    private void triggleUI(boolean enable) {
        portTextField.setEnabled(enable);
        rateComboBox.setEnabled(enable);
        dataComboBox.setEnabled(enable);
        stopComboBox.setEnabled(enable);
        parityComboBox.setEnabled(enable);
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        JFrame frame = new JFrame("Serial Port Tool");
        frame.setContentPane(new SwingDemoMain(frame).contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024,768);
        frame.setVisible(true);
    }

    private JPanel contentPanel;
    private JPanel serialPortPanel;
    private JComboBox rateComboBox;
    private JComboBox dataComboBox;
    private JComboBox parityComboBox;
    private JComboBox stopComboBox;
    private JTextArea receivedTextArea;
    private JButton startButton;
    private JTextArea sendTextArea;
    private JButton sendButton;
}
