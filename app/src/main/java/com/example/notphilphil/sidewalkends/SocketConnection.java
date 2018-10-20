package com.example.notphilphil.sidewalkends;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

class SocketConnection extends AsyncTask<Void, Void, Void> {
    private Socket myConn;
    private String msg;
    private int num;
    private static int count = 0;

    SocketConnection(String msg) {
        this.msg = msg;
        num = count++;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Log.d("SocketConnection", "Trying to connect...");
            myConn = new Socket("143.215.113.43", 4000);
            PrintWriter out = new PrintWriter(myConn.getOutputStream());
            Log.d("SocketConnection", "We connected! "+num);
            out.printf(this.msg);
            out.flush();
            out.close();
            myConn.close();
        } catch(IOException err) {
            Log.d("SocketConnection", "Something went wrong => "+err.getMessage());
        }
        return null;
    }
}
