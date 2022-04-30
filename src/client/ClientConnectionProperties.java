package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.TrokosException;

public class ClientConnectionProperties {
	
	private static final String HOST_REGEX = "[a-zA-Z.0-9]+";
	private static final String ADDRESS_REGEX = "([a-zA-Z.0-9]+):([0-9]+)";
	
	private String hostname;
	private int port;

	public ClientConnectionProperties(String address) throws TrokosException {
		parseAddress(address);
	}
	
	private void parseAddress( String address) throws TrokosException {
		Pattern pat = Pattern.compile(ADDRESS_REGEX);
    	Matcher matcher = pat.matcher(address);
    	matcher.find();
    	setHostname(matcher.group(1));
    	int inPort = 0;
    	try {
    		inPort = Integer.parseInt(matcher.group(2));
    	}catch (NumberFormatException e) {
			throw new TrokosException("Error. Port is not a number");
		}
    	setPort(inPort);
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
