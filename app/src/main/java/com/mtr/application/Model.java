package com.mtr.application;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mtr.application.shared.ArticleRow;
import com.mtr.application.shared.ExportArticle;

import java.util.ArrayList;
import java.util.HashMap;

import application.R;

public class Model {

    private HashMap<String, ArticleRow> analysis;
    private ArrayList<ExportArticle> returns, orders;
    private Settings settings;
    private Context context;
    private ArticleRow selectedItem;
    private String name;


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


    ArrayList<String> suggestions = new ArrayList<>();

    public ArrayList<String> getSuggestions() {
        return suggestions;
    }


    public void createSettings(Context c) {
        this.settings = new Settings(c);
    }

    public Settings getSettings(){
        return this.settings;
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

    public void setName(String text) {
        this.name = text;
    }

    public String getName() {
        return name;
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void loadAnalysis() {
        analysis = settings.getAnalysis();
    }

    public void saveAnalysis() {
        settings.setAnalysis(analysis);
    }

    public void loadOrdersAndReturns() {
        orders = settings.getOrders();
        returns = settings.getReturns();
        System.out.println(orders.size() + " returns " + returns.size());
    }

    public void saveOrdersAndReturns() {
        settings.setOrders(orders);
        settings.setReturns(returns);
    }


    public void updateDisplay(EditText eanInput, TextView bookNameTextView, TextView bookEanTextView, TextView totalAmountTextView, TextView soldAmountTextView, TextView supplierTextView, TextView locationTextView, TextView rankTextView, TextView authorTextView) {
        String ean = eanInput.getText().toString();
        ArticleRow a = findEan(ean);
        bookNameTextView.setText("");
        bookEanTextView.setText("");
        totalAmountTextView.setText("");
        soldAmountTextView.setText("");
        supplierTextView.setText("");
        locationTextView.setText("");
        rankTextView.setText("");
        authorTextView.setText("");
        supplierTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
        if (a.getEan().equalsIgnoreCase("neznamy")) {// item does not exist
            selectedItem = null;
            Toast.makeText(context, "Položka není v systému", Toast.LENGTH_SHORT).show();
            bookNameTextView.setText("");
            bookEanTextView.setText("");
            totalAmountTextView.setText("");
            soldAmountTextView.setText("");
            supplierTextView.setText("");
            locationTextView.setText("");
            rankTextView.setText("");
            authorTextView.setText("");
        } else if (a.getSales().isEmpty()) {// all items in analysis have some record of sale. This block handles situation when item is not in analysis.
            bookNameTextView.setText("Název: " + a.getName());
            bookEanTextView.setText(String.format("EAN: %s", a.getEan()));
            supplierTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            supplierTextView.setText("\n\n               Položka není v analýze.");
            locationTextView.setText("");
            rankTextView.setText(String.format("Cena: %s ,- Kč", a.getPrice()));
            authorTextView.setText("");
        } else {
            bookNameTextView.setText(String.format("Název: %s", a.getName()));
            bookEanTextView.setText(String.format("EAN: %s  %s (%s)", a.getEan(), a.getSupplier(), pickConsignmentOrSale(a)));
            totalAmountTextView.setText(String.format("Stav skladu: %s (%s)  Příjem: %s ", a.getStoredAmount(), cleanSupplyString(a.getSupply()), a.getDateOfLastDelivery()));
            soldAmountTextView.setText(String.format("Prodeje: %s/%s  Poslední prodej: %s", a.getSales(), a.getSales2(), a.getDateOfLastSale()));
            supplierTextView.setText(String.format("Cena: %s,- Kč   Obrat: %s,- Kč", a.getPrice(), a.getRevenue()));
            locationTextView.setText(String.format("LOC: %s", a.getLocations()));
            rankTextView.setText(String.format("Pořadí prodeje: %s   Pořadí eshop: %s", a.getRank(), a.getEshopRank()));
            authorTextView.setText(String.format("Autor: %s (%s)", a.getAuthor(), a.getRealeaseDate()));
        }

    }

    public void scannerUpdate(String ean, EditText eanInput, TextView bookNameTextView, TextView bookEanTextView, TextView totalAmountTextView, TextView soldAmountTextView, TextView supplierTextView, TextView locationTextView, TextView rankTextView, TextView authorTextView) {
        ArticleRow a = findEan(ean);
        bookNameTextView.setText("");
        bookEanTextView.setText("");
        totalAmountTextView.setText("");
        soldAmountTextView.setText("");
        supplierTextView.setText("");
        locationTextView.setText("");
        rankTextView.setText("");
        authorTextView.setText("");
        supplierTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
        if (a.getEan().equalsIgnoreCase("neznamy")) {// item does not exist
            selectedItem = null;
            Toast.makeText(context, "Položka není v systému", Toast.LENGTH_SHORT).show();
            bookNameTextView.setText("");
            bookEanTextView.setText("");
            totalAmountTextView.setText("");
            soldAmountTextView.setText("");
            supplierTextView.setText("");
            locationTextView.setText("");
            rankTextView.setText("");
            authorTextView.setText("");
        } else if (a.getSales().isEmpty()) {// all items in analysis have some record of sale. This block handles situation when item is not in analysis.
            bookNameTextView.setText("Název: " + a.getName());
            bookEanTextView.setText(String.format("EAN: %s", a.getEan()));
            supplierTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            supplierTextView.setText("\n\n               Položka není v analýze.");
            locationTextView.setText("");
            rankTextView.setText(String.format("Cena: %s ,- Kč", a.getPrice()));
            authorTextView.setText("");
        } else {
            bookNameTextView.setText(String.format("Název: %s", a.getName()));
            bookEanTextView.setText(String.format("EAN: %s  %s (%s)", a.getEan(), a.getSupplier(), pickConsignmentOrSale(a)));
            totalAmountTextView.setText(String.format("Stav skladu: %s (%s)  Příjem: %s ", a.getStoredAmount(), cleanSupplyString(a.getSupply()), a.getDateOfLastDelivery()));
            soldAmountTextView.setText(String.format("Prodeje: %s/%s  Poslední prodej: %s", a.getSales(), a.getSales2(), a.getDateOfLastSale()));
            supplierTextView.setText(String.format("Cena: %s,- Kč   Obrat: %s,- Kč", a.getPrice(), a.getRevenue()));
            locationTextView.setText(String.format("LOC: %s", a.getLocations()));
            rankTextView.setText(String.format("Pořadí prodeje: %s   Pořadí eshop: %s", a.getRank(), a.getEshopRank()));
            authorTextView.setText(String.format("Autor: %s (%s)", a.getAuthor(), a.getRealeaseDate()));
        }

    }


    private String cleanSupplyString(String s) {
        if (s == null || s.isEmpty()) {
            s = "0";
        }
        if (s.contains(",")) {
            s = s.substring(0, s.indexOf(","));
        }
        return s;
    }


    private String pickConsignmentOrSale(ArticleRow a) {
        String consignmentOrSale = "";


        if (a.getDeliveredAs().equalsIgnoreCase("1512") || a.getDeliveredAs().equalsIgnoreCase("komise")) {
            consignmentOrSale = "Pevno";
        } else if (a.getDeliveredAs().equalsIgnoreCase("1514") || a.getDeliveredAs().equalsIgnoreCase("komise")) {
            consignmentOrSale = "Komise";
        }
        return consignmentOrSale;
    }


    public void saveReturnItem(String returnAmount) {
        if (returns.isEmpty()) {
            ExportArticle exportArticle = new ExportArticle();
            exportArticle.setRank(selectedItem.getRank());
            exportArticle.setFirstCode(selectedItem.getFirstCode());
            exportArticle.setEan(selectedItem.getEan());
            exportArticle.setName(selectedItem.getName());
            exportArticle.setSales(selectedItem.getSales());
            exportArticle.setSales2(selectedItem.getSales2());
            exportArticle.setRevenue(selectedItem.getRevenue());
            exportArticle.setStoredAmount(selectedItem.getStoredAmount());
            exportArticle.setSupply(selectedItem.getSupply());
            exportArticle.setLocations(selectedItem.getLocations());
            exportArticle.setPrice(selectedItem.getPrice());
            exportArticle.setDph(selectedItem.getDph());
            exportArticle.setSupplier(selectedItem.getSupplier());
            exportArticle.setAuthor(selectedItem.getAuthor());
            exportArticle.setDateOfLastSale(selectedItem.getDateOfLastSale());
            exportArticle.setDateOfLastDelivery(selectedItem.getDateOfLastDelivery());
            exportArticle.setRealeaseDate(selectedItem.getRealeaseDate());
            exportArticle.setDeliveredAs(selectedItem.getDeliveredAs());
            exportArticle.setEshopRank(selectedItem.getEshopRank());
            exportArticle.setExportAmount(returnAmount);
            returns.add(exportArticle);
        } else {
            iterateListForMatch(selectedItem.getEan(), returnAmount, true);
        }
    }


    public ArticleRow getSelectedItem() {
        return this.selectedItem;
    }

    public void saveOrderItem(String orderAmount) {
        if (orders.isEmpty()) {
            ExportArticle exportArticle = new ExportArticle();
            exportArticle.setRank(selectedItem.getRank());
            exportArticle.setFirstCode(selectedItem.getFirstCode());
            exportArticle.setEan(selectedItem.getEan());
            exportArticle.setName(selectedItem.getName());
            exportArticle.setSales(selectedItem.getSales());
            exportArticle.setSales2(selectedItem.getSales2());
            exportArticle.setRevenue(selectedItem.getRevenue());
            exportArticle.setStoredAmount(selectedItem.getStoredAmount());
            exportArticle.setSupply(selectedItem.getSupply());
            exportArticle.setLocations(selectedItem.getLocations());
            exportArticle.setPrice(selectedItem.getPrice());
            exportArticle.setDph(selectedItem.getDph());
            exportArticle.setSupplier(selectedItem.getSupplier());
            exportArticle.setAuthor(selectedItem.getAuthor());
            exportArticle.setDateOfLastSale(selectedItem.getDateOfLastSale());
            exportArticle.setDateOfLastDelivery(selectedItem.getDateOfLastDelivery());
            exportArticle.setRealeaseDate(selectedItem.getRealeaseDate());
            exportArticle.setDeliveredAs(selectedItem.getDeliveredAs());
            exportArticle.setEshopRank(selectedItem.getEshopRank());
            exportArticle.setExportAmount(orderAmount);
            orders.add(exportArticle);
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
                ExportArticle exportArticle = new ExportArticle();
                exportArticle.setRank(selectedItem.getRank());
                exportArticle.setFirstCode(selectedItem.getFirstCode());
                exportArticle.setEan(selectedItem.getEan());
                exportArticle.setName(selectedItem.getName());
                exportArticle.setSales(selectedItem.getSales());
                exportArticle.setSales2(selectedItem.getSales2());
                exportArticle.setRevenue(selectedItem.getRevenue());
                exportArticle.setStoredAmount(selectedItem.getStoredAmount());
                exportArticle.setSupply(selectedItem.getSupply());
                exportArticle.setLocations(selectedItem.getLocations());
                exportArticle.setPrice(selectedItem.getPrice());
                exportArticle.setDph(selectedItem.getDph());
                exportArticle.setSupplier(selectedItem.getSupplier());
                exportArticle.setAuthor(selectedItem.getAuthor());
                exportArticle.setDateOfLastSale(selectedItem.getDateOfLastSale());
                exportArticle.setDateOfLastDelivery(selectedItem.getDateOfLastDelivery());
                exportArticle.setRealeaseDate(selectedItem.getRealeaseDate());
                exportArticle.setDeliveredAs(selectedItem.getDeliveredAs());
                exportArticle.setEshopRank(selectedItem.getEshopRank());
                exportArticle.setExportAmount(exportAmount);
                returns.add(0, exportArticle);
            } else {
                ExportArticle exportArticle = new ExportArticle();
                exportArticle.setRank(selectedItem.getRank());
                exportArticle.setFirstCode(selectedItem.getFirstCode());
                exportArticle.setEan(selectedItem.getEan());
                exportArticle.setName(selectedItem.getName());
                exportArticle.setSales(selectedItem.getSales());
                exportArticle.setSales2(selectedItem.getSales2());
                exportArticle.setRevenue(selectedItem.getRevenue());
                exportArticle.setStoredAmount(selectedItem.getStoredAmount());
                exportArticle.setSupply(selectedItem.getSupply());
                exportArticle.setLocations(selectedItem.getLocations());
                exportArticle.setPrice(selectedItem.getPrice());
                exportArticle.setDph(selectedItem.getDph());
                exportArticle.setSupplier(selectedItem.getSupplier());
                exportArticle.setAuthor(selectedItem.getAuthor());
                exportArticle.setDateOfLastSale(selectedItem.getDateOfLastSale());
                exportArticle.setDateOfLastDelivery(selectedItem.getDateOfLastDelivery());
                exportArticle.setRealeaseDate(selectedItem.getRealeaseDate());
                exportArticle.setDeliveredAs(selectedItem.getDeliveredAs());
                exportArticle.setEshopRank(selectedItem.getEshopRank());
                exportArticle.setExportAmount(exportAmount);
                orders.add(0, exportArticle);
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
            ArticleRow article = new ArticleRow();
            article.setEan("neznamy");
            item = article;
        }
        selectedItem = item;
        return item;
    }


    private void lookForInDatabase(String ean) {
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();
        Item selectedItem = db.getBook(ean);

        if (!"notFound".equalsIgnoreCase(selectedItem.getName())) {
            ArticleRow article = new ArticleRow();
            article.setName(selectedItem.getName());
            article.setEan(selectedItem.getEan());
            article.setPrice(selectedItem.getPrice());
            item = article;
        }
        db.close();
    }


    public void addBook() {
    }


}
