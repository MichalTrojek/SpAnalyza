package com.mtr.skladovypomocnikanalyza;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private String data;
    private List<ArticleRow> articles = new ArrayList<>();
    private List<ArticleRow> articlesDatabase = new ArrayList<>();
    private Settings settings;
    private Context context;
    private ArticleRow selectedItem;
//    new ArticleRow("Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze");

    private ArrayList<ExportArticle> returns = new ArrayList<>();
    private ArrayList<ExportArticle> orders = new ArrayList<>();


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


    public void setData(String data) {
        this.data = data;
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void deleteData() {
        orders.clear();
        returns.clear();
    }

    //articles.add(new ArticleRow(temp[1], temp[0], "", "", "", "", "", "", ""));


    public void convertStringToList() {
        String[] temp = data.split("~");
        for (String s : temp) {
            String[] line = s.split(";");
            if (line.length == 9) {
                articles.add(new ArticleRow(line[0], line[1], line[2], line[3], line[4], line[5],
                        line[6], line[7],
                        line[8]));
            } else {
                articles.add(new ArticleRow(line[0], line[1], line[2], line[3], line[4], line[5],
                        line[6], line[7],
                        "empty"));
            }
        }
    }


    public void updateDisplay(EditText eanInput, TextView bookNameTextView, TextView bookEanTextView, TextView totalAmountTextView, TextView soldAmountTextView, TextView supplierTextView, TextView dateOfLastSaleTextView, TextView dateofLastDeliveryTextView, TextView receivedAsTextView) {
        String ean = eanInput.getText().toString();
        ArticleRow a = Model.getInstance().findEan(ean);
        if (a.getEan().equalsIgnoreCase("není v analýze")) {
            Toast.makeText(context, "Není v analýze", Toast.LENGTH_SHORT).show();
        } else {
            bookNameTextView.setText("Název: " + a.getName());
            bookEanTextView.setText("EAN: " + a.getEan());
            totalAmountTextView.setText("Stav skladu: " + a.getTotalAmount());
            soldAmountTextView.setText("Prodej za období: " + a.getSoldAmount());
            supplierTextView.setText("Dodavatel: " + a.getSupplier());
            dateOfLastSaleTextView.setText("Datum posledního prodeje: " + a.getDateOfLastSale());
            dateofLastDeliveryTextView.setText("Datum posledního příjmu: " + a.getDateOfLastDelivery());
            receivedAsTextView.setText("Přijmuto jako: " + pickConsignmentOrSale(a));
        }
    }

    private String pickConsignmentOrSale(ArticleRow a) {
        String consignmentOrSale = "Není v analýze";
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
//        if (selectedItem != null) {
        if (orders.isEmpty()) {
            orders.add(new ExportArticle(selectedItem.getEan(), selectedItem.getName(), selectedItem.getSoldAmount(), selectedItem.getTotalAmount(), selectedItem.getPrice(), selectedItem.getSupplier(), selectedItem.getDateOfLastSale(), selectedItem.getDateOfLastDelivery(), selectedItem.getOrderOfLastDelivery(), orderAmount));
        } else {
            iterateListForMatch(selectedItem.getEan(), orderAmount, false);
        }
//        } else {
//            Toast.makeText(context, "Není v analýze", Toast.LENGTH_SHORT).show();
//        }

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
        for (ArticleRow a : articles) {
            if (ean.equalsIgnoreCase(a.getEan()) && !ean.equalsIgnoreCase("Není v analýze")) {
                selectedItem = a;
                return a;
            }
        }
        return new ArticleRow("Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze");
    }

//    private ArticleRow findEan(String ean) {
//        int i = 0;
//        for (ArticleRow a : articlesDatabase) {
//            if (i == 1) {
//                break;
//            }
//            i++;
//            System.out.println("name " + a.getName() + " ean " + a.getEan());
//            if (ean.equalsIgnoreCase(a.getEan()) && !ean.equalsIgnoreCase("Není v analýze")) {
//                selectedItem = a;
//                return a;
//            }
//        }
//        return new ArticleRow("Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze", "Není v analýze");
//    }


    public String createStringFromLists() {
        StringBuilder sb = new StringBuilder();

        for (ExportArticle e : returns) {
            sb.append(e.getEan()).append(";").append(e.getName()).append(";").append(e.getSoldAmount()).append(";").append(e.getTotalAmount()).append(";").append(e.getPrice()).append(";").append(e.getSupplier()).append(";").append(e.getDateOfLastSale()).append(";").append(e.getDateOfLastDelivery()).append(";").append(e.getOrderOfLastDelivery()).append(";").append(e.getExportAmount()).append(";").append("~");
        }
        sb.append("~o~");

        for (ExportArticle e : orders) {
            sb.append(e.getEan()).append(";").append(e.getName()).append(";").append(e.getSoldAmount()).append(";").append(e.getTotalAmount()).append(";").append(e.getPrice()).append(";").append(e.getSupplier()).append(";").append(e.getDateOfLastSale()).append(";").append(e.getDateOfLastDelivery()).append(";").append(e.getOrderOfLastDelivery()).append(";").append(e.getExportAmount()).append(";").append("~");

        }
        return sb.toString();
    }

}
