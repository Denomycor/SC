package server.blockchain;

import java.io.Serializable;

public class Transaction implements Serializable {

	private static final long serialVersionUID = -220512868026077576L;

	private String fromId;
	private String destId;
	private double amount;

	public Transaction(String fromId, String destId, double amount) {
		this.fromId = fromId;
		this.destId = destId;
		this.amount = amount;
	}
	
	public String getFromId() {
		return fromId;
	}

	public String getDestId() {
		return destId;
	}

	public double getAmount() {
		return amount;
	}
}
