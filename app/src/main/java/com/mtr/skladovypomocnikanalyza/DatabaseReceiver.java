package com.mtr.skladovypomocnikanalyza;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class DatabaseReceiver extends AsyncTask<String, Void, Void> {

    private boolean connectedToServer = true;
    private int port = 9777;
    private Context context;
    private String ip;
    String data = "";


    public DatabaseReceiver(String ip, Context c) {
        this.ip = ip;
        this.context = c;
    }


    @Override
    protected Void doInBackground(String... voids) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), 500);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "CP1250"))) {
                data = br.readLine();
//                Model.getInstance().setDatabaseData(data);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            connectedToServer = false; //  this means that server is offline
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void v) {
        if (connectedToServer) {
            System.out.println(data);
        } else {

        }
        System.out.println("DATABAZE " + data.length());
    }

}
