package server.model;

import java.util.HashMap;
import java.util.Map;

public class User {
	
	private String username;
	private String password;
	private double balance;
	private Map<Integer, PaymentRequest> requestedPayments;
	private Map<Integer, Group> groups;
	
	public User(String userPass) {
		//TODO
		init();
	}
	
	public User(String user, String pass) {
		this.username = user;
		this.password = pass;
		init();
	}
	
	private void init( ) {
		requestedPayments = new HashMap<>();
		groups = new HashMap<>();
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
	public String getUsername() {
		return username;
	}

	public double getBalance() {
		return balance;
	}
	
}
