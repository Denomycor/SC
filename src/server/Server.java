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
}
