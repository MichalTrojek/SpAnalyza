package com.mtr.application.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.mtr.application.Model;
import com.mtr.application.shared.Message;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LoadOfflineAnalysisAsyncTask extends AsyncTask<String, Integer, Void> {


    private Context context;
    private String filePath;
    private String message;
    private AlertDialog loadingDialog;
    private Message mes;

    public LoadOfflineAnalysisAsyncTask(Context c, AlertDialog loadingDialog, String filePath) {
        this.context = c;
        this.loadingDialog = loadingDialog;
        this.filePath = filePath;
    }

    @Override
    protected Void doInBackground(String... voids) {
        this.message = voids[0];
        readAnalysisFile(filePath);
        return null;
    }

    private void readAnalysisFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mes = (Message) ois.readObject();
            Model.getInstance().setAnalysis(mes.getAnalysis());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPreExecute() {
        loadingDialog.show();
    }


    //
    @Override
    protected void onPostExecute(Void v) {
        loadingDialog.hide();
        if (mes.getAnalysis().size() > 0) {
            Toast.makeText(context, String.format("Data byla nahraná\nAnalyza: %d", Model.getInstance().getAnalysis().size()), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Nahrávání selhalo", Toast.LENGTH_LONG).show();
        }

    }

}
