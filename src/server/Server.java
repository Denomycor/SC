package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import exceptions.TrokosException;
import model.Group;
import model.PaymentRequest;
import model.User;
import network.Connection;

public class Server implements AutoCloseable {
	
	private static final String USERS_FN = "users.txt";
	private static final String PAY_REQ_FN = "pr.txt";
	
	private ServerConnection serverConnection;
	private ConcurrentHashMap<String, User> users;
	private ConcurrentHashMap<String, Group> groups;
	private String cypherPassword;
	
	private static AtomicLong idCounter = new AtomicLong();
	
	public Server(int port, String cypherPassword) throws TrokosException {
		users = new ConcurrentHashMap<>();
		groups = new ConcurrentHashMap<>();
		this.cypherPassword = cypherPassword;
		try {
			serverConnection = new ServerConnection(port);
		} catch (IOException e) {
			e.printStackTrace();
			throw new TrokosException("cannot start server");
		}
		loadUsers();
		loadPaymentRequests();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run( ) {
				System.out.println("Saving Payment Requests");
				commitPayRequests();
				System.out.println("Saving Users");
				commitUsers();
			}
		});
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

	public void addUser(User user){
		users.put(user.getId(), user);
	}
	
	private void loadUsers() throws TrokosException {
		File file = new File(USERS_FN);
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] data = line.split(":");
				users.put(data[0], new User(data[0],data[1]));
			}
		} catch (FileNotFoundException e) {
			throw new TrokosException("Could not load users");
		}
	}
	
	private void loadPaymentRequests( ) {
		File file = new File(PAY_REQ_FN);
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] data = line.split(":");
				
				User source = users.get(data[0]);
				User target = users.get(data[1]);
				
				String id = createID();
				target.addRequest(new PaymentRequest(id, source, Double.parseDouble(data[2]), Boolean.parseBoolean(data[3]), null));
			}
		} catch (FileNotFoundException e) {
			System.out.println("No payment requests found to load");
		}
	}
	
	private void commitPayRequests() {
		StringBuilder sb = new StringBuilder();
		
		for (User u : users.values()) {
			for( PaymentRequest pr: u.getRequestedPayments()) {
				sb.append(pr.getRequested().getId() + ":" + u.getId() + ":" + pr.getAmount() + ":" + pr.isQRcode() + "\n");
			}
		}
		 
		try (FileWriter writer = new FileWriter(PAY_REQ_FN)) {
			writer.write(sb.toString());
		} catch (IOException e) {
			System.out.println("Failed saving payment requests");
		}
	}
	
	private void commitUsers(){
		StringBuilder sb = new StringBuilder();
		
		for (User u : users.values()) {
			sb.append(u.getId()+":"+u.getKeyFile()+"\n");
		}

		try (FileWriter writer = new FileWriter(USERS_FN)) {
			writer.write(sb.toString());
		} catch (IOException e) {
			System.out.println("Failed saving users");
		}
	}

	@Override
	public void close() throws Exception {
		serverConnection.close();
		commitPayRequests();
		commitUsers();
	}
	
	// static
	public static String createID() {
	    return String.valueOf(idCounter.getAndIncrement());
	}
}
