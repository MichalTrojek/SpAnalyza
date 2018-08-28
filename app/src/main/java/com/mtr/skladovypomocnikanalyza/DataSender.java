package com.mtr.skladovypomocnikanalyza;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class DataSender extends AsyncTask<String, Void, Void> {

    private boolean connectedToServer = true;
    private int port = 8799;
    private Context context;
    private String ip;


    public DataSender(String ip, Context c) {
        this.ip = ip;
        this.context = c;
    }

    String message;

    @Override
    protected Void doInBackground(String... voids) {
        message = voids[0];
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), 500);
            System.out.println("Connected");
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                System.out.println("SENDING MESSAGE " + message);
                message = message.replaceAll("ě", "e");
                message = message.replaceAll("š", "s");
                message = message.replaceAll("č", "c");
                message = message.replaceAll("ř", "ř");
                message = message.replaceAll("ž", "z");
                message = message.replaceAll("ý", "y");
                message = message.replaceAll("á", "a");
                message = message.replaceAll("í", "i");
                message = message.replaceAll("é", "e");
                message = message.replaceAll("ů", "u");
                message = message.replaceAll("ú", "u");
                message = message.replaceAll("ň", "n");
                message = message.replaceAll("ď", "d");
                message = message.replaceAll("ť", "t");

                out.write(message);
            } catch (NullPointerException e) {
                e.printStackTrace();
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
            if ("~o~".equalsIgnoreCase(message) || "objvr~o~".equalsIgnoreCase(message)  || "obj~o~".equalsIgnoreCase(message) || "vr~o~".equalsIgnoreCase(message) ) {
                Toast.makeText(context, "Žádná data k vyexportování.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Data byla vyexportována.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Exportování selhalo.", Toast.LENGTH_LONG).show();
        }

    }

}
