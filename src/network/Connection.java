package network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Connection {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public Connection(String hostname, int port) throws UnknownHostException, IOException {
    	socket = new Socket(hostname, port);
    	openConnection();
    }
    
    public Connection(Socket socket) throws IOException {
    	this.socket = socket;
    	openConnection();
    }
    
    private void openConnection() throws IOException{ 
    	in = new ObjectInputStream(socket.getInputStream());
    	out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void close() throws IOException{
    	in.close();
        out.close();
        socket.close();
    }

    public String read() throws ClassNotFoundException, IOException{
        return (String) in.readObject();
    }
    
    public void write(String write) throws IOException{
        out.writeObject(write);
    }
}
