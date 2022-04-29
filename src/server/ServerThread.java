package server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;

import exceptions.TrokosException;
import model.Group;
import model.GroupPayment;
import model.PaymentRequest;
import model.User;
import network.AuthMessage;
import network.Connection;
import network.Message;
import network.RequestMessage;
import network.ResponseMessage;
import network.ResponseStatus;

public class ServerThread extends Thread{
	
	private Connection conn;
	private Map<String, User> users;
	private Map<String, Group> groups;
	private User logged = null;

	private Boolean firstResponse = true;
	private User authUser = null;
	private Long nonce;
	
	public ServerThread(Connection conn, Map<String, User> users, Map<String, Group> groups) {
		this.users = users;
		this.groups = groups;
		this.conn = conn;
	}
	
	@Override
	public void run() {
		
		while (true) {
			Message request = null;
			try {
				
				request = conn.read();
				if(request.getMessageType().equals(Message.MessageType.USERAUTH)){
					checkAuthentication((AuthMessage)request);
				}else{
					handleRequest((RequestMessage)request);
				}
			} catch (Exception e) {
				continue;
			}
			
				
		}
	}
	
	private void checkAuthentication(AuthMessage request) throws Exception{
		if(firstResponse){
			//Send nonce msg
			Random rd = new Random();
			nonce = rd.nextLong();
			
			for(User u : users.values()){
				if(u.getUsername().equals(request.userId)){
					authUser = u;
					break;
				}
			}

			request.nonce = nonce.toString();
			request.flag = authUser != null;
			request.pub = null;
			request.signature = null;
			request.userId = null;

			conn.write(request);
			firstResponse = false;

		}else{
			//Receive resp
			if(authUser != null){
				//User exists
				PublicKey pub = authUser.getKey();
				Cipher c = Cipher.getInstance("RSA");
				
        		c.init(Cipher.DECRYPT_MODE, pub);
        		String nonce2 = new String(c.doFinal(request.signature));
				
				request.nonce = null;
				request.pub = null;
				request.signature = null;
				request.userId = null;
				request.flag = nonce.toString().equals(nonce2);
				
				if(request.flag){
					logged = authUser;
				}

				conn.write(request);
				
			}else{
				//User doesn't exist
				Cipher c = Cipher.getInstance("RSA");
				
        		c.init(Cipher.DECRYPT_MODE, request.pub);
        		String nonce2 = new String(c.doFinal(request.signature));
				
				request.nonce = null ;
				request.pub = null;
				request.signature = null;
				request.userId = null;

				if(nonce.toString().equals(nonce2)){
					//TODO: Regist user

					
					request.flag = true;
					logged = authUser;
				}else{
					
					request.flag = false;
				}

				conn.write(request);
			}
		}
	}

