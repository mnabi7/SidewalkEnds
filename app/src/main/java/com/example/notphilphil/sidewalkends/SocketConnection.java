package com.example.notphilphil.sidewalkends;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;

class SocketConnection implements Runnable {
    private Socket myConn;
    private String msg;
    private Thread thread;
    private int num;
    private static int count = 0;

    SocketConnection(String msg) {
        this.msg = msg;
        num = count++;
        try {
            Log.d("SocketConnection", "Trying to connect...");
            myConn = new Socket("143.215.113.43", 4000);
            Log.d("SocketConnection", "We connected! "+num);
            BufferedOutputStream dOut = new BufferedOutputStream(myConn.getOutputStream());
            BufferedInputStream dIn = new BufferedInputStream(myConn.getInputStream());
            boolean keepGoing = true;
            dOut.write(1);
            dOut.flush();
            Log.d("SocketConnection", "Finished writing");
            while (keepGoing) {
                int res = dIn.read();
                switch (res) {
                    case 0: Log.d("SocketConnection", "Got 0 as return byte"); break;
                    case 1:
//                        Log.d("SocketConnection", dIn.read());
                        BufferedReader newBr = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
                        Log.d("SocketConnection", newBr.readLine());
                        keepGoing = false;
                        break;
                    default: Log.d("SocketConnection", "We got nothing :("); keepGoing = false;
                }
            }
            Log.d("SocketConnection", "Finished reading");
            dOut.close();
            dIn.close();
            myConn.close();
        } catch(IOException err) {
            Log.d("SocketConnection", "Something went wrong => "+err.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            Log.d("SocketConnection", "Trying to connect...");
            myConn = new Socket("143.215.113.43", 4000);
            Log.d("SocketConnection", "We connected! "+num);
            PrintWriter out = new PrintWriter(myConn.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
            out.println(this.msg);
            if (this.msg.equals("get")) System.out.println(br.readLine());
        } catch(IOException err) {
            Log.d("SocketConnection", "Something went wrong => "+err.getMessage());
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.run();
        }
    }
}
