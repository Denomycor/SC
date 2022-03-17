package server;

import java.io.IOException;

import exceptions.TrokosException;

public class Server implements AutoCloseable {
	
	private ServerConnection serverConnection;
	
	public Server(int port) throws TrokosException {
		try {
			serverConnection =  new ServerConnection(port);
		} catch (IOException e) {
			throw new TrokosException("cannot start server");
		}
	}

	@Override
	public void close() throws Exception {
		serverConnection.close();
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
}
