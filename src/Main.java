import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {

    public static class Ref<T> { 
        public T object = null;
    }

    public static String[] getArgs(String[] args, Scanner in){
        if(args.length != 3 && args.length != 2){
            System.out.println("Usage: Trokos <address> <userId> [password]");
            System.exit(0);
        }

        String[] res = {args[0], args[1], null};

        if(args.length == 2){           
            System.out.printf("Password: ");
            res[2] = in.nextLine();
        }else{
            res[2] = args[2];
        }

        return res;
    }

    public static void openConnection(String address, Ref<Socket> socket, Ref<ObjectInputStream> in, Ref<ObjectOutputStream> out) throws UnknownHostException, IOException{
        Integer port = 45678;
        
        if(address.contains(":")){
            String[] split = address.split(":");
            address = split[0];
            port = Integer.parseInt(split[1]);
        }

        System.out.println(address);
        System.out.println(port);

        socket.object = new Socket(address, port);
        in.object = new ObjectInputStream(socket.object.getInputStream());
        out.object = new ObjectOutputStream(socket.object.getOutputStream());

    }

    public static void main(String[] args) throws UnknownHostException, IOException {

        Scanner reader = new Scanner(System.in);
        
        String[] params = getArgs(args, reader);
        String address = params[0];
        String username = params[1];
        String password = params[2];
        
        Ref<Socket> socket = new Ref<>();
        Ref<ObjectInputStream> in = new Ref<>();
        Ref<ObjectOutputStream> out = new Ref<>();
        
        openConnection(address, socket, in, out);
    
        reader.close();
    }


}