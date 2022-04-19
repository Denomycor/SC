package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
	
	private final String id;
	private final String username;
	private final String password;
	private double balance;
	private Map<String, PaymentRequest> requestedPayments;
	private List<Group> groups; 
	
	
	public User(String id, String user, String pass) {
		this.id = id;
		this.username = user;
		this.password = pass;
		requestedPayments = new HashMap<>();
		this.balance = 1000;
	}

	public void deposit(double amount) {
		this.balance = balance + amount;
	}
	
	public void withdraw(double amount) {
		this.balance = balance - amount;
	}
	
	public boolean checkPassword( String password ) {
		return this.password.equals(password);
	}
	
	public void addRequest( PaymentRequest pr ) {
		requestedPayments.put(pr.getId(), pr);
	}
	
	public void addGroup( Group group ) {
		groups.add(group);
	}
	
	// Getters
	public String getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}

	public double getBalance() {
		return balance;
	}
	
	public List<Group> getGroups() {
		return groups;
	}
	
	public Collection<PaymentRequest> getRequestedPayments() {
		return requestedPayments.values();
	}
	
	public PaymentRequest getRequestedPaymentById(String reqId) {
		return requestedPayments.get(reqId);
	}

	public void removePayRequest(PaymentRequest pr) {
		requestedPayments.remove(pr.getId());
	}
	
}
