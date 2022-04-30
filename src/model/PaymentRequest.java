package model;

public class PaymentRequest {
	
	private final String id;
	private final User requested;
	private final Double amount;
	private boolean paid;
	private boolean qrcode;
	private GroupPayment group;
	
	public PaymentRequest( String id, User requested, double amount, boolean qrcode, GroupPayment group ) {
		this.id = id;
		this.requested = requested;
		this.amount = amount;
		this.qrcode = qrcode;
		this.group = group;
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
		if(group != null) {
			group.updatePayment(this);
		}
	}
	
	public String getId() {
		return id;
	}
	
	public User getRequested( ) {
		return requested;
	}

	public Double getAmount() {
		return amount;
	}
}
