package application;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import application.shared.ArticleRow;
import application.shared.ExportArticle;

public class Model {


    private HashMap<String, ArticleRow> analysis;
    private ArrayList<ExportArticle> returns, orders;
    private Settings settings;
    private Context context;
    private ArticleRow selectedItem;


    public Model() {
        this.analysis = new HashMap<>();
        this.returns = new ArrayList<>();
        this.orders = new ArrayList<>();
    }


    private static class LazyHolder {
        static final Model INSTANCE = new Model();
    }

    public static Model getInstance() {
        return LazyHolder.INSTANCE;
    }


    public void createSettings(Context c) {
        this.settings = new Settings(c);
    }

    public String getIp() {
        return this.settings.getIp();
    }

    public void setIp(String ip) {
        settings.setIP(ip);
    }


    public void setAnalysis(HashMap<String, ArticleRow> analysis) {
        this.analysis = analysis;
    }

    public HashMap<String, ArticleRow> getAnalysis() {
        return this.analysis;
    }

    public ArrayList<ExportArticle> getOrders() {
        return this.orders;
    }

    public ArrayList<ExportArticle> getReturns() {
        return this.returns;
    }


    public void setContext(Context context) {
        this.context = context;
    }

//    private TextView bookNameTextView, bookEanTextView, totalAmountTextView, soldAmountTextView, supplierAmountTextView;


    public void updateDisplay(EditText eanInput, TextView bookNameTextView, TextView bookEanTextView, TextView totalAmountTextView, TextView soldAmountTextView, TextView supplierTextView) {
        String ean = eanInput.getText().toString();
        ArticleRow a = findEan(ean);
        if (a.getEan().equalsIgnoreCase("neznamy")) {
            selectedItem = null;
            Toast.makeText(context, "Položka není v systému", Toast.LENGTH_SHORT).show();
            bookNameTextView.setText("");
            bookEanTextView.setText("");
            totalAmountTextView.setText("");
            soldAmountTextView.setText("");
            supplierTextView.setText("");
        } else if (a.getSoldAmount().isEmpty()) {
            bookNameTextView.setText("Název: " + a.getName());
            bookEanTextView.setText(String.format("EAN: %s", a.getEan()));
            supplierTextView.setText("\n\n               Položka není v analýze.");
        } else {
            bookNameTextView.setText("Název: " + a.getName());
            bookEanTextView.setText(String.format("EAN: %s,   Cena: %s,- Kč", a.getEan(), a.getPrice()));
            totalAmountTextView.setText(String.format("Stav skladu: %s   Příjem: %s ", a.getTotalAmount(), a.getDateOfLastDelivery()));
            soldAmountTextView.setText(String.format("Prodeje: %s   Poslední prodej: %s", a.getSoldAmount(), a.getDateOfLastSale()));
            supplierTextView.setText(String.format("Dodavatel: %s (%s)", a.getSupplier(), pickConsignmentOrSale(a)));
        }
//        bookEanTextView.clearFocus();
//        eanInput.requestFocus();
    }

    private String pickConsignmentOrSale(ArticleRow a) {
        String consignmentOrSale = "";
        if (a.getOrderOfLastDelivery().equalsIgnoreCase("1512")) {
            consignmentOrSale = "Pevno";
        } else if (a.getOrderOfLastDelivery().equalsIgnoreCase("1514")) {
            consignmentOrSale = "Komise";
        }
        return consignmentOrSale;
    }


    //ean, name, soldAmount, totalAmount, price, supplier, dateOfLastSale, dateOfLastDelivery, orderOfLastDelivery
    public void saveReturnItem(String returnAmount) {
        if (returns.isEmpty()) {
            returns.add(new ExportArticle(selectedItem.getEan(), selectedItem.getName(), selectedItem.getSoldAmount(), selectedItem.getTotalAmount(), selectedItem.getPrice(), selectedItem.getSupplier(), selectedItem.getDateOfLastSale(), selectedItem.getDateOfLastDelivery(), selectedItem.getOrderOfLastDelivery(), returnAmount));
        } else {
            iterateListForMatch(selectedItem.getEan(), returnAmount, true);
        }
    }


