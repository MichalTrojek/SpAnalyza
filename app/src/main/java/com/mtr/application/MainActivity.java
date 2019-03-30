package com.mtr.application;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mtr.application.AsyncTasks.Client;
import com.mtr.application.AsyncTasks.LoadOfflineAnalysisAsyncTask;
import com.mtr.application.AsyncTasks.SaveOfflineFilesTask;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import application.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText eanInput;
    private Button editButton, deleteButton, scannerButton;
    private AlertDialog editDialog;
    private TextView bookNameTextView, bookEanTextView, totalAmountTextView, soldAmountTextView, supplierTextView, ipAddressTextView, amountInfoTextView, locationTextView, rankTextView, authorTextView, downloadDatabaseTextview;
    private EditText inputIpAddress;
    private AlertDialog ipDialog, deleteDialog, loadingAnalysisDialog, importDialog, exportDialog, savingDialog, exportInfoDialog, downloadingDialog;
    private final Activity scannerActivity = this;
    private static final int SCANNER_PERMISSION_REQUEST_CODE = 200;
    private ProgressBar downloadingDatabaseProgressBar;


    private String[] filePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    File file;

    ArrayList<String> pathHistory;
    //    String lastDirectory;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Model.getInstance().createSettings(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Model.getInstance().setContext(this);

        createImportAlertDialog();
        createExportAlertDialog();
        createLoadingAnalysisDialog();
        createSavingDialog();
        createIpAddressAlertDialog();
        createDeleteDialog();
        setToolbar();
        setButtons();
        setTextViews();
        setEanInputListenerToHandleEnterKey();
        setAds();
        createEditDialog();
        hideKeyboard(eanInput);

        if (getIntent().getStringExtra("ean") != null) {
            String ean = getIntent().getStringExtra("ean");
            Model.getInstance().scannerUpdate(ean, eanInput, bookNameTextView, bookEanTextView, totalAmountTextView, soldAmountTextView, supplierTextView, locationTextView, rankTextView, authorTextView);
        }


        amountInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Model.getInstance().getOrders().isEmpty() || !Model.getInstance().getReturns().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ChangesInfoActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Vratky/Objednávky jsou prázdné", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        handleLoadingAnalysis();
        handleLoadingOrdersAndReturns();
        askForPermission();
    }

    private void handleLoadingAnalysis() {
        Model.getInstance().loadAnalysis();
    }

    private void handleLoadingOrdersAndReturns() {
        Model.getInstance().loadOrdersAndReturns();
        if (!Model.getInstance().getOrders().isEmpty() || !Model.getInstance().getReturns().isEmpty()) {
            updateAmountInfo();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Model.getInstance().saveAnalysis();
        Model.getInstance().saveOrdersAndReturns();
    }


    // This creates a dialog window that shows up after pressing network settings button
    private void createIpAddressAlertDialog() {
        View view = getLayoutInflater().inflate(R.layout.edit_ip_dialog, null);
        view.findViewById(R.id.editIpButton).setOnClickListener(MainActivity.this);
        view.findViewById(R.id.backButtonIp).setOnClickListener(MainActivity.this);

        inputIpAddress = (EditText) view.findViewById(R.id.editIpText);
        ipAddressTextView = (TextView) view.findViewById(R.id.ipAddressTextView);
        ipAddressTextView.setText("IP Adresa : " + Model.getInstance().getIp());
        ipDialog = new AlertDialog.Builder(this).setView(view).create();
    }


    private void createImportAlertDialog() {
        View view = getLayoutInflater().inflate(R.layout.import_dialog, null);
        view.findViewById(R.id.offlineButton).setOnClickListener(this);
        view.findViewById(R.id.onlineButton).setOnClickListener(this);
        view.findViewById(R.id.importBackButton).setOnClickListener(this);
        importDialog = new AlertDialog.Builder(this).setView(view).create();
    }

    private void createExportAlertDialog() {
        View view = getLayoutInflater().inflate(R.layout.export_dialog, null);
        view.findViewById(R.id.offlineExportButton).setOnClickListener(this);
        view.findViewById(R.id.onlineExportButton).setOnClickListener(this);
        view.findViewById(R.id.backExportDialog).setOnClickListener(this);
        exportDialog = new AlertDialog.Builder(this).setView(view).create();
    }


    private void createDeleteDialog() {
        View view = getLayoutInflater().inflate(R.layout.delete_dialog, null);
        view.findViewById(R.id.printOrdersButton).setOnClickListener(MainActivity.this);
        view.findViewById(R.id.deleteOrdersButton).setOnClickListener(MainActivity.this);
        view.findViewById(R.id.printReturnsButton).setOnClickListener(MainActivity.this);
        view.findViewById(R.id.backDeleteButton).setOnClickListener(MainActivity.this);

        deleteDialog = new AlertDialog.Builder(this).setView(view).create();
    }

    private void createLoadingAnalysisDialog() {
        View view = getLayoutInflater().inflate(R.layout.loading_analysis_dialog, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        TextView loadingTextView = (TextView) view.findViewById(R.id.loadingTextView);
        progressBar.setVisibility(View.VISIBLE);
        loadingAnalysisDialog = new AlertDialog.Builder(this).setView(view).create();
    }

    private void createDownloadingDatabaseDialog() {
        View view = getLayoutInflater().inflate(R.layout.start_downloading_dialog, null);
        downloadingDatabaseProgressBar = view.findViewById(R.id.progressBar);
        downloadDatabaseTextview = (TextView) view.findViewById(R.id.loadingInfoTextView);
        downloadingDialog = new AlertDialog.Builder(this).setView(view).setCancelable(false).create();
    }

    private void createSavingDialog() {
        View view = getLayoutInflater().inflate(R.layout.saving_data_dialog, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        TextView loadingTextView = (TextView) view.findViewById(R.id.loadingTextView);
        progressBar.setVisibility(View.VISIBLE);
        savingDialog = new AlertDialog.Builder(this).setView(view).create();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                handleSettingsButton();
                return true;
            case R.id.importDataButton:
                handleImportDataButton();
                return true;
            case R.id.exportButton:
                handleExportButton();
                return true;
            case R.id.search:
                handleSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void handleSearch() {
        Intent intent = new Intent(this, FindByNameActivity.class);
        startActivity(intent);
    }


    private void handleSettingsButton() {
        ipDialog.show();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        toolbar.setTitle("SP");
        setSupportActionBar(toolbar);
    }

    private void setButtons() {
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
        scannerButton = (Button) findViewById(R.id.scannerButton);
        scannerButton.setOnClickListener(this);

    }

    private void setTextViews() {
        bookNameTextView = (TextView) findViewById(R.id.bookNameTextView);
        bookEanTextView = (TextView) findViewById(R.id.bookEanTextView);
        bookEanTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = bookEanTextView.getText().toString();
                text = text.substring(4, 18).trim();
                Toast.makeText(MainActivity.this, "EAN ULOŽEN.", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ean", text);
                clipboard.setPrimaryClip(clip);
            }
        });
        totalAmountTextView = (TextView) findViewById(R.id.totalAmountTextView);
        supplierTextView = (TextView) findViewById(R.id.supplierTextView);
        soldAmountTextView = (TextView) findViewById(R.id.soldAmountTextView);
        amountInfoTextView = (TextView) findViewById(R.id.amountInfoTextView);
        locationTextView = (TextView) findViewById(R.id.locTextView);
        rankTextView = (TextView) findViewById(R.id.rankTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
    }


    private void setEanInputListenerToHandleEnterKey() {
        eanInput = (EditText) findViewById(R.id.eanInput);
        eanInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent keyevent) {
                return handleEnter(keyCode, keyevent);
            }
        });

    }

    private boolean handleEnter(int keyCode, KeyEvent keyevent) {
        if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            clearAllTextViews();
            Model.getInstance().updateDisplay(eanInput, bookNameTextView, bookEanTextView, totalAmountTextView, soldAmountTextView, supplierTextView, locationTextView, rankTextView, authorTextView);
            hideKeyboard(eanInput);
            eanInput.setText("");
            return true;
        }
        return false;
    }


    private void setAds() {
        MobileAds.initialize(this, "ca-app-pub-6403268384265634~6879673047");
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
    }

    private EditText returnAmountInput;
    private EditText orderAmountInput;

    private void createEditDialog() {
        View view = getLayoutInflater().inflate(R.layout.edit_layout, null);
        view.findViewById(R.id.backButton).setOnClickListener(MainActivity.this);
        view.findViewById(R.id.saveButton).setOnClickListener(MainActivity.this);
        returnAmountInput = view.findViewById(R.id.returnAmountInput);
        orderAmountInput = view.findViewById(R.id.orderAmountInput);
        editDialog = new AlertDialog.Builder(this).setView(view).create();
    }

    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editButton:
                handleEditButton();
                break;
            case R.id.backButton:
                handleBackButton();
                break;
            case R.id.saveButton:
                handleSaveButton();
                break;
            case R.id.backButtonIp:
                handleIpBackButton();
                break;
            case R.id.editIpButton:
                handleEditIpButton();
                break;
            case R.id.deleteOrdersButton:
                handleDeleteOrdersButton();
                break;
            case R.id.printOrdersButton:
                handleDeleteAllButton();
                break;
            case R.id.printReturnsButton:
                handleDeleteReturnsButton();
                break;
            case R.id.deleteButton:
                handleDeleteButton();
                break;
            case R.id.backDeleteButton:
                handleBackDeleteButton();
                break;
            case R.id.scannerButton:
                handleScannerButton();
                break;
            case R.id.offlineButton:
                handleOfflineButton();
                break;
            case R.id.onlineButton:
                handleOnlineButton();
                break;
            case R.id.offlineExportButton:
                handleOfflineExportButton();
                break;
            case R.id.onlineExportButton:
                handleOnlineExportButton();
                break;
            case R.id.backExportDialog:
                handleExportBackButton();
                break;
            case R.id.importBackButton:
                handleImportBackButton();
                break;
            default:
                break;
        }

    }

    private void handleAmountInfoTextViewClick() {

        exportInfoDialog.show();
    }


    private void handleInfoBackButton() {
        exportInfoDialog.hide();
    }

    private void handleExportBackButton() {
        exportDialog.hide();
    }

    private void handleImportBackButton() {
        importDialog.hide();
    }

    private void handleOfflineExportButton() {
        if (!Model.getInstance().getOrders().isEmpty() || !Model.getInstance().getReturns().isEmpty()) {
            SaveOfflineFilesTask save = new SaveOfflineFilesTask(this, savingDialog);
            save.execute("");
            exportDialog.hide();
        } else {
            Toast.makeText(this, "Není co exportovat!", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleOnlineExportButton() {
        if (!Model.getInstance().getOrders().isEmpty() || !Model.getInstance().getReturns().isEmpty()) {
            Client export = new Client(Model.getInstance().getIp(), this, savingDialog);
            export.execute("export");
        } else {
            Toast.makeText(this, "Není co exportovat!", Toast.LENGTH_SHORT).show();
        }
        exportDialog.hide();
    }


    public void handleBackDeleteButton() {
        deleteDialog.hide();
    }


    public void handleDeleteAllButton() {
        if (!Model.getInstance().getReturns().isEmpty() || !Model.getInstance().getOrders().isEmpty()) {
            Model.getInstance().clearReturns();
            Model.getInstance().clearOrders();
            hasReturn = false;
            hasReturn = false;
            updateAmountInfo();
            deleteDialog.hide();
            Toast.makeText(this, "Všechna data byla vymazána.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Vratka i objednavka jsou prázdné.", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleDeleteReturnsButton() {
        if (!Model.getInstance().getReturns().isEmpty()) {
            Model.getInstance().clearReturns();
            hasReturn = false;
            updateAmountInfo();
            deleteDialog.hide();
            Toast.makeText(this, "Data ve vratce byla vymazána.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Vratka je prázdná.", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleDeleteOrdersButton() {
        if (!Model.getInstance().getOrders().isEmpty()) {
            Model.getInstance().clearOrders();
            hasOrder = false;
            updateAmountInfo();
            deleteDialog.hide();
            Toast.makeText(this, "Data v objednávce byla vymazána.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Objednávka je prázdná.", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleDeleteButton() {
        deleteDialog.show();
    }

    private void handleEditIpButton() {
        hideKeyboard(inputIpAddress);
        String ip = inputIpAddress.getText().toString().replace(" ", "");
        if (ip.length() != 0) {
            setNewIpAddress(ip);
        } else {
            Toast.makeText(this, "Vlož IP Adresu", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNewIpAddress(String ip) {
        if (validateIp(ip)) {
            Model.getInstance().setIp(ip);
            ipAddressTextView.setText("IP Adresa : " + Model.getInstance().getIp());
            ipDialog.dismiss();
            Toast.makeText(this, "IP Adresa : " + Model.getInstance().getIp() + " uložena", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Neplatná ip adresa", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validateIp(String ip) {
        Pattern PATTERN = Pattern.compile(
                "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        return PATTERN.matcher(ip).matches();
    }


    private void handleIpBackButton() {
        hideKeyboard(inputIpAddress);
        ipDialog.hide();
    }

    private void handleExportButton() {
        exportDialog.show();
    }

    private void handleImportDataButton() {
        importDialog.show();
    }

    private void handleOnlineButton() {
        Model.getInstance().clearAnalysis();
        clearAllTextViews();
        Client client = new Client(Model.getInstance().getIp(), this, loadingAnalysisDialog);
        client.execute("analyza");
        importDialog.hide();
    }

    private void handleOfflineButton() {
        count = 0;
        pathHistory = new ArrayList<String>();
        pathHistory.add(count, System.getenv("EXTERNAL_STORAGE"));
        checkInternalStorage();

        boolean fileFound = false;
        for (int index = 0; index < listFile.length; index++) {
            String name = listFile[index].getName();
            if ("ssbAnalyza".equalsIgnoreCase(name)) {
                String path = listFile[index].getAbsolutePath();
                LoadOfflineAnalysisAsyncTask loadAnalysis = new LoadOfflineAnalysisAsyncTask(getApplicationContext(), loadingAnalysisDialog, path);
                loadAnalysis.execute("");
                fileFound = true;
                break;
            }
        }
        if (fileFound == false) {
            Toast.makeText(this, "Soubor ssbAnalyza nebyl nalezen", Toast.LENGTH_LONG).show();
        }
        importDialog.hide();
    }


    private void checkInternalStorage() {
        try {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
            } else {
                file = new File(pathHistory.get(count));
            }
            listFile = file.listFiles();
            filePathStrings = new String[listFile.length];
            FileNameStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++) {
                filePathStrings[i] = listFile[i].getAbsolutePath();
                FileNameStrings[i] = listFile[i].getName();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void askForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // you already have a permission
                checkForDatabaseUpdate();
            } else {
                // asks for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else { //you dont need to worry about these stuff below api level 23

        }
    }


    int onlineDbVersionNumber;

    private void checkForDatabaseUpdate() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Api api = retrofit.create(Api.class);
        Call<DatabaseVersion> call = api.getDatabaseVersionInfo();
        call.enqueue(new Callback<DatabaseVersion>() {
            @Override
            public void onResponse(Call<DatabaseVersion> call, Response<DatabaseVersion> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Dotaz nebyl úspešný" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    DatabaseVersion version = response.body();
                    onlineDbVersionNumber = Integer.valueOf(version.getDatabaseVersion());
                    if (onlineDbVersionNumber > Model.getInstance().getSettings().getCurrentDatabaseVersion()) {
                        if (isWifiEnabled()) {
                            updateFoundDialog(MainActivity.this);
                        } else {
                            Toast.makeText(getApplicationContext(), "Je dostupná nová databáze, pro stažení se připojte k WIFI.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DatabaseVersion> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Nepřipojeno k internetu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateFoundDialog(Context context) {
        new AlertDialog.Builder(context).setTitle(String.format("Aktualizace databaze (v.%d)", onlineDbVersionNumber)).setMessage("Je dostupná aktualizace databáze.").setPositiveButton("Aktualizovat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createDownloadingDatabaseDialog();
                updateDatabase();
            }
        }).setNegativeButton("Neaktualizovat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setIcon(R.drawable.ic_file_download).show();
    }


    private void updateDatabase() {
        try {

            DatabaseDownloader databaseDownloader = new DatabaseDownloader(this, onlineDbVersionNumber, downloadingDialog, downloadingDatabaseProgressBar);
            databaseDownloader.download("http://www.skladovypomocnik.cz/BooksDatabase.db");

        } catch (Exception e) {
            Toast.makeText(this, "Aktulalizace se nezdařila.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public boolean isWifiEnabled() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    private void clearAllTextViews() {
        bookNameTextView.setText("");
        bookEanTextView.setText("");
        totalAmountTextView.setText("");
        soldAmountTextView.setText("");
        supplierTextView.setText("");
        authorTextView.setText("");
        locationTextView.setText("");
        rankTextView.setText("");
    }


    private void handleEditButton() {
        editDialog.show();
    }

    private void handleBackButton() {
        hideKeyboard(returnAmountInput);
        returnAmountInput.setText("");
        orderAmountInput.setText("");
        editDialog.hide();
    }

    boolean hasReturn = false, hasOrder = false;

    private void handleSaveButton() {
        hideKeyboard(returnAmountInput);
        if (Model.getInstance().getSelectedItem() != null) {
            if (returnAmountInput.length() > 0) {
                hasReturn = true;
                Model.getInstance().saveReturnItem(returnAmountInput.getText().toString());
            }
            if (orderAmountInput.length() > 0) {
                hasOrder = true;
                Model.getInstance().saveOrderItem(orderAmountInput.getText().toString());
            }
            updateAmountInfo();
        } else {
            Toast.makeText(this, "Nic není vybrané", Toast.LENGTH_SHORT).show();

        }


        returnAmountInput.setText("");
        orderAmountInput.setText("");
        editDialog.hide();
    }


    private void handleScannerButton() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    SCANNER_PERMISSION_REQUEST_CODE);
        } else {
            invokeScanner();
        }

    }

    private void invokeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(scannerActivity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SCANNER_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                invokeScanner();
            } else {
                Toast.makeText(this, "Nebyla dána opravnění ke skenru, aplikace nebude fungovat správně.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkForDatabaseUpdate();
            } else {
                Toast.makeText(this, "Nebyla dána opravnění, aplikace nebude fungovat správně.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Skenování  bylo zrušeno", Toast.LENGTH_SHORT).show();
            } else {
                String ean = result.getContents();
                Model.getInstance().scannerUpdate(ean, eanInput, bookNameTextView, bookEanTextView, totalAmountTextView, soldAmountTextView, supplierTextView, locationTextView, rankTextView, authorTextView);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void updateAmountInfo() {
        String returns = "", orders = "";
        if (Model.getInstance().getOrders().size() == 1) {
            orders = String.format("Objednávka obsahuje %s položku a %d ks.", Model.getInstance().getOrders().size(), Model.getInstance().calculateTotalAmount(Model.getInstance().getOrders()));
        } else if (Model.getInstance().getOrders().size() > 1 && Model.getInstance().getOrders().size() < 5) {
            orders = String.format("Objednávka obsahuje %s položky a %d ks.", Model.getInstance().getOrders().size(), Model.getInstance().calculateTotalAmount(Model.getInstance().getOrders()));
        } else if (Model.getInstance().getOrders().size() > 4) {
            orders = String.format("Objednávka obsahuje %s položek a %d ks.", Model.getInstance().getOrders().size(), Model.getInstance().calculateTotalAmount(Model.getInstance().getOrders()));
        }

        if (Model.getInstance().getReturns().size() == 1) {
            returns = String.format("Vratka obsahuje %s položku a %d ks.", Model.getInstance().getReturns().size(), Model.getInstance().calculateTotalAmount(Model.getInstance().getReturns()));
        } else if (Model.getInstance().getReturns().size() > 1 && Model.getInstance().getReturns().size() < 5) {
            returns = String.format("Vratka obsahuje %s položky a %d ks.", Model.getInstance().getReturns().size(), Model.getInstance().calculateTotalAmount(Model.getInstance().getReturns()));
        } else if (Model.getInstance().getReturns().size() > 4) {
            returns = String.format("Vratka obsahuje %s položek a %d ks.", Model.getInstance().getReturns().size(), Model.getInstance().calculateTotalAmount(Model.getInstance().getReturns()));
        }

        amountInfoTextView.setText(String.format("%s\n%s", returns, orders));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
