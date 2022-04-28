package server;

public class Main {
	
	public static void main(String[] args){
		
		int port = 45678;
		String cypherPassword = null;
		
        if(args.length == 4) {
            try {
            	port = Integer.parseInt(args[0]);     	
            } catch (NumberFormatException e) {
				System.out.println("Cannot Parse given port continued with default");
			}
            cypherPassword = args[1];
            System.setProperty("javax.net.ssl.keyStore", args[2]);
        	System.setProperty("javax.net.ssl.keyStorePassword", args[3]);
        } else if (args.length == 3) {
        	cypherPassword = args[0];
        	System.setProperty("javax.net.ssl.keyStore", args[1]);
        	System.setProperty("javax.net.ssl.keyStorePassword", args[2]);
        	
        } else {
        	System.out.println("Incorrect number of arguments. usage:");
        	System.out.println("TrokosServer <port> <password-cifra> <keystore> <password-keystore>");
        	return;
        }
        
        try ( Server server = new Server(port, cypherPassword) ) {
        	server.mainLoop();
        	
        } catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
    }

}
