package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.TrokosException;

public class User {

	private final String userId;
	private final String keyFile;
	private double balance;
	private Map<String, PaymentRequest> requestedPayments;
	private List<Group> groups;

	public User(String userId, String keyFile) {
		this.userId = userId;
		this.keyFile = keyFile;
		requestedPayments = new HashMap<>();
		this.balance = 1000;
		groups = new ArrayList<>();
	}

	public User(String userId, String keyFile, Certificate cert) {
		this(userId, keyFile);

		try {
			FileOutputStream fos = new FileOutputStream(new File(keyFile));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(cert.getEncoded());
			fos.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void deposit(double amount) {
		this.balance = balance + amount;
	}

	public void withdraw(double amount) {
		this.balance = balance - amount;
	}

	public void addRequest(PaymentRequest pr) {
		requestedPayments.put(pr.getId(), pr);
	}

	public void addGroup(Group group) {
		groups.add(group);
	}

	// Getters

	public PublicKey getKey() throws TrokosException {
		Certificate certificate = null;

		try (FileInputStream fis = new FileInputStream(new File("rsc/maybe/cert/" + keyFile))) {
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			certificate = cf.generateCertificate(fis);
		} catch (IOException | CertificateException e) {
			e.printStackTrace();
			throw new TrokosException("Can not get user public key");
		}
		return certificate.getPublicKey();
	}

	public String getKeyFile() {
		return keyFile;
	}

	public String getId() {
		return userId;
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
