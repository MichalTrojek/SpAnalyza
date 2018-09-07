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


    private HashMap<String, ArticleRow> analysis = new HashMap<>();
    private HashMap<String, ArticleRow> data = new HashMap<>();
    private Settings settings;
    private Context context;
    private ArticleRow selectedItem;


    private ArrayList<ExportArticle> returns = new ArrayList<>();
    private ArrayList<ExportArticle> orders = new ArrayList<>();

    public Model() {
        analysis = new HashMap<>();
        data = new HashMap<>();
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


    public void setData(HashMap<String, ArticleRow> data) {
        this.data = data;
    }

    public HashMap<String, ArticleRow> getData() {
        return this.data;
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

    public void deleteData() {
        orders.clear();
        returns.clear();
    }


    public void updateDisplay(EditText eanInput, TextView bookNameTextView, TextView bookEanTextView, TextView totalAmountTextView, TextView soldAmountTextView, TextView supplierTextView, TextView dateOfLastSaleTextView, TextView dateofLastDeliveryTextView, TextView receivedAsTextView) {
        String ean = eanInput.getText().toString();
        ArticleRow a = findEan(ean);
        if (a.getEan().equalsIgnoreCase("neznamy")) {
            Toast.makeText(context, "Položka není v systému", Toast.LENGTH_SHORT).show();
        } else {
            bookNameTextView.setText("Název: " + a.getName());
            bookEanTextView.setText("EAN: " + a.getEan());
            totalAmountTextView.setText(String.format("Stav skladu: %s   Příjem: %s", a.getTotalAmount(), a.getDateOfLastDelivery()));
            soldAmountTextView.setText(String.format("Prodeje: %s   Poslední prodej: %s", a.getSoldAmount(), a.getDateOfLastSale()));
            supplierTextView.setText(String.format("Dodavatel: %s (%s)", a.getSupplier(), pickConsignmentOrSale(a)));
        }
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

    public void reset() {
        if (data != null) {
            
            this.data.clear();
        }

        if (analysis != null) {
            this.analysis.clear();
        }
        selectedItem = new ArticleRow("neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy");
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

    private void addNewItemIfNotFound(String ean, String exportAmount, boolean isReturn) {
        if (!containsEan) {
            if (isReturn) {
                returns.add(0, new ExportArticle(selectedItem.getEan(), selectedItem.getName(), selectedItem.getSoldAmount(), selectedItem.getTotalAmount(), selectedItem.getPrice(), selectedItem.getSupplier(), selectedItem.getDateOfLastSale(), selectedItem.getDateOfLastDelivery(), selectedItem.getOrderOfLastDelivery(), exportAmount));
            } else {
                orders.add(0, new ExportArticle(selectedItem.getEan(), selectedItem.getName(), selectedItem.getSoldAmount(), selectedItem.getTotalAmount(), selectedItem.getPrice(), selectedItem.getSupplier(), selectedItem.getDateOfLastSale(), selectedItem.getDateOfLastDelivery(), selectedItem.getOrderOfLastDelivery(), exportAmount));
            }
        }
    }

    private ArticleRow findEan(String ean) {
        ArticleRow item;
        item = analysis.get(ean);
        if (item == null) {
            item = data.get(ean);
        }

        if (item == null) {
            item = new ArticleRow("neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy", "neznamy");
        }
        selectedItem = item;
        return item;
    }


}
