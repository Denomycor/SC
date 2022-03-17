package client;
import java.io.IOException;
import java.util.Scanner;

import exceptions.TrokosException;
import network.Connection;
import network.RequestMessage;
import network.ResponseMessage;


public class Client implements AutoCloseable {

	private ClientConnectionProperties connProps;
	private Connection connection;
	private Scanner sc;

	public Client(String[] args, Scanner sc) throws TrokosException, Exception {
		this.sc = sc;
		if( args.length == 2 ) {
			connProps = new ClientConnectionProperties(args[0], args[1]);
			promptPassword();
		} else if( args.length == 3 ) {
			connProps = new ClientConnectionProperties(args[0], args[1], args[2]);
		}
		connection = new Connection(connProps.getHostname(), connProps.getPort());
	}

	private void promptPassword() throws TrokosException {
		System.out.println("Whats the password?");
		connProps.setPassword(sc.next());
	}
	
	public void processRequest( ) throws TrokosException  {
		RequestMessage requested = userInteraction();
		sendRequest(requested);
		
	}
	
	private RequestMessage userInteraction( ) {
		//TODO: ler do sc. returnar RequestMessage com dados 
		return new RequestMessage();
	}
	
	private ResponseMessage sendRequest(RequestMessage msg) throws TrokosException {
		try {
			connection.write(msg.toString());
		} catch (IOException e) {
			throw new TrokosException("Failed sending a message");
		}
		try {
			return new ResponseMessage(connection.read());
		} catch (Exception e) {
			throw new TrokosException("Failed recieving a message");
		}
	}

	@Override
	public void close() throws Exception {
		connection.close();
	}
}