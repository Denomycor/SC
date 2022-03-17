package server;
import java.io.IOException;
import java.net.ServerSocket;

import network.Connection;


public class ServerConnection implements AutoCloseable{
    private ServerSocket svSocket;
    
    public ServerConnection(int port) throws IOException {
    	svSocket = new ServerSocket(port);
    }

    public Connection listen() throws IOException{
        Connection connection = new Connection(svSocket.accept());
        return connection;
    }

	@Override
	public void close() throws IOException {
		svSocket.close();
	}
 
}
