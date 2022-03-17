import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public abstract class Connection {

    protected Socket socket;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;

    public void openConnection() throws IOException{ 
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void closeConnection() throws IOException{
        in.close();
        out.close();
        closeSocket();
    }

    public void closeSocket() throws IOException{
        socket.close();
    }

    public Socket getSocket(){
        return socket;
    }

    public String readSocket() throws ClassNotFoundException, IOException{
        return (String) in.readObject();
    }

    public void writeSocket(String write) throws IOException{
        out.writeObject(write);
    }
}
