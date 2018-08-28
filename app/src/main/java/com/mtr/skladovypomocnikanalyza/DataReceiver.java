package com.mtr.skladovypomocnikanalyza;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class DataReceiver extends AsyncTask<String, Void, Void> {

    private boolean connectedToServer = true;
    private int port = 9998;
    private Context context;
    private String ip;
    String data;


    public DataReceiver(String ip, Context c) {
        this.ip = ip;
        this.context = c;
    }


    @Override
    protected Void doInBackground(String... voids) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), 500);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "CP1250"))) {
                data = br.readLine();

                Model.getInstance().setData(data);
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
            if (data == null) {
                Toast.makeText(context, "Není vložen CSV soubor.", Toast.LENGTH_LONG).show();
            } else {
                Model.getInstance().convertStringToList();
                System.out.println("DATA BYLA NAHRANA");
                Toast.makeText(context, "Data byla nahrána.", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(context, "Nahrávaní dat selhalo.", Toast.LENGTH_LONG).show();
        }

    }

}
