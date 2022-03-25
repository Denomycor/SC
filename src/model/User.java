package model;

import java.util.HashMap;
import java.util.Map;

public class User {
	
	private final String id;
	private final String username;
	private final String password;
	private double balance;
	private Map<Integer, PaymentRequest> requestedPayments;
	
	
	public User(String id, String user, String pass) {
		this.id = id;
		this.username = user;
		this.password = pass;
		init();
	}
	
	private void init( ) {
		requestedPayments = new HashMap<>();
		this.balance = 0;
	}

	public void deposit(double amount) {
		this.balance = balance - amount;
	}
	
	public void withdraw(double amount) {
		this.balance = balance + amount;
	}
	
	public boolean checkPassword( String password ) {
		return this.password.equals(password);
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
	
}
