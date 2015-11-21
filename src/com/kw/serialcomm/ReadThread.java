package com.kw.serialcomm;

import java.io.InputStream;

/**
 * Created by Administrator on 2015/10/18.
 */
public class ReadThread extends Thread {
    boolean close = false;
    InputStream inputStream = null;
    RxtxListener listener;

    public ReadThread(InputStream inputStream, RxtxListener listener) {
        this.inputStream = inputStream;
        this.listener = listener;
    }

    void close(){
        close = true;
    }
    public void run() {
        byte[] buffer = new byte[2048];
        int count = 0;
        while (!close) {
            try {
                if (inputStream.available() > 0) {
                    count = inputStream.read(buffer);
                    if (count < 0) {
                        System.out.println("Thread exit for input stream exit");
                        return;
                    }
                    if (listener == null){
                        continue;
                    }
                    if (listener instanceof StreamListener){
                        ((StreamListener)listener).listen(new String(buffer, 0, count));
                    }
                    if (listener instanceof ByteListener){
                        ((ByteListener)listener).listen(buffer,count);
                    }
                } else {
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ReadThread initAndRun() {
        setName("ReadThread");
        start();
        return this;
    }
    interface RxtxListener{}
    interface StreamListener extends RxtxListener{
        void listen(String result);
    }
    interface ByteListener extends RxtxListener{
        void listen(byte[] result, int count);
    }
}
