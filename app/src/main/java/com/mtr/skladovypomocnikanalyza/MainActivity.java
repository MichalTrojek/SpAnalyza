package com.mtr.skladovypomocnikanalyza;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText eanInput;
    private Button editButton, importDataButton, exportButton, deleteButton;
    private AlertDialog editDialog;
    private TextView bookNameTextView, bookEanTextView, totalAmountTextView, soldAmountTextView, supplierTextView, dateOfLastSaleTextView, dateofLastDeliveryTextView, receivedAsTextView, ipAddressTextView;
    private EditText inputIpAddress;
    private AlertDialog ipDialog;

    private List<ArticleRow> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Model.getInstance().createSettings(this);
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);


        Model.getInstance().setContext(this);
        createIpAddressAlertDialog();
        setToolbar();
        setButtons();
        setTextViews();
        setEanInputListenerToHandleEnterKey();
        setAds();
        createEditDialog();
        hideKeyboard(eanInput);
    }

    //    @Override
//    public void onStop() {
//        super.onStop();
//        Model.getInstance().saveData();
//        Model.getInstance().saveBooleans(hasReturn, hasOrder);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        hideKeyboard(eanInput);
//
//    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putString("articles", convertArrayListToString());
//        super.onSaveInstanceState(outState);
//    }

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
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void handleSettingsButton() {
        ipDialog.show();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
    }

    private void setButtons() {
        importDataButton = (Button) findViewById(R.id.importDataButton);
        importDataButton.setOnClickListener(this);
        exportButton = (Button) findViewById(R.id.exportButton);
        exportButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
    }

    private void setTextViews() {
        bookNameTextView = (TextView) findViewById(R.id.bookNameTextView);
        bookEanTextView = (TextView) findViewById(R.id.bookEanTextView);
        totalAmountTextView = (TextView) findViewById(R.id.totalAmountTextView);
        supplierTextView = (TextView) findViewById(R.id.supplierTextView);
        dateOfLastSaleTextView = (TextView) findViewById(R.id.dateOfLastSaleTextView);
        dateofLastDeliveryTextView = (TextView) findViewById(R.id.dateofLastDeliveryTextView);
        receivedAsTextView = (TextView) findViewById(R.id.receivedAsTextView);
        soldAmountTextView = (TextView) findViewById(R.id.soldAmountTextView);
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
            Model.getInstance().updateDisplay(eanInput, bookNameTextView, bookEanTextView, totalAmountTextView, soldAmountTextView, supplierTextView, dateOfLastSaleTextView, dateofLastDeliveryTextView, receivedAsTextView);
            hideKeyboard(eanInput);
            eanInput.setText("");
            return true;
        }
        return false;
    }


    private void setAds() {
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
            case R.id.importDataButton:
                handleImportDataButton();
                break;
            case R.id.exportButton:
                handleExportButton();
                break;
            case R.id.backButtonIp:
                handleIpBackButton();
                break;
            case R.id.editIpButton:
                handleEditIpButton();
                break;
            case R.id.deleteButton:
                handleDeleteButton();
                break;
            default:
                break;
        }

    }


    private void handleDeleteButton() {
        Model.getInstance().deleteData();
        hasReturn = false;
        hasOrder = false;
        Toast.makeText(this, "Data byla vymazána", Toast.LENGTH_SHORT).show();
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
        DataSender sender = new DataSender(Model.getInstance().getIp(), this);
        String data = Model.getInstance().createStringFromLists();
        StringBuilder sb = new StringBuilder();
        if (hasOrder) {
            sb.append("obj");
        }

        if (hasReturn) {
            sb.append("vr");
        }

        sender.execute(sb.toString() + data);
    }

    private void handleImportDataButton() {
        DataReceiver dataReceiver = new DataReceiver(Model.getInstance().getIp(), this);
        dataReceiver.execute("import");
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
        } else {
            Toast.makeText(this, "Nic není vybrané", Toast.LENGTH_SHORT).show();

        }
        returnAmountInput.setText("");
        orderAmountInput.setText("");
        editDialog.hide();
    }


}
