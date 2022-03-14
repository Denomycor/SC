import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    
    public Socket socket;
    public ObjectInputStream in;
    public ObjectOutputStream out;

    public void openConnection(String address) throws UnknownHostException, IOException{
        Integer port = 45678;
        
        if(address.contains(":")){
            String[] split = address.split(":");
            address = split[0];
            port = Integer.parseInt(split[1]);
        }

        socket = new Socket(address, port);
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void closeConnection() throws IOException{
        in.close();
        out.close();
        socket.close();
    }
}
