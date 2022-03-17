package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.TrokosException;

public class ClientConnectionProperties {
	
	private static final String HOST_REGEX = "[a-zA-Z.:/]+";
	private static final String ADDRESS_REGEX = "([a-zA-Z.:/]+):([0-9]+)";
	private static final String USERNAME_REGEX = ".+";
	private static final String PASSWORD_REGEX = ".+";
	
	private String hostname;
	private int port;
	private String username;
	private String password;
	
	public ClientConnectionProperties(String address, String username) throws NumberFormatException, TrokosException {
		parseAddress(address);
		setUsername(username);
		this.password = null;
	}
	
	public ClientConnectionProperties(String address, String username, String password) throws NumberFormatException, TrokosException {
		parseAddress(address);
		setUsername(username);
		setPassword(password);
	}
	
	private void parseAddress( String address) throws NumberFormatException, TrokosException {
		Pattern pat = Pattern.compile(ADDRESS_REGEX);
    	Matcher matcher = pat.matcher(address);
    	matcher.find();
    	setHostname(matcher.group(1));
    	setPort(Integer.parseInt(matcher.group(2)));
	}

	
	
	
	//Static methods
	public static boolean isValidHostname( String hostname ) {
		return hostname.matches(HOST_REGEX);
	}
	public static boolean isValidPort(int port) {
		return port > 0 && port <= 65535;
	}
	public static boolean isValidUsername( String username ) {
		return username.matches(USERNAME_REGEX);
	}
	public static boolean isValidPassword( String password ) {
		return password.matches(PASSWORD_REGEX);
	}
	
	// Getters
	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	//Setters
	public void setHostname(String hostname) throws TrokosException {
		if ( isValidHostname(hostname)) {
			this.hostname = hostname;
		} else {
			throw new TrokosException("cannot set hostname given value is invalid");
		}
	}

	public void setPort(int port) throws TrokosException {
		if ( isValidPort(port)) {
			this.port = port;			
		} else {
			throw new TrokosException("cannot set port given value is invalid");
		}
	}

	public void setUsername(String username) throws TrokosException {
		if ( isValidUsername(username) ) {
			this.username = username;
		} else {
			throw new TrokosException("cannot set username given value is invalid");
		}
	}

	public void setPassword(String password) throws TrokosException {
		if (isValidPassword(password)) {
			this.password = password;			
		} else {
			throw new TrokosException("cannot set password given value is invalid");
		}
	}
}