    public ArticleRow getSelectedItem() {
        return this.selectedItem;
    }

    public void saveOrderItem(String orderAmount) {
        if (orders.isEmpty()) {
            orders.add(new ExportArticle(selectedItem.getEan(), selectedItem.getName(), selectedItem.getSoldAmount(), selectedItem.getTotalAmount(), selectedItem.getPrice(), selectedItem.getSupplier(), selectedItem.getDateOfLastSale(), selectedItem.getDateOfLastDelivery(), selectedItem.getOrderOfLastDelivery(), orderAmount));
        } else {
            iterateListForMatch(selectedItem.getEan(), orderAmount, false);
        }
    }

    public void clearAnalysis() {
        if (analysis != null) {
            this.analysis.clear();
        }
        selectedItem = null;
    }


    public void clearOrders() {
        if (orders != null) {
            this.orders.clear();
        }
    }

    public void clearReturns() {
        if (returns != null) {
            this.returns.clear();
        }
    }


    private void iterateListForMatch(String ean, String exportAmount, boolean isReturn) {
        searchListForItemByEan(ean, exportAmount, isReturn);
        addNewItemIfNotFound(ean, exportAmount, isReturn);
    }

    private boolean containsEan = false;

    private void searchListForItemByEan(String ean, String exportAmount, boolean isReturn) {
        if (isReturn) {
            for (int i = 0; i < returns.size(); i++) {
                ExportArticle a = returns.get(i);
                containsEan = false;
                if (ean.equalsIgnoreCase(a.getEan())) {
                    containsEan = true;
                    a.setExportAmount(exportAmount);
                    break;
                }
            }
        } else {
            for (int i = 0; i < orders.size(); i++) {
                ExportArticle a = orders.get(i);
                containsEan = false;
                if (ean.equalsIgnoreCase(a.getEan())) {
                    containsEan = true;
                    a.setExportAmount(exportAmount);
                    break;
                }
            }
        }

    }

    public int calculateTotalAmount(ArrayList<ExportArticle> items) {
        int amount = 0;
        for (ExportArticle a : items) {
            amount += Integer.parseInt(a.getExportAmount());
        }
        return amount;
    }


    private void addNewItemIfNotFound(String ean, String exportAmount, boolean isReturn) {
        if (!containsEan) {
            if (isReturn) {
                returns.add(0, new ExportArticle(selectedItem.getEan(), selectedItem.getName(), selectedItem.getSoldAmount(), selectedItem.getTotalAmount(), selectedItem.getPrice(), selectedItem.getSupplier(), selectedItem.getDateOfLastSale(), selectedItem.getDateOfLastDelivery(), selectedItem.getOrderOfLastDelivery(), exportAmount));
            } else {
                orders.add(0, new ExportArticle(selectedItem.getEan(), selectedItem.getName(), selectedItem.getSoldAmount(), selectedItem.getTotalAmount(), selectedItem.getPrice(), selectedItem.getSupplier(), selectedItem.getDateOfLastSale(), selectedItem.getDateOfLastDelivery(), selectedItem.getOrderOfLastDelivery(), exportAmount));
            }
        }
    }

    private ArticleRow item;

    private ArticleRow findEan(String ean) {
        item = analysis.get(ean);
        if (item == null) {
            lookForInDatabase(ean);
        }
        if (item == null) {
            item = new ArticleRow("neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy");
        }
        selectedItem = item;
        return item;
    }


    private void lookForInDatabase(String ean) {
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();
        String name = db.getBook(ean);
        if (!"notFound".equalsIgnoreCase(name)) {
            item = new ArticleRow(ean, name, "", "", "", "", "", "", "");
        }
        db.close();
    }


}
