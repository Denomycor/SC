package client;

import java.util.Scanner;

import exceptions.TrokosException;

public class Main {
	
	public static void main(String[] args) {
		
		if (args.length != 5) {
			System.out.println("Incorrect usage");
			return;
		}
		
		ClientConnectionProperties connProps = null;
		try {
			connProps = new ClientConnectionProperties(args);
		} catch (TrokosException e) {
			System.out.println(e.getMessage());
			return;
		} catch (Exception e){
			System.out.println(e.getMessage());
			return;
		}
		 
    	String username = args[4];
    	
		Scanner reader = new Scanner(System.in);
		System.setProperty("javax.net.ssl.trustStore", args[1]);
		System.setProperty("javax.net.ssl.trustStorePassword", "cliente");
		System.setProperty("javax.net.ssl.keyStore", args[2]);
        System.setProperty("javax.net.ssl.keyStorePassword", args[3]);
    	
    	try (Client client = new Client(connProps, reader, username)) {
    		while (true) {
    			try {
    				client.processRequest();    				
    			} catch (TrokosException e) {
					System.out.println("Error occurred:");
					System.out.println(e.getMessage());
				}
			}
    	} catch (TrokosException e) {
    		System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			reader.close();
		}
    }

}
