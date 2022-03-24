package client;

import java.util.Scanner;

import exceptions.TrokosException;

public class Main {
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.out.println("Incorrect usage");
			return;
		}
		
		ClientConnectionProperties connProps = null;
		try {
			connProps = new ClientConnectionProperties(args[0]);
		} catch (TrokosException e) {
			System.out.println(e.getMessage());
			return;
		}
		 
    	String username = args[1];
    	String password = null;
    	try {
    		password = args[2];
    	} catch (ArrayIndexOutOfBoundsException e) {
    		// Do nothing
		}
    	
		Scanner reader = new Scanner(System.in); 
    	
    	try (Client client = new Client(connProps, reader, username, password)) {
    		while (true) {
    			try {
    				client.processRequest();    				
    			} catch (TrokosException e) {
					System.out.println("Error occurred:");
					System.out.println(e.getMessage());
				}
			}
    	} catch (TrokosException e) {
    		System.out.println("Program terminated with the following error:");
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
