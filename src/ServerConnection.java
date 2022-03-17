import java.io.IOException;
import java.net.ServerSocket;


public class ServerConnection{
    private ServerSocket svSocket;

    public void openListeningSocket(Integer port) throws IOException{
        svSocket = new ServerSocket(port);
    }

    public ClientConnection listen() throws IOException{
        ClientConnection connection = new ClientConnection();
        connection.setSocket(svSocket.accept());
        return connection;
    }

    public void closeListeningSocket() throws IOException{
        svSocket.close();
    }
 
}
