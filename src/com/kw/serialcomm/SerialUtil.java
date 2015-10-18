package com.kw.serialcomm;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Created by Administrator on 2015/10/18.
 */
public class SerialUtil {

    public static void showMessageDialog(Component componet, String msg) {
        JOptionPane.showMessageDialog(componet, msg);
    }

    static void writeContentToFile(String fileName, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(fileName), true);
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
