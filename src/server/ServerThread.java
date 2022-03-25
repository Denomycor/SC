package server;

import java.io.IOException;
import java.util.Map;

import exceptions.TrokosException;
import model.Group;
import model.PaymentRequest;
import model.User;
import network.Connection;
import network.RequestMessage;
import network.ResponseMessage;
import network.ResponseStatus;

public class ServerThread extends Thread{
	
	private Connection conn;
	private Map<String, User> users;
	private Map<String, Group> groups;
	private User logged = null;
	
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
				handleRequest(request);
			} catch (Exception e) {
				continue;
			}
			
				
		}
	}
	
	private void handleRequest(RequestMessage request) throws TrokosException {
		String args[] = request.getArgs();
		
		//TODO: decide how to handle NFE
		try {	
			switch (request.getType()) {
			case BALANCE:
				conn.write(new ResponseMessage(ResponseStatus.OK, ""+logged.getBalance()));
				break;
			case MAKE_PAYMENT:
				conn.write(makePayment(args[0], Double.parseDouble(args[1])));
				break;
			case REQUEST_PAYMENT:
				conn.write(requestPayment(args[0], Double.parseDouble(args[1]), false));
				break;
			case VIEW_REQUESTS:
				conn.write(viewRequest());
				break;
			case PAY_REQUEST:
				conn.write(PayRequest(args[0]));
				break;
			case OBTAIN_QR_CODE:
				conn.write(requestPayment(args[0], Double.parseDouble(args[1]), true));
				break;
			case CONFIRM_QR_CODE:
				conn.write(confirmQrcode(args[0]));
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
				conn.write(logUser(args[0], args[1]));
				System.out.println("Got this shit working");
				break;
			default:
				break;
			}
		} catch (IOException e) {
			System.out.println("Error Sending Reply");
		}
	}
	
	private ResponseMessage logUser(String username, String password) {
		for (User u : users.values()) {
			if(u.getUsername() == username && u.checkPassword(password)) {
				logged = u;
				return new ResponseMessage(ResponseStatus.OK);
			}
		}
		return new ResponseMessage(ResponseStatus.ERROR, "User does not exists or password doesnt match");
	}
	
	private ResponseMessage makePayment(String userId, double amount) {
		User target = users.get(userId);
		if (amount > logged.getBalance()) {
			return new ResponseMessage(ResponseStatus.ERROR, "You dont have enough money go work");
		}
		if (target == null) {
			return new ResponseMessage(ResponseStatus.ERROR, "Cant find user with userId = " + userId);
		}
		
		logged.withdraw(amount);
		target.deposit(amount);
		return new ResponseMessage(ResponseStatus.OK);
	}
	
	private ResponseMessage requestPayment( String userId, double ammount, boolean qrcode ) {
		User target = users.get(userId);
		if (target == null) {
			return new ResponseMessage(ResponseStatus.ERROR, "Cant find user with userId = " + userId);
		}
		target.addRequest(new PaymentRequest(Server.createID(), target, ammount, qrcode));
		return new ResponseMessage(ResponseStatus.OK);
	}
	
	private ResponseMessage viewRequest() {
		
		StringBuilder sb = new StringBuilder("\n Current Pending paymentRequests: \n\n");
		
		for (PaymentRequest pr : logged.getRequestedPayments()) {
			if (pr.isPaid()) {
				continue;
			}
			sb.append(pr.getId() + " -------- " + pr.getAmount() + " -------- " + pr.getRequester().getId() + " -> " + pr.getRequester().getUsername() + "\n");
		}
		
		return new ResponseMessage(ResponseStatus.OK, sb.toString());
	}
	
	private ResponseMessage PayRequest( String reqId ) {
		PaymentRequest pr = logged.getRequestedPaymentById(reqId);
		if (pr == null || pr.isQRcode()) {
			return new ResponseMessage(ResponseStatus.ERROR, "Payment Request not found");
		}
		ResponseMessage ret = makePayment(pr.getRequester().getId(), pr.getAmount());
		if (ret.getStatus() == ResponseStatus.OK) {
			pr.markAsPaid();
		}
		return ret;
	}
	
	private ResponseMessage confirmQrcode( String reqId ) {
		PaymentRequest pr = logged.getRequestedPaymentById(reqId);
		if (pr == null || !pr.isQRcode()) {
			return new ResponseMessage(ResponseStatus.ERROR, "Qrcode not found");
		}
		ResponseMessage ret = makePayment(pr.getRequester().getId(), pr.getAmount());
		if (ret.getStatus() == ResponseStatus.OK) {
			logged.removePayRequest(pr);
		}
		return ret;
	}
}