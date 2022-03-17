import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientConnection {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    
    public void openSocket(String address) throws UnknownHostException, IOException{
        Integer port = 45678;
        
        if(address.contains(":")){
            String[] split = address.split(":");
            address = split[0];
            port = Integer.parseInt(split[1]);
        }

        socket = new Socket(address, port);
    }

    public void closeSocket() throws IOException{
        socket.close();
    }

    public void openConnection() throws IOException{ 
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void closeConnection() throws IOException{
        in.close();
        out.close();
    }
    
    public String read() throws ClassNotFoundException, IOException{
        return (String) in.readObject();
    }
    
    public void write(String write) throws IOException{
        out.writeObject(write);
    }
    
        public Socket getSocket(){
            return socket;
        }

    public void setSocket(Socket socket){
        this.socket = socket;
    }

}
