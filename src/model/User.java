package model;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

		String pub = Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded());
		FileWriter w = new FileWriter(new File(keyFile));
		w.write(pub);
		w.close();

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
	
	public PublicKey getKey() throws CertificateException, FileNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException{
		Scanner sc = new Scanner(new File(keyFile));
		String pubString = sc.nextLine();
		sc.close();
		byte[] publicBytes = Base64.getDecoder().decode(pubString);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(keySpec);	
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
