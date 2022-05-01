package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import server.Server;

public class Group {

	private String id;
	private User owner;
	private List<User> members;
	private List<GroupPayment> groupPayments;

	public Group(String id, User owner) {
		this.owner = owner;
		this.id = id;
		this.members = new ArrayList<>();
		this.groupPayments = new ArrayList<>();
	}

	public void addMember(User member) {
		members.add(member);
		member.addGroup(this);
	}

	public void addGroupPayment(GroupPayment gp) {
		groupPayments.add(gp);
	}

	public void dividePayment(double amount) {
		double value = amount / members.size();
		GroupPayment payment = new GroupPayment(Server.createID(), id);
		for (User m : members) {
			PaymentRequest request = new PaymentRequest(Server.createID(), owner.getId(), m, value, false,
					payment.getId());
			m.addRequest(request);
			payment.addPayment(request);
		}
		groupPayments.add(payment);
	}

	public boolean isOwner(User user) {
		return this.owner.equals(user);
	}

	public boolean isMember(User user) {
		return members.contains(user);
	}

	public List<GroupPayment> getActive() {
		return groupPayments.stream().filter(gp -> !gp.isPaid()).collect(Collectors.toList());
	}

	public List<GroupPayment> getComplete() {
		return groupPayments.stream().filter(gp -> gp.isPaid()).collect(Collectors.toList());
	}

	public List<GroupPayment> getGroupPayments() {
		return groupPayments;
	}

	public List<User> getMembers() {
		return members;
	}

	public String getId() {
		return this.id;
	}

	public String getOwnerId() {
		return owner.getId();
	}
}
