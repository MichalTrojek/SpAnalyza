package com.mtr.application;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class DatabaseLoaderAsyncTask extends AsyncTask<Void, Integer, HashMap<String, String>> {


    private Context context;
    DatabaseAccess db;

    private AlertDialog dialog;
    private ProgressBar progress;
    private TextView loadingInfoTextView;
    int max;


    public DatabaseLoaderAsyncTask(Context context, AlertDialog dialog, ProgressBar progress, TextView loadingInfoTextView) {
        this.context = context;
        this.db = db;
        this.dialog = dialog;
        this.progress = progress;
        this.loadingInfoTextView = loadingInfoTextView;
        this.db = DatabaseAccess.getInstance(context);
        db.open();
    }

    @Override
    protected void onPreExecute() {
        max = (int) db.getTableSize();
        progress.setMax(max);
        progress.setProgress(0);
        dialog.show();

    }


    @Override
    protected HashMap<String, String> doInBackground(Void... voids) {
        HashMap<String, String> map = new HashMap<>();
        Cursor cursor = db.getDb().rawQuery("Select EAN, NAME from articles", new String[]{});
        cursor.moveToFirst();
        int progressCounter = 0;
        while (!cursor.isAfterLast()) {
            progressCounter++;
            publishProgress(progressCounter);
            String ean = cursor.getString(0);
            String name = cursor.getString(1);
            map.put(name, ean);
            cursor.moveToNext();
        }
        return map;
    }

//    @Override
//    protected HashMap<String, String> doInBackground(Void... voids) {
//        HashMap<String, String> eanIsKey = new HashMap<>();
//        HashMap<String, String> nameIsKey = new HashMap<>();
//        List<HashMap<String, String>> listOfMaps = new ArrayList<>();
//        Cursor cursor = db.getDb().rawQuery("Select EAN, NAME from articles", new String[]{});
//        cursor.moveToFirst();
//        int progressCounter = 0;
//        while (!cursor.isAfterLast()) {
//            progressCounter++;
//            publishProgress(progressCounter);
//            String ean = cursor.getString(0);
//            String name = cursor.getString(1);
//            eanIsKey.put(ean, name);
//            nameIsKey.put(name, ean);
//            cursor.moveToNext();
//        }
//
//        listOfMaps.add(eanIsKey);
//        listOfMaps.add(nameIsKey);
//        return eanIsKey;
//    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progress.setProgress(values[0]);
        loadingInfoTextView.setText(values[0] + "/" + max);
    }


    @Override
    protected void onPostExecute(HashMap<String, String> map) {
        db.close();
        Model.getInstance().setMapOfNames(map);
        Toast.makeText(context, "Nahráno " + map.size() + " položek", Toast.LENGTH_SHORT).show();
        dialog.hide();
    }

}

