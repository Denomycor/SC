package server;

import java.util.Map;

import model.Group;
import model.User;
import network.Connection;
import network.Message;
import network.RequestMessage;
import network.RequestTypes;

public class ServerThread extends Thread{
	
	private Connection conn;
	private Map<String, User> users;
	private Map<String, Group> groups;
	
	public ServerThread(Connection conn, Map<String, User> users, Map<String, Group> groups) {
		this.users = users;
		this.groups = groups;
		this.conn = conn;
	}
	//TODO: don't forget to close connection
	@Override
	public void run() {
		
		while (true) {
			RequestMessage request = null;
			try {
				request = (RequestMessage) conn.read();
			} catch (Exception e) {
				continue;
			}
			
			switch (request.getType()) {
			
			case BALANCE:
				break;
			case MAKE_PAYMENT:
				break;
			case VIEW_REQUESTS:
				break;
			case PAY_REQUEST:
				break;
			case OBTAIN_QR_CODE:
				break;
			case CONFIRM_QR_CODE:
				break;
			case NEW_GROUP:
				break;
			case ADD_USER:
				break;
			case GROUPS:
				break;
			case DIVIDE_PAYMENT:
				break;
			case STATUS_PAYMENT:
				break;
			case HISTORY:
				break;
			case LOGIN:
				System.out.println("Got this shit working");
				break;
			default:
				break;
			}	
		}
	}
}