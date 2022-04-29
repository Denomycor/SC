package network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import exceptions.TrokosException;


public class Connection {
	
	private static SocketFactory sf = SSLSocketFactory.getDefault();
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public Connection(String hostname, int port) throws TrokosException {
    	try {
    		socket = sf.createSocket(hostname, port);
    		openConnection();
    	} catch (Exception e) {
			throw new TrokosException("Can not connect to server");
		}
    }
    
    public Connection(Socket socket) throws IOException {
    	this.socket = socket;
    	openConnection();
    }
    
    private void openConnection() throws IOException{
    	out = new ObjectOutputStream(socket.getOutputStream());
    	in = new ObjectInputStream(socket.getInputStream());
    }

    public void close() throws IOException{
    	in.close();
        out.close();
        socket.close();
    }

    public Message read() throws ClassNotFoundException, IOException{
        return (Message) in.readObject();
    }
    
    public void write(Message write) throws IOException{
        out.writeObject(write);
    }
}
