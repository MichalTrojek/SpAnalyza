package application.shared;

import java.io.Serializable;

public class ArticleRow implements Serializable {


	private static final long serialVersionUID = -2160864851277312099L;

	private String ean, name, totalAmount, soldAmount, price, supplier, dateOfLastSale, dateOfLastDelivery,
			orderOfLastDelivery;

	public ArticleRow(String ean, String name, String soldAmount, String totalAmount, String price, String supplier,
			String dateOfLastSale, String dateOfLastDelivery, String orderOfLastDelivery) {
		this.ean = ean;
		this.name = name;
		this.soldAmount = soldAmount;
		this.totalAmount = totalAmount;
		this.price = price;
		this.supplier = supplier;
		this.dateOfLastSale = dateOfLastSale;
		this.dateOfLastDelivery = dateOfLastDelivery;
		this.orderOfLastDelivery = orderOfLastDelivery;
	}

	public String getEan() {
		return this.ean;
	}

	public String getName() {
		return this.name;
	}

	public String getSoldAmount() {
		return this.soldAmount;
	}

	public String getTotalAmount() {
		return this.totalAmount;
	}

	public String getPrice() {
		return this.price;
	}

	public String getSupplier() {
		return this.supplier;
	}

	public String getDateOfLastSale() {
		return this.dateOfLastSale;
	}

	public String getOrderOfLastDelivery() {
		return this.orderOfLastDelivery;
	}

	public String getDateOfLastDelivery() {
		return this.dateOfLastDelivery;
	}

}
