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

		
		Scanner reader = new Scanner(System.in); 

    	try (Client client = new Client(connProps, reader, connProps.getUserId())) {
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
