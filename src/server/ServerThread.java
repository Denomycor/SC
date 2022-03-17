package server;

import network.Connection;

public class ServerThread implements Runnable{
	
	private Connection conn;
	
	public ServerThread( Connection conn ) {
		this.conn = conn;
	}

	@Override
	public void run() {
		System.out.println("I am running");
		// TODO: Read
		
	}

}
