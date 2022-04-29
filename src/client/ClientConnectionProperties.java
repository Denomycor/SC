package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import exceptions.TrokosException;

public class ClientConnectionProperties {
	
	private static final String HOST_REGEX = "[a-zA-Z.0-9]+";
	private static final String ADDRESS_REGEX = "([a-zA-Z.0-9]+):([0-9]+)";
	
	private String hostname;
	private int port;
	private String truststore;
	private String userId;
	private char[] password;
	private KeyStore kstore;

	public ClientConnectionProperties(String[] args) throws TrokosException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		parseAddress(args[0]);
		this.truststore = args[1];
		this.userId = args[4];
		this.password = args[3].toCharArray();

		FileInputStream kfile = new FileInputStream(args[2]);
		kstore = KeyStore.getInstance("PKCS12");
		kstore.load(kfile, password);

	}
	
	private void parseAddress( String address) throws TrokosException {
		Pattern pat = Pattern.compile(ADDRESS_REGEX);
    	Matcher matcher = pat.matcher(address);
    	matcher.find();
    	setHostname(matcher.group(1));
    	int port = 0;
    	try {
    		port = Integer.parseInt(matcher.group(2));
    	}catch (NumberFormatException e) {
			throw new TrokosException("Error. Port is not a number");
		}
    	setPort(port);
	}

	//Static methods
	public static boolean isValidHostname( String hostname ) {
		return hostname.matches(HOST_REGEX);
	}
	public static boolean isValidPort(int port) {
		return port > 0 && port <= 65535;
	}
	
	// Getters
	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getTruststore() {
		return truststore;
	}

	public String getUserId() {
		return userId;
	}

	public PrivateKey getPrivateKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException{
		return (PrivateKey) kstore.getKey("keyRSA", password); //TODO: check private key alias for clients
	}

	public PublicKey getPublicKey() throws KeyStoreException{
		return (PublicKey) kstore.getCertificate("keyRSA").getPublicKey();
	}
	
	//Setters
	public void setHostname(String hostname) throws TrokosException {
		if (isValidHostname(hostname)) {
			this.hostname = hostname;
		} else {
			throw new TrokosException("cannot set hostname. Given value is invalid");
		}
	}

	public void setPort(int port) throws TrokosException {
		if (isValidPort(port)) {
			this.port = port;			
		} else {
			throw new TrokosException("cannot set port. Given value is invalid");
		}
	}
}
