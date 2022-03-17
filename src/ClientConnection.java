import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientConnection extends Connection {

    public void openSocket(String address) throws UnknownHostException, IOException{
        Integer port = 45678;
        
        if(address.contains(":")){
            String[] split = address.split(":");
            address = split[0];
            port = Integer.parseInt(split[1]);
        }

        socket = new Socket(address, port);
    }

}
