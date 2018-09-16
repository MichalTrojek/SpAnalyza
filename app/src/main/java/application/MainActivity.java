package application;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText eanInput;
    private Button editButton, deleteButton;
    private AlertDialog editDialog;
    private TextView bookNameTextView, bookEanTextView, totalAmountTextView, soldAmountTextView, supplierTextView, ipAddressTextView, amountInfoTextView;
    private EditText inputIpAddress;
    private AlertDialog ipDialog, loadingDialog, deleteDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application.Model.getInstance().createSettings(this);
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);


        application.Model.getInstance().setContext(this);
        createLoadingDialog();
        createIpAddressAlertDialog();
        createDeleteDialog();
        setToolbar();
        setButtons();
        setTextViews();
        setEanInputListenerToHandleEnterKey();
        setAds();
        createEditDialog();
        hideKeyboard(eanInput);
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

    private void createDeleteDialog() {
        View view = getLayoutInflater().inflate(R.layout.delete_dialog, null);
        view.findViewById(R.id.deleteAllButton).setOnClickListener(MainActivity.this);
        view.findViewById(R.id.deleteOrdersButton).setOnClickListener(MainActivity.this);
        view.findViewById(R.id.deleteReturnsButton).setOnClickListener(MainActivity.this);
        view.findViewById(R.id.backDeleteButton).setOnClickListener(MainActivity.this);
        deleteDialog = new AlertDialog.Builder(this).setView(view).create();
    }


    private void createLoadingDialog() {
        View view = getLayoutInflater().inflate(R.layout.loading_analysis_dialog, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        TextView loadingTextView = (TextView) view.findViewById(R.id.loadingTextView);
        progressBar.setVisibility(View.VISIBLE);
        loadingDialog = new AlertDialog.Builder(this).setView(view).create();
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
            default:
                return super.onOptionsItemSelected(item);

        }
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
            Model.getInstance().updateDisplay(eanInput, bookNameTextView, bookEanTextView, totalAmountTextView, soldAmountTextView, supplierTextView);
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
            case R.id.deleteOrdersButton:
                handleDeleteOrdersButton();
                break;
            case R.id.deleteAllButton:
                handleDeleteAllButton();
                break;
            case R.id.deleteReturnsButton:
                handleDeleteReturnsButton();
                break;
            case R.id.deleteButton:
                handleDeleteButton();
                break;
            case R.id.backDeleteButton:
                handleBackDeleteButton();
                break;
            default:
                break;
        }

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
        if (!Model.getInstance().getOrders().isEmpty() || !Model.getInstance().getReturns().isEmpty()) {
            Client export = new Client(Model.getInstance().getIp(), this, loadingDialog);
            export.execute("export");
        } else {
            Toast.makeText(this, "Není co exportovat!", Toast.LENGTH_SHORT).show();
        }

    }

    private void handleImportDataButton() {
        Model.getInstance().clearAnalysis();
        clearAllTextViews();
        Client client = new Client(Model.getInstance().getIp(), this, loadingDialog);
        client.execute("analyza");
    }

    private void clearAllTextViews() {
        bookNameTextView.setText("");
        bookEanTextView.setText("");
        totalAmountTextView.setText("");
        soldAmountTextView.setText("");
        supplierTextView.setText("");
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


}
