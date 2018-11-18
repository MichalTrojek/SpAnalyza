
package com.mtr.application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mtr.application.adapters.ExportInfoAdapter;
import com.mtr.application.shared.ExportArticle;

import java.util.ArrayList;
import java.util.List;

import application.R;

public class ChangesInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView exportInfoListView;
    private AlertDialog printDialog;
    private WebView mWebView;
    private List<PrintJob> mPrintJobs;
    private ArrayList<ExportArticle> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changes_info);
        setupListeners();


        createPrintDialog();

        if (!Model.getInstance().getOrders().isEmpty()) {
            ExportArticle article = new ExportArticle();
            article.setName("Objednávky");
            list.add(article);
            for (ExportArticle e : Model.getInstance().getOrders()) {
                list.add(e);
            }
        }

        if (!Model.getInstance().getReturns().isEmpty()) {
            ExportArticle article = new ExportArticle();
            article.setName("Vratky");
            list.add(article);
            for (ExportArticle e : Model.getInstance().getReturns()) {
                list.add(e);
            }
        }

        exportInfoListView = (ListView) findViewById(R.id.exportListView);
        exportInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (list.get(i).getName().equalsIgnoreCase("objednávky") || list.get(i).getName().equalsIgnoreCase("Vratky")) {
                    // do nothing
                } else {

                    Intent intent = new Intent(ChangesInfoActivity.this, MainActivity.class);
                    intent.putExtra("ean", list.get(i).getEan());
                    ChangesInfoActivity.this.startActivity(intent);
                }
            }
        });

        ExportInfoAdapter listAdapter = new ExportInfoAdapter(this, list.toArray(new ExportArticle[list.size()]));
        exportInfoListView.setAdapter(listAdapter);
    }


    private void setupListeners() {
        findViewById(R.id.exportInfoBackButton).setOnClickListener(this);
        findViewById(R.id.exportInfoPrintButton).setOnClickListener(this);
    }

    private void createPrintDialog() {
        View view = getLayoutInflater().inflate(R.layout.print_dialog, null);
        view.findViewById(R.id.printOrdersButton).setOnClickListener(this);
        view.findViewById(R.id.printReturnsButton).setOnClickListener(this);
        view.findViewById(R.id.backPrintButton).setOnClickListener(this);
        printDialog = new AlertDialog.Builder(this).setView(view).create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exportInfoBackButton:
                handleBackButton();
                break;
            case R.id.exportInfoPrintButton:
                handlePrintButton();
                break;
            case R.id.printOrdersButton:
                printOrders();
                break;
            case R.id.printReturnsButton:
                printReturns();
                break;
            case R.id.backPrintButton:
                hidePrintDialog();
                break;
            default:
                break;
        }
    }

    private void printOrders() {
        if (Model.getInstance().getOrders().isEmpty()) {
            Toast.makeText(this, "Objednávky jsou prázdné.", Toast.LENGTH_SHORT).show();
        } else {
            print(Model.getInstance().getOrders(), "Objednávka");
        }
    }

    private void printReturns() {
        if (Model.getInstance().getReturns().isEmpty()) {
            Toast.makeText(this, "Vratky jsou prázdné.", Toast.LENGTH_SHORT).show();
        } else {
            print(Model.getInstance().getReturns(), "Vratka");
        }
    }

    private void print(List<ExportArticle> list, String name) {
        WebView webview = new WebView(this);
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                System.out.println("Page finished loading " + url);
                createWebPrintJob(view);
                mWebView = null;
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append(name + "<BR/>");
        for (ExportArticle e : list) {
            sb.append("Počet :" + e.getExportAmount() + ", " + e.getName() + ", " + e.getEan() + ", lokace: " + e.getLocations() + "<BR/>");
        }
        String htmlDocument = "<html><body><p>" + sb.toString() + "</p></body></html>";
        webview.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
        mWebView = webview;
    }


    private void handleBackButton() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void handlePrintButton() {
        printDialog.show();
    }

    private void hidePrintDialog() {
        printDialog.hide();
    }


    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        mPrintJobs = printManager.getPrintJobs();
        String jobName = getString(R.string.app_name) + " Document";
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
        mPrintJobs.add(printJob);
    }


}
