package client;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import exceptions.TrokosException;
import network.Connection;
import network.RequestMessage;
import network.RequestTypes;
import network.ResponseMessage;


public class Client implements AutoCloseable {

	private ClientConnection connection;
	private Scanner sc;
	private UserAuth userAuth;

	public Client(ClientConnectionProperties connProps, Scanner sc, String username) throws TrokosException, Exception {
		this.sc = sc;
		connection = new ClientConnection(connProps.getHostname(), connProps.getPort());
		userAuth = new UserAuth(connProps, connection);
		if(!userAuth.checkAuthentication(username)){
			throw new TrokosException("Couldn't authenticate user");
		}
		
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