package com.example.shubh.access;

/**
 * Created by shubh on 18/03/18.
 */
import android.os.AsyncTask;
import java.net.*;
import java.io.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AsyncTCPSend extends AsyncTask<Void, Void, Void>  {
    String address;
    int port;
    String message;
    String response = "";
    AsyncTCPSend(String addr, int p, String mes) {
        address = addr;
        port = p;
        message = mes;
    }
    AsyncTCPSend(String mes) {
        address = "172.20.10.2";
        port = 47002;
        message = mes;
    }


    @Override
    protected Void doInBackground(Void... params) {
        Socket socket = null;
        try {
            socket = new Socket(address, port);
            DataOutputStream writeOut = new DataOutputStream(socket.getOutputStream());
            writeOut.write(message.getBytes());
            android.util.Log.w("SENT", String.format("[%s] %d", message, message.length()));
            writeOut.flush();
        } catch (UnknownHostException e){
            e.printStackTrace();
            response = "Unknown HostException: " + e.toString();
            System.out.println(response);
        } catch (IOException e) {
            response = "IOException: " + e.toString();
            System.out.println(response);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
//        recieve.setText(response);
        super.onPostExecute(result);

    }


}
