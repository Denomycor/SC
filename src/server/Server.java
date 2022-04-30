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
import model.GroupPayment;
import model.PaymentRequest;
import model.User;
import network.Connection;

public class Server implements AutoCloseable {
	
	private static final String USERS_FN = "users.txt";
	private static final String GROUPS_FN = "groups.txt";
	private static final String GROUPPAY_FN = "grouppay.txt";
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
		loadGroups();
		loadPaymentRequests(loadGroupPayments());
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run( ) {
				System.out.println("Saving Users");
				commitUsers();
				System.out.println("Saving Groups");
				commitGroups();
				System.out.println("Saving GroupPayments");
				commitGroupPayments();
				System.out.println("Saving Payment Requests");
				commitPayRequests();
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
	
	private void loadGroups() throws TrokosException {
		File file = new File(GROUPS_FN);
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] data = line.split(":");
				Group g = new Group(data[0], users.get(data[1]));
				groups.put(data[0], g);
				if(data.length > 2) {
					for(String s : data[2].split(",")) {
						g.addMember(users.get(s));
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new TrokosException("Could not load groups");
		}
	}
	
	private Map<String, GroupPayment> loadGroupPayments() throws TrokosException {
		Map<String, GroupPayment> gps = new HashMap<>();
		File file = new File(GROUPPAY_FN);
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] data = line.split(":");
				GroupPayment gp = new GroupPayment(data[0], data[1]);
				groups.get(data[1]).addGroupPayment(gp);
				gps.put(data[0], gp);
			}
			return gps;
		} catch (FileNotFoundException e) {
			throw new TrokosException("Could not load group payments");
		}
	}
	
	private void loadPaymentRequests(Map<String, GroupPayment> gp) throws TrokosException{
		File file = new File(PAY_REQ_FN);
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] data = line.split(":");
				String groupPayId = data[5].trim().equals("null") ? null : data[5];
				PaymentRequest pr = new PaymentRequest(data[0], data[1], users.get(data[2]), Double.parseDouble(data[3]), 
						Boolean.parseBoolean(data[4]), groupPayId);
				if(Boolean.parseBoolean(data[6])) {
					pr.markAsPaid();
				} else {
					pr.getRequested().addRequest(pr);
				}
				if(pr.isGroup()) {
					gp.get(pr.getGroupPayId()).addPayment(pr);
				}
			}
		} catch (FileNotFoundException e) {
			throw new TrokosException("Could not load payment requests");
		}
	}
	
	private void commitUsers() {
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
	
	private void commitGroups() {
		StringBuilder sb = new StringBuilder();
		
		for(Group g : groups.values()) {
			sb.append(g.getId()+":"+ g.getOwnerId()+":");
			for(User u : g.getMembers()) {
				sb.append(u.getId()+",");
			}
			sb.append("\n");
		}
		
		try (FileWriter writer = new FileWriter(GROUPS_FN)) {
			writer.write(sb.toString());
		} catch (IOException e) {
			System.out.println("Failed saving groups");
		}
	}
	
	private void commitGroupPayments() {
		StringBuilder sb = new StringBuilder();
		
		for(Group g : groups.values()) {
			for(GroupPayment gp : g.getGroupPayments()) {
				sb.append(gp.getId() +":"+ gp.getGroupId());
			}
		}
		
		try (FileWriter writer = new FileWriter(GROUPPAY_FN)) {
			writer.write(sb.toString());
		} catch (IOException e) {
			System.out.println("Failed saving group payments");
		}
	}
	
	private void commitPayRequests() {
		StringBuilder sb = new StringBuilder();
		
		for (User u : users.values()) {
			for(PaymentRequest pr: u.getRequestedPayments()) {
				if(!pr.isGroup()) {
					sb.append(pr.getId()+":"+pr.getRequesterId()+":"+u.getId()+":"+pr.getAmount()+":"+pr.isQRcode()+
							":"+"null"+":"+pr.isPaid()+"\n");
				}
			}
		}
		for (Group g : groups.values()) {
			for(GroupPayment gp : g.getGroupPayments()) {
				for(PaymentRequest pr: gp.getPayments()) {
					sb.append(pr.getId()+":"+pr.getRequesterId()+":"+pr.getRequested().getId()+":"+pr.getAmount()+":"+pr.isQRcode()+
							":"+ pr.getGroupPayId()+":"+pr.isPaid()+"\n");
				}
			}
		}
		 
		try (FileWriter writer = new FileWriter(PAY_REQ_FN)) {
			writer.write(sb.toString());
		} catch (IOException e) {
			System.out.println("Failed saving payment requests");
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