	private void handleRequest(RequestMessage request) throws TrokosException {
		String args[] = request.getArgs();
		
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
				conn.write(requestPayment(logged.getId(), Double.parseDouble(args[0]), true));
				break;
			case CONFIRM_QR_CODE:
				conn.write(confirmQrcode(args[0]));
				break;
			case NEW_GROUP:
				conn.write(createGroup(args[0]));
				break;
			case ADD_USER:
				conn.write(addToGroup(args[0], args[1]));
				break;
			case GROUPS:
				conn.write(getGroups());
				break;
			case DIVIDE_PAYMENT:
				conn.write(dividePayment(args[0], Double.parseDouble(args[1])));
				break;
			case STATUS_PAYMENT:
				conn.write(statusPayments(args[0]));
				break;
			case HISTORY:
				conn.write(getHistory(args[0]));
				break;
			default:
				break;
			}
		} catch (IOException e) {
			System.out.println("Error Sending Reply");
		}
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
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}
	
	private ResponseMessage requestPayment( String userId, double ammount, boolean qrcode ) {
		User target = users.get(userId);
		if (target == null) {
			return new ResponseMessage(ResponseStatus.ERROR, "Cant find user with userId = " + userId);
		}
		target.addRequest(new PaymentRequest(Server.createID(), target, ammount, qrcode, null));
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}
	
	private ResponseMessage viewRequest() {
		
		StringBuilder sb = new StringBuilder("\n Current Pending paymentRequests: \n\n");
		
		for (PaymentRequest pr : logged.getRequestedPayments()) {
			if (pr.isPaid()) {
				continue;
			}
			sb.append(pr.getId() + " -------- " + pr.getAmount() + " -------- " + pr.getRequested().getId() + " -> " + pr.getRequested().getUsername() + "\n");
		}
		
		return new ResponseMessage(ResponseStatus.OK, sb.toString());
	}
	
	private ResponseMessage PayRequest( String reqId ) {
        PaymentRequest pr = logged.getRequestedPaymentById(reqId);
        if (pr == null || pr.isQRcode()) {
            return new ResponseMessage(ResponseStatus.ERROR, "Payment Request not found");
        }
        if (pr.getAmount() > logged.getBalance()) {
            return new ResponseMessage(ResponseStatus.ERROR, "You dont have enough money go work");
        }
        logged.withdraw(pr.getAmount());
        pr.getRequested().deposit(pr.getAmount());
        pr.markAsPaid();

        return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
    }
	
	private ResponseMessage confirmQrcode( String reqId ) {
		PaymentRequest pr = logged.getRequestedPaymentById(reqId);
		if (pr == null || !pr.isQRcode()) {
			return new ResponseMessage(ResponseStatus.ERROR, "Qrcode not found");
		}
		ResponseMessage ret = makePayment(pr.getRequested().getId(), pr.getAmount());
		if (ret.getStatus() == ResponseStatus.OK) {
			logged.removePayRequest(pr);
		}
		return ret;
	}

	private ResponseMessage createGroup( String groupID ) {
		if (groups.get(groupID) != null) {
			return new ResponseMessage(ResponseStatus.ERROR, "Group already exists");
		}
		groups.put(groupID, new Group(groupID, logged));
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}
	
	private ResponseMessage addToGroup( String userID, String groupID ) {
		Group group = groups.get(groupID);
		User target = users.get(userID);
		if (group == null || target == null || group.isMember(target)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Invalid group,user or already a member");
		} else if(!group.isOwner(logged)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Not the group owner");
		}
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}
	
	private ResponseMessage getGroups() {
		StringBuilder sb = new StringBuilder("User's Groups: \n\n Owned: \n");
		StringBuilder owned = new StringBuilder();
		StringBuilder belongs = new StringBuilder();
		for(Group g : logged.getGroups()) {
			if(g.isOwner(logged)) {
				owned.append(g.getId()+"\n");
			} else {
				belongs.append(g.getId()+"\n");
			}
		}
		sb.append((owned.length() == 0 ? "None" : owned));
		sb.append("\n Belongs: \n" + ((belongs.length() == 0 ? "None" : belongs)));
		return new ResponseMessage(ResponseStatus.OK, sb.toString());
	}
	
	private ResponseMessage dividePayment( String groupID, double amount) {
		Group group = groups.get(groupID);
		if(group == null || !group.isOwner(logged)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Invalid group or not the owner");
		}
		group.dividePayment(amount);
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}
	
	private ResponseMessage statusPayments( String groupID ) {
		Group group = groups.get(groupID);
		if(group == null || !group.isOwner(logged)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Invalid group or not the owner");
		}
		StringBuilder sb = new StringBuilder("\n Ongoing payments for group " + groupID + ": \n\n");
		for(GroupPayment gp : group.getActive()) {
			sb.append("--------------- \n");
			for(PaymentRequest pr : gp.getActive()) {
				sb.append(pr.getRequested()+"\n");
			}
			sb.append("--------------- \n");
		}
		return new ResponseMessage(ResponseStatus.OK, sb.toString());
	}
	
	private ResponseMessage getHistory( String groupID ) {
		Group group = groups.get(groupID);
		if(group == null || !group.isOwner(logged)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Invalid group or not the owner");
		}
		StringBuilder sb = new StringBuilder("\n Completed payments of the group " + groupID + ": \n\n");
		for(GroupPayment gp : group.getComplete()) {
			sb.append("--------------- \n");
			for(PaymentRequest pr : gp.getComplete()) {
				sb.append(pr.getId() + " -------- " + pr.getAmount() + " -------- " + pr.getRequested().getId() + " -> " + pr.getRequested().getUsername() + "\n");
			}
			sb.append("--------------- \n");
		}
		return new ResponseMessage(ResponseStatus.OK, sb.toString());
	}
}