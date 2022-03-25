package model;

import java.util.List;
import java.util.Map;

import server.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {
	
	private User owner;
	private List<User> members;
	private Map<String, List<PaymentRequest>> activePayments;
	private Map<String, List<PaymentRequest>> completePayments;
	
	public Group( User owner ) {
		this.owner = owner;
		this.members = new ArrayList<>();
		this.activePayments = new HashMap<>();
		this.completePayments = new HashMap<>();
		members.add(owner);
	}

	public void addMember(User member) {
		members.add(member);
	}
	
	public void dividePayment(double amount) {
		List<PaymentRequest> requests = new ArrayList<>();
		double value = amount/members.size();
		for (User m : members) {
			PaymentRequest request = new PaymentRequest(Server.createID(), m, value, false);
			requests.add(request);
			m.addRequest(request);
		}
		activePayments.put(Server.createID(), requests);
	}
	
	public User getOwner() {
		return this.owner;
	}
	
	public boolean isMember(User user) {
		return members.contains(user);
	}
}
