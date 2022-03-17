import java.io.IOException;
import java.net.ServerSocket;


public class ServerConnection extends Connection{
    private ServerSocket svSocket;

    public void openListeningSocket(Integer port) throws IOException{
        svSocket = new ServerSocket(port);
    }

    public void listen() throws IOException{
        socket = svSocket.accept();
    }

    public void closeListeningSocket() throws IOException{
        svSocket.close();
    }
 
}
