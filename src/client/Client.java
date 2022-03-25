package client;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import exceptions.TrokosException;
import network.Connection;
import network.RequestMessage;
import network.RequestTypes;
import network.ResponseMessage;
import network.ResponseStatus;


public class Client implements AutoCloseable {

	private Connection connection;
	private Scanner sc;

	public Client(ClientConnectionProperties connProps, Scanner sc, String username, String password) throws TrokosException {
		this.sc = sc;
		connection = new Connection(connProps.getHostname(), connProps.getPort());
		login(username, password == null ? promptPassword() : password);
	}
	
	private void login(String user, String password) throws TrokosException {
		String args[] = {user, password}; 
		RequestMessage loginRequest = new RequestMessage(RequestTypes.LOGIN, args);
		ResponseMessage rsp = sendRequest(loginRequest);
		if (rsp.getStatus() != ResponseStatus.OK ) {
			throw new TrokosException(rsp.getBody());
		}
	}

	private String promptPassword() {
		System.out.println("Whats the password?");
		return sc.next();
	}
	
	public void processRequest( ) throws TrokosException  {
		RequestMessage requested = userInteraction();
		ResponseMessage rsp = sendRequest(requested);
		System.out.println(rsp.getBody());
	}
	
	private RequestMessage userInteraction( ) throws TrokosException {
		System.out.println("Insert commands");
		String input = sc.nextLine();
		String splitInput[] = input.split(" ");
		RequestTypes type = RequestTypes.getRequestType(splitInput[0]);
		if (type == null) {
			throw new TrokosException("Error command not recognized");
		}
		String args[] = Arrays.copyOfRange(splitInput, 1, splitInput.length);
		return new RequestMessage(type, args);
	}
	
	private ResponseMessage sendRequest(RequestMessage msg) throws TrokosException {
		try {
			connection.write(msg);
		} catch (IOException e) {
			throw new TrokosException("Failed sending a message");
		}
		try {
			return (ResponseMessage) connection.read();
		} catch (Exception e) {
			throw new TrokosException("Failed recieving a message");
		}
	}

	@Override
	public void close() throws Exception {
		connection.close();
	}
}