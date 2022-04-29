package server;
import java.io.IOException;
import java.net.ServerSocket;

import exceptions.TrokosException;
import network.Connection;


public class ServerConnection implements AutoCloseable{
    private ServerSocket svSocket;
    
    public ServerConnection(int port) throws IOException {
    	svSocket = new ServerSocket(port);
    }

    public Connection listen() throws TrokosException {
        try {
        	return new Connection(svSocket.accept());
        } catch (IOException e) {
			throw new TrokosException("Cannot accept connection");
		}
    }

	@Override
	public void close() throws IOException {
		svSocket.close();
	}
 
}
