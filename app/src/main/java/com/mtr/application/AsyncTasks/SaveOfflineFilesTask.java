package com.mtr.application.AsyncTasks;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.mtr.application.Model;
import com.mtr.application.shared.ExportArticle;
import com.mtr.application.shared.Message;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveOfflineFilesTask extends AsyncTask<String, Integer, Void> {


    private Context context;
    private String message;
    private AlertDialog loadingDialog;
    private Message mes;

    public SaveOfflineFilesTask(Context c, AlertDialog loadingDialog) {
        this.context = c;
        this.loadingDialog = loadingDialog;

    }

    @Override
    protected Void doInBackground(String... voids) {
        this.message = voids[0];
        createOfflineFiles(Model.getInstance().getOrders(), Model.getInstance().getReturns());
        return null;
    }


    @Override
    protected void onPreExecute() {
        loadingDialog.show();
    }


    //
    @Override
    protected void onPostExecute(Void v) {
        loadingDialog.hide();
        Toast.makeText(context, "Data uložena", Toast.LENGTH_LONG).show();

    }

    private void createOfflineFiles(ArrayList<ExportArticle> orders, ArrayList<ExportArticle> returns) {
        createExcelReturnsFile(returns);
        createExcelOrdersFile(orders);
    }


    public void createExcelReturnsFile(ArrayList<ExportArticle> returns) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet();
        createFirstRow(sheet1, "Vratka");
        int i = 1;
        for (ExportArticle e : returns) {
            Row row = sheet1.createRow(i);
            row.createCell(0).setCellValue(e.getRank());
            row.createCell(1).setCellValue(e.getFirstCode());
            row.createCell(2).setCellValue(e.getEan());
            row.createCell(3).setCellValue(e.getName());
            row.createCell(4).setCellValue(e.getSales());
            row.createCell(5).setCellValue(e.getRevenue());
            row.createCell(6).setCellValue(e.getStoredAmount());
            row.createCell(7).setCellValue(e.getLocations());
            row.createCell(8).setCellValue(e.getPrice());
            row.createCell(9).setCellValue(e.getSupplier());
            row.createCell(10).setCellValue(e.getAuthor());
            row.createCell(11).setCellValue(e.getDateOfLastSale());
            row.createCell(12).setCellValue(e.getDateOfLastDelivery());
            row.createCell(13).setCellValue(e.getRealeaseDate());
            row.createCell(14).setCellValue(e.getDeliveredAs());
            row.createCell(15).setCellValue(e.getEshopRank());
            row.createCell(16).setCellValue(e.getExportAmount());
            i++;
        }

        try {
            if (isExternalStorageWritable()) {
                String filename = "VR_" + returnDate() + ".xlsx";
                File file = new File(Environment.getExternalStorageDirectory(), filename);
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
                MediaScannerConnection.scanFile(
                        context,
                        new String[]{file.getAbsolutePath()}, // "file" was created with "new File(...)"
                        null,
                        null);
            } else {
                System.out.println("external storage is not writable");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createExcelOrdersFile(ArrayList<ExportArticle> orders) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet();
        createFirstRow(sheet1, "Objednávka");
        int i = 1;
        for (ExportArticle e : orders) {
            Row row = sheet1.createRow(i);
            row.createCell(0).setCellValue(e.getRank());
            row.createCell(1).setCellValue(e.getFirstCode());
            row.createCell(2).setCellValue(e.getEan());
            row.createCell(3).setCellValue(e.getName());
            row.createCell(4).setCellValue(e.getSales());
            row.createCell(5).setCellValue(e.getRevenue());
            row.createCell(6).setCellValue(e.getStoredAmount());
            row.createCell(7).setCellValue(e.getLocations());
            row.createCell(8).setCellValue(e.getPrice());
            row.createCell(9).setCellValue(e.getSupplier());
            row.createCell(10).setCellValue(e.getAuthor());
            row.createCell(11).setCellValue(e.getDateOfLastSale());
            row.createCell(12).setCellValue(e.getDateOfLastDelivery());
            row.createCell(13).setCellValue(e.getRealeaseDate());
            row.createCell(14).setCellValue(e.getDeliveredAs());
            row.createCell(15).setCellValue(e.getEshopRank());
            row.createCell(16).setCellValue(e.getExportAmount());
            i++;
        }

        try {
            if (isExternalStorageWritable()) {
                String filename = "OBJ_" + returnDate() + ".xlsx";
                File file = new File(Environment.getExternalStorageDirectory(), filename);
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
                MediaScannerConnection.scanFile(
                        context,
                        new String[]{file.getAbsolutePath()}, // "file" was created with "new File(...)"
                        null,
                        null);
            } else {
                System.out.println("external storage is not writable");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    private void createFirstRow(Sheet sheet1, String text) {
        Row row = sheet1.createRow(0);
        row.createCell(0).setCellValue("Pořadí");
        row.createCell(1).setCellValue("Kód");
        row.createCell(2).setCellValue("Ean");
        row.createCell(3).setCellValue("Název");
        row.createCell(4).setCellValue("Prodej");
        row.createCell(5).setCellValue("Obrat");
        row.createCell(6).setCellValue("Stav skladu");
        row.createCell(7).setCellValue("Lokace");
        row.createCell(8).setCellValue("DPC");
        row.createCell(9).setCellValue("Dodavatel");
        row.createCell(10).setCellValue("Autor");
        row.createCell(11).setCellValue("Datum posledního prodeje");
        row.createCell(12).setCellValue("Datumm posledního příjmu");
        row.createCell(13).setCellValue("Datum vydání");
        row.createCell(14).setCellValue("Řada posledního příjmu");
        row.createCell(15).setCellValue("Pořadí eshop");
        row.createCell(16).setCellValue(text);
    }

    private String returnDate() {
        DateFormat sdf = new SimpleDateFormat("dd-MM-YYYY_HH-MM-SS");
        Date date = new Date();
        System.out.println(sdf.format(date));
        return sdf.format(date).toString();
    }

}
