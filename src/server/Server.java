package server;

import java.io.IOException;
import java.util.Map;

import exceptions.TrokosException;
import server.model.User;

public class Server implements AutoCloseable {
	
	private final static String USERS_FN = "users.txt";
	
	private ServerConnection serverConnection;
	private Map<Integer, User> users;
	
	public Server(int port) throws TrokosException {
		try {
			serverConnection =  new ServerConnection(port);
		} catch (IOException e) {
			throw new TrokosException("cannot start server");
		}
		loadUsers();
	}

	public void mainLoop() {
		while (true) {
			try {
				// TODO: open new thread with connection to process request 
				serverConnection.listen();
			}catch (TrokosException e) {
				System.out.println("Server Error: " + e.getMessage());
			}
			
		}
	}
	
	private void loadUsers() {
		// TODO
	}
	
	private void writeUsers() {
		// TODO
	}
	
	@Override
	public void close() throws Exception {
		serverConnection.close();
		writeUsers();
	}
}
