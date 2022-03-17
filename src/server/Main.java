package server;

import java.io.IOException;

import network.Connection;

public class Main {
	
	public static void main(String[] args){
		int port = 45678;
        if(args.length > 1){
            try {
            	port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
				System.out.println("Cannot Parse given port continued with default");
			}
        }
        
        try ( ServerConnection server = new ServerConnection(port) ) {
        	while(true) {
        		Connection con = server.listen();
        	}
        	
        } catch (IOException e) {
			System.out.println("Error opening or closing sockets");
		}
    }

}
