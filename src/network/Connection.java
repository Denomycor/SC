package network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import exceptions.TrokosException;


public class Connection {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public Connection(String hostname, int port) throws TrokosException {
    	try {
    		socket = new Socket(hostname, port);
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
