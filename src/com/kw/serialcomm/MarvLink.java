package com.kw.serialcomm;

public class MarvLink {
    enum STATUS{START,VERIFY};
    public static void main(String[] args) throws Exception{
        if (args.length != 5){
            System.err.println("Usage: portName,rate, data, stopBit, parity");
            System.exit(-1);
            return;
        }
        new ListenMachine(new ReadThread.ByteListener(){
            STATUS status = STATUS.START;
            @Override
            public void listen(byte[] result, int count) {
                for (byte b : result){
                    switch (status){
                        case START:
                            if (b == 0xFE){
                                status = STATUS.VERIFY;
                            }
                            break;
                        case VERIFY:

                            break;
                    }
                    if (b == 0xFE){
                        status = STATUS.VERIFY;
                    }

                }
            }
        }).start(args[0],toInt(args[1]),toInt(args[2])
                ,toInt(args[3]),toInt(args[4]));
    }

    static int toInt(String s){
       return Integer.parseInt(s);
    }

}
