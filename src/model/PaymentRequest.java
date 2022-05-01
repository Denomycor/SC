package model;

public class PaymentRequest {

	private final String id;
	private final String requesterId;
	private String groupPayId;
	private final User requested;
	private final Double amount;
	private boolean paid;
	private boolean qrcode;

	public PaymentRequest(String id, String requesterId, User requested, double amount, boolean qrcode,
			String groupPayId) {
		this.id = id;
		this.requesterId = requesterId;
		this.requested = requested;
		this.amount = amount;
		this.qrcode = qrcode;
		this.groupPayId = groupPayId;
		paid = false;
	}

	public boolean isPaid() {
		return paid;
	}

	public boolean isQRcode() {
		return qrcode;
	}

	public boolean isGroup() {
		return groupPayId != null;
	}

	public void markAsPaid() {
		paid = true;
	}

	public String getId() {
		return id;
	}

	public String getRequesterId() {
		return requesterId;
	}

	public User getRequested() {
		return requested;
	}

	public Double getAmount() {
		return amount;
	}

	public String getGroupPayId() {
		return groupPayId;
	}

}
