package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import exceptions.TrokosException;
import model.Group;
import model.User;
import network.Connection;

public class Server implements AutoCloseable {
	
	private final static String USERS_FN = "users.txt";
	
	private ServerConnection serverConnection;
	private Map<String, User> users;
	private Map<String, Group> groups;
	
	private static AtomicLong idCounter = new AtomicLong();
	
	public Server(int port) throws TrokosException {
		users = new HashMap<>();
		groups = new HashMap<>();
		try {
			serverConnection =  new ServerConnection(port);
		} catch (IOException e) {
			throw new TrokosException("cannot start server");
		}
		loadUsers();
	}

	public void mainLoop() {
		while (true) {
			try { 
				Connection con = serverConnection.listen();
				ServerThread st = new ServerThread(con, users, groups);
				st.start();
			}catch (TrokosException e) {
				System.out.println("Server Error: " + e.getMessage());
			}
			
		}
	}
	
	private void loadUsers() throws TrokosException {
		
		File file = new File(USERS_FN);
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String data[] = line.split(":");
				String id = createID();
				users.put(id, new User(id,data[0],data[1]));
			}
		} catch (FileNotFoundException e) {
			throw new TrokosException("Could not load users");
		}
	}
	
	@Override
	public void close() throws Exception {
		serverConnection.close();
	}
	
	// static
	public static String createID() {
	    return String.valueOf(idCounter.getAndIncrement());
	}
}
