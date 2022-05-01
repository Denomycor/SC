package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupPayment {

	private String groupPayId;
	private List<PaymentRequest> payments;
	private String groupId;

	public GroupPayment(String id, String groupId) {
		this.groupPayId = id;
		this.groupId = groupId;
		this.payments = new ArrayList<>();
	}

	public boolean isPaid() {
		return payments.stream().filter(p -> p.isPaid()).collect(Collectors.toList()).size() == payments.size();
	}

	public void addPayment(PaymentRequest payment) {
		payments.add(payment);
	}

	public List<PaymentRequest> getActive() {
		return payments.stream().filter(p -> !p.isPaid()).collect(Collectors.toList());
	}

	public List<PaymentRequest> getComplete() {
		return payments.stream().filter(p -> p.isPaid()).collect(Collectors.toList());
	}

	public List<PaymentRequest> getPayments() {
		return payments;
	}

	public String getId() {
		return groupPayId;
	}

	public String getGroupId() {
		return groupId;
	}

}
