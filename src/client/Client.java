package client;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import exceptions.TrokosException;
import network.AuthMessage;
import network.Message;
import network.RequestMessage;
import network.RequestTypes;
import network.ResponseMessage;

public class Client implements AutoCloseable {

	private ClientConnection connection;
	private Scanner sc;
	private String userId;
	private KeyStore kstore;

	public Client(ClientConnectionProperties connProps, String userId) throws TrokosException {
		this.userId = userId;
		sc = new Scanner(System.in);
		
		try (FileInputStream kfile = new FileInputStream(System.getProperty("javax.net.ssl.keyStore"))) {
			kstore = KeyStore.getInstance("PKCS12");
			kstore.load(kfile, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
		} catch (IOException e) {
			throw new TrokosException("cannot find File: keystore");
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			throw new TrokosException("Error in loading keyStore");
		}

		connection = new ClientConnection(connProps.getHostname(), connProps.getPort());
		if (!(checkAuthentication(userId))) {
			throw new TrokosException("Couldn't authenticate user");
		}

	}

	public void processRequest() throws TrokosException {
		RequestMessage requested = userInteraction();
		ResponseMessage rsp = (ResponseMessage) sendRequest(requested);
		System.out.println(rsp.getBody());
	}

	private RequestMessage userInteraction() throws TrokosException {
		System.out.println("Insert commands");
		String input = sc.nextLine();
		String[] splitInput = input.split(" ");
		RequestTypes type = RequestTypes.getRequestType(splitInput[0]);
		if (type == null) {
			throw new TrokosException("Error command not recognized");
		}
		String[] args = Arrays.copyOfRange(splitInput, 1, splitInput.length);
		return new RequestMessage(type, args);
	}

	private Message sendRequest(Message msg) throws TrokosException {
		try {
			connection.write(msg);
		} catch (IOException e) {
			throw new TrokosException("Failed sending a message");
		}
		try {
			return connection.read();
		} catch (Exception e) {
			throw new TrokosException("Failed receiving a message");
		}
	}

	private boolean checkAuthentication(String userId) throws TrokosException {
		//TODO Try catch if authentication fails (stop in main)
		// send userId
		AuthMessage msg = startAuthentication(userId);
		//#2
		PrivateKey priv = getPrivateKey();
		Certificate cert = getPublicCertificate();
		try {
			Cipher c = Cipher.getInstance("RSA");

			c.init(Cipher.ENCRYPT_MODE, priv);
			msg.signature = c.doFinal(msg.nonce.getBytes());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			// Review exception message
			throw new TrokosException("Error encrypting nonce");
		}

		msg.userId = null;

		if (msg.flag) {
			// User exists
			msg.nonce = null;
			msg.pub = null;
		} else {
			// User doesn't exist
			msg.pub = cert;
		}

		msg = (AuthMessage) sendRequest(msg);

		// is authenticated
		return msg.flag;
	}
	
	private AuthMessage startAuthentication(String userId) throws TrokosException {
		AuthMessage msg = new AuthMessage();
		msg.setUserId(userId);
		return (AuthMessage) sendRequest(msg); 
	}

	private PrivateKey getPrivateKey() throws TrokosException {
		try {
			return (PrivateKey) kstore.getKey(userId,
					System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new TrokosException("Error in getting private key");
		}
	}

	private Certificate getPublicCertificate() throws TrokosException {
		try {
			return kstore.getCertificate(userId);
		} catch (KeyStoreException e) {
			throw new TrokosException("Error getting public certificate");
		}
	}

	@Override
	public void close() throws Exception {
		sc.close();
		connection.close();
	}
}