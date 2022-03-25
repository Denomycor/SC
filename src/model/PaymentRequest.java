package model;

public class PaymentRequest {
	
	private final String id;
	private final User requested;
	private final double amount;
	private boolean paid;
	private boolean qrcode;
	
	public PaymentRequest( String id, User requested, double amount, boolean qrcode ) {
		this.id = id;
		this.requested = requested;
		this.amount = amount;
		this.qrcode = qrcode;
		paid = false;
	}
	
	public boolean isPaid() {
		return paid;
	}
	
	public boolean isQRcode() {
		return qrcode;
	}
	
	public void markAsPaid( ) {
		paid = true;
	}
	
	public String getId() {
		return id;
	}
	
	public User getRequester( ) {
		return requested;
	}

	public double getAmount() {
		return amount;
	}
}
