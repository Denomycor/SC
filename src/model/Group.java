package model;

import java.util.List;

import server.Server;

import java.util.ArrayList;

public class Group {
	
	private User owner;
	private List<User> members;
	private List<GroupPayment> active;
	private List<GroupPayment> complete;
	
	public Group( User owner ) {
		this.owner = owner;
		this.members = new ArrayList<>();
		this.active = new ArrayList<>();
		this.complete = new ArrayList<>();
		members.add(owner);
	}

	public void addMember(User member) {
		members.add(member);
		member.addGroup(this);
	}
	
	public void dividePayment(double amount) {
		double value = amount/members.size();
		GroupPayment payment = new GroupPayment(this);
		for (User m : members) {
			PaymentRequest request = new PaymentRequest(Server.createID(), m, value, false, payment);
			m.addRequest(request);
			payment.addPayment(request);
		}
		active.add(payment);
	}
	
	public User getOwner() {
		return this.owner;
	}
	
	public boolean isMember(User user) {
		return members.contains(user);
	}
	
	public void updatePayment(GroupPayment payment) {
		active.remove(payment);
		complete.add(payment);
	}
}
