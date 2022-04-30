package client;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Scanner;

import exceptions.TrokosException;
import helper.Helper;
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

	public void processRequest() throws TrokosException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {
		RequestMessage requested = userInteraction();
		ResponseMessage rsp = (ResponseMessage) sendRequest(requested);
		System.out.println(rsp.getBody());
	}

	private RequestMessage userInteraction() throws TrokosException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {
		System.out.println("Insert commands");
		String input = sc.nextLine();
		String[] splitInput = input.split(" ");
		RequestTypes type = RequestTypes.getRequestType(splitInput[0]);
		if (type == null) {
			throw new TrokosException("Error command not recognized");
		}
		String[] args = Arrays.copyOfRange(splitInput, 1, splitInput.length);
		RequestMessage r = new RequestMessage(type, args);
		parseRequest(r);
		return r;
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

	private boolean checkAuthentication(String userId){
		try{
			AuthMessage msg = startAuthentication(userId);

			PrivateKey priv = getPrivateKey();
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(priv);
			signature.update(msg.getNonce().getBytes());
			


			if(msg.isFlag()){
				//User exists
				msg.setSignedObject(signature.sign());

			}else{
				//User doesn't exist
				Certificate certificate = getPublicCertificate();
				msg.setSignedObject(signature.sign());
				msg.setCertificate(certificate);

			}

			msg = (AuthMessage) sendRequest(msg);
			return msg.isFlag();
		}catch(Exception e){
			return false;
		}
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

	private void parseRequest(RequestMessage request) throws TrokosException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException{
		if(request.getType().equals(RequestTypes.MAKE_PAYMENT)){	
			byte[] data = Helper.StringArrayToBytes(request.getArgs());

			PrivateKey priv = getPrivateKey();
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(priv);
			signature.update(data);
			request.setSignature(signature.sign());

		}else if(request.getType().equals(RequestTypes.PAY_REQUEST)){
			String[] args = {request.getArgs()[0]}; //reqId
			String[] toSign = {request.getArgs()[1], request.getArgs()[2]}; //value, userId
			request.setArgs(args);

			byte[] data = Helper.StringArrayToBytes(toSign);

			PrivateKey priv = getPrivateKey();
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(priv);
			signature.update(data);
			request.setSignature(signature.sign());

		}else if(request.getType().equals(RequestTypes.CONFIRM_QR_CODE)){
			//TODO
		}
	}

	@Override
	public void close() throws Exception {
		sc.close();
		connection.close();
	}
}