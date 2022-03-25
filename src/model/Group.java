package model;

import java.util.List;

import server.Server;

import java.util.ArrayList;

public class Group {
	
	private User owner;
	private String id;
	private List<User> members;
	private List<GroupPayment> active;
	private List<GroupPayment> complete;
	
	public Group( String id, User owner ) {
		this.owner = owner;
		this.id = id;
		this.members = new ArrayList<>();
		this.active = new ArrayList<>();
		this.complete = new ArrayList<>();
		addMember(owner);
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
	
	public boolean isOwner(User user) {
		return this.owner == user;
	}
	
	public List<GroupPayment> getActive() {
		return this.active;
	}
	
	public List<GroupPayment> getComplete() {
		return this.complete;
	}
	
	public boolean isMember(User user) {
		return members.contains(user);
	}
	
	public String getId() {
		return this.id;
	}
	
	public void updatePayment(GroupPayment payment) {
		active.remove(payment);
		complete.add(payment);
	}
}
