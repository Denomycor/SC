package client;

import java.util.Scanner;

import exceptions.TrokosException;

public class Main {
	
	public static void main(String[] args) {
    	
		Scanner reader = new Scanner(System.in); 
    	
    	try (Client client = new Client(args, reader)) {
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
