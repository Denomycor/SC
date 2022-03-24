package model;

public class PaymentRequest {
	
	private int amount;
	private boolean paid;
	
	public PaymentRequest( int amount ) {
		this.amount = amount;
		paid = false;
	}
	
	public boolean isPaid() {
		return paid;
	}
	
	public void markAsPaid( ) {
		paid = true;
	}
}
