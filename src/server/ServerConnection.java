package server;
import java.io.IOException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import exceptions.TrokosException;
import network.Connection;


public class ServerConnection implements AutoCloseable{
    
	private static ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
	private SSLServerSocket svSocket;
    
    
    public ServerConnection(int port) throws IOException {
    	svSocket = (SSLServerSocket) ssf.createServerSocket(9096);
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
