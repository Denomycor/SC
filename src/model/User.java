package model;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
	
	private final String id;
	private final String username;
	private final String keyFile;
	private double balance;
	private Map<String, PaymentRequest> requestedPayments;
	private List<Group> groups; 
	
	
	public User(String id, String user, String keyFile) {
		this.id = id;
		this.username = user;
		this.keyFile = keyFile;
		requestedPayments = new HashMap<>();
		this.balance = 1000;
	}

	public static User makeUser(String id, String user, String keyFile, Certificate cert) throws CertificateEncodingException, IOException{
		User userN = new User(id, user, keyFile);

		//TODO: generate certificate;
		final FileOutputStream os = new FileOutputStream(keyFile);
		//os.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
		os.write(Base64.getEncoder().encode(cert.getEncoded()));
		//os.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
		os.close();

		return userN;
	}

	public void deposit(double amount) {
		this.balance = balance + amount;
	}
	
	public void withdraw(double amount) {
		this.balance = balance - amount;
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
	
	public PublicKey getKey() throws CertificateException, FileNotFoundException{
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
    	Certificate cert = cf.generateCertificate(new FileInputStream(keyFile));
		return cert.getPublicKey();
	}

	public String getKeyFile(){
		return keyFile;
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
