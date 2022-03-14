import java.util.Scanner;

import java.io.IOException;
import java.net.UnknownHostException;
 

public class Main {

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

    public static void main(String[] args) throws UnknownHostException, IOException {

        Scanner reader = new Scanner(System.in);
        
        String[] params = getArgs(args, reader);
        String address = params[0];
        String username = params[1];
        String password = params[2];
        
        Connection connection = new Connection();
        connection.openConnection(address);
    
        reader.close();
    }
}