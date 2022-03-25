package model;

import java.util.ArrayList;
import java.util.List;

public class GroupPayment {
	
	private Group group;
	private List<PaymentRequest> active;
	private List<PaymentRequest> complete;
	
	public GroupPayment(Group group) {
		this.group = group;
		this.active = new ArrayList<>();
		this.complete = new ArrayList<>();
	}
	
	public boolean isPaid() {
		return active.isEmpty();
	}
	
	public void addPayment(PaymentRequest payment) {
		active.add(payment);
	}
	
	public List<PaymentRequest> getActive() {
		return this.active;
	}
	
	public List<PaymentRequest> getComplete() {
		return this.complete;
	}

	public void updatePayment(PaymentRequest payment) {
		active.remove(payment);
		complete.add(payment);
		if(active.isEmpty()) {
			group.updatePayment(this);
		}
	}
}
