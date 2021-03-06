package server;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import exceptions.TrokosException;
import model.Group;
import model.GroupPayment;
import model.PaymentRequest;
import model.TransactionDto;
import model.User;
import network.AuthMessage;
import network.Connection;
import network.Message;
import network.RequestMessage;
import network.ResponseMessage;
import network.ResponseStatus;
import server.blockchain.Transaction;
import server.blockchain.TransactionLog;

public class ServerThread extends Thread {

	private Connection conn;
	private Map<String, User> users;
	private Map<String, Group> groups;
	private Map<String, PaymentRequest> qrPayments;
	private User logged = null;
	private TransactionLog transactionLog;

	private String userIdAuth = null;
	private User foundUser = null;
	private Long nonce = 0L;

	public ServerThread(Connection conn, Map<String, User> users, Map<String, Group> groups,
			Map<String, PaymentRequest> qrPayments, TransactionLog transactionLog) {
		this.users = users;
		this.groups = groups;
		this.qrPayments = qrPayments;
		this.conn = conn;
		this.transactionLog = transactionLog;
	}

	@Override
	public void run() {

		while (true) {
			Message request = null;
			try {

				request = conn.read();
				if (request.getMessageType().equals(Message.MessageType.USERAUTH)) {
					checkAuthentication((AuthMessage) request);
				} else {
					handleRequest((RequestMessage) request);
				}
			} catch (TrokosException e) {
				e.printStackTrace();
			} catch (Exception e){
				return;
			}

		}
	}

	private void checkAuthentication(AuthMessage request) throws Exception {
		if (userIdAuth == null) {
			firstStepAuth(request);
		} else {
			secondStepAuth(request);
			nonce = 0L;
			foundUser = null;
			userIdAuth = null;
		}
	}

	private void firstStepAuth(AuthMessage request) throws IOException {
		userIdAuth = request.getUserId();
		foundUser = users.get(userIdAuth);

		Random rd = new Random();
		nonce = rd.nextLong();

		request.setFlag(foundUser != null);
		request.setNonce(nonce.toString());

		conn.write(request);
	}

	private void secondStepAuth(AuthMessage request) throws Exception {
		Signature signature = Signature.getInstance("MD5withRSA");

		if (foundUser != null) {
			// User exists
			byte[] signed = request.getSignedObject();
			PublicKey key = foundUser.getKey();

			signature.initVerify(key);
			signature.update(nonce.toString().getBytes());

			if (signature.verify(signed)) {
				// Success, login user
				logged = foundUser;
				request.setFlag(true);
			} else {
				// Miss
				request.setFlag(false);
			}

		} else {
			// User doesn't exist
			byte[] signed = request.getSignedObject();
			PublicKey key = request.getCertificate().getPublicKey();

			signature.initVerify(key);
			signature.update(nonce.toString().getBytes());

			if (signature.verify(signed)) {
				// Success, create user
				logged = new User(userIdAuth, Server.SERVER_PATH + userIdAuth + ".cer", request.getCertificate());
				users.put(logged.getId(), logged);

				request.setFlag(true);
			} else {
				// Miss
				request.setFlag(false);
			}

		}

		conn.write(request);
	}

	private void handleRequest(RequestMessage request) throws TrokosException {
		String[] args = request.getArgs();

		try {
			switch (request.getType()) {
				case BALANCE:
					conn.write(new ResponseMessage(ResponseStatus.OK, "" + logged.getBalance()));
					break;
				case MAKE_PAYMENT:
					conn.write(makePayment(args[0], Double.parseDouble(args[1])));
					break;
				case REQUEST_PAYMENT:
					conn.write(requestPayment(args[0], Double.parseDouble(args[1])));
					break;
				case VIEW_REQUESTS:
					conn.write(viewRequest());
					break;
				case PAY_REQUEST:
					conn.write(PayRequest(args[0]));
					break;
				case OBTAIN_QR_CODE:
					conn.write(obtainQRCode(Double.parseDouble(args[0])));
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

	private ResponseMessage makePayment(String userId, double amount) throws TrokosException {
		User target = users.get(userId);
		if (amount > logged.getBalance()) {
			return new ResponseMessage(ResponseStatus.ERROR, "You dont have enough money go work");
		} else if (target == null) {
			return new ResponseMessage(ResponseStatus.ERROR, "Cant find user with userId = " + userId);
		}

		Transaction t = new Transaction(logged.getId(), target.getId(), amount);

		if (!verifySignedTransaction(new TransactionDto(t))) {
			return new ResponseMessage(ResponseStatus.ERROR, "Signature did not verify. Transaction Aborted");
		}

		transactionLog.addTransaction(t);

		logged.withdraw(amount);
		target.deposit(amount);
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}

	private ResponseMessage requestPayment(String userId, double ammount) {
		User target = users.get(userId);
		if (target == null) {
			return new ResponseMessage(ResponseStatus.ERROR, "Cant find user with userId = " + userId);
		}
		target.addRequest(new PaymentRequest(Server.createID(), logged.getId(), target, ammount, null));
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}
	

	private ResponseMessage viewRequest() {

		StringBuilder sb = new StringBuilder("\n Current Pending paymentRequests: \n\n");

		for (PaymentRequest pr : logged.getRequestedPayments()) {
			sb.append(pr.getId() + " -------- " + pr.getAmount() + " ---------> " + pr.getRequesterId() + "\n");
		}

		return new ResponseMessage(ResponseStatus.OK, sb.toString());
	}

	private ResponseMessage PayRequest(String reqId) throws TrokosException {
		PaymentRequest pr = logged.getRequestedPaymentById(reqId);
		if (pr == null) {
			return new ResponseMessage(ResponseStatus.ERROR, "Payment Request not found");
		} else if (pr.getAmount() > logged.getBalance()) {
			return new ResponseMessage(ResponseStatus.ERROR, "You dont have enough money go work");
		}

		Transaction t = new Transaction(logged.getId(), pr.getRequesterId(), pr.getAmount());
		
		if (!verifySignedTransaction(new TransactionDto(t))) {
			return new ResponseMessage(ResponseStatus.ERROR, "Signature did not verify. Transaction Aborted");
		}

		transactionLog.addTransaction(t);

		logged.withdraw(pr.getAmount());
		users.get(pr.getRequesterId()).deposit(pr.getAmount());
		pr.markAsPaid();
		logged.removePayRequest(pr);

		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}
	
	private ResponseMessage obtainQRCode(double amount) {
		String qrcode = UUID.randomUUID().toString();
		qrPayments.put(qrcode, new PaymentRequest(Server.createID(), logged.getId(), null, amount, null));
		return new ResponseMessage(ResponseStatus.OB_QR, qrcode);
	}

	private ResponseMessage confirmQrcode(String qrCode) throws TrokosException {
		PaymentRequest pr = qrPayments.get(qrCode);
		if (pr == null) {
			return new ResponseMessage(ResponseStatus.ERROR, "Qrcode not found");
		}
<<<<<<< HEAD

		Transaction t = new Transaction(pr.getRequesterId(), pr.getAmount());
		
		if (!verifySignedTransaction(t)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Signature did not verify. Transaction Aborted");
		}

		transactionLog.addTransaction(t);

		ResponseMessage ret = makePayment(pr.getRequested().getId(), pr.getAmount());
		if (ret.getStatus() == ResponseStatus.OK) {
			logged.removePayRequest(pr);
=======
		qrPayments.remove(qrCode);
		if(logged.getBalance() < pr.getAmount()) {
			return new ResponseMessage(ResponseStatus.ERROR, "Not enough balance");
>>>>>>> qrcode
		}
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}

	private ResponseMessage createGroup(String groupID) {
		if (groups.get(groupID) != null) {
			return new ResponseMessage(ResponseStatus.ERROR, "Group already exists");
		}
		groups.put(groupID, new Group(groupID, logged));
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}

	private ResponseMessage addToGroup(String userID, String groupID) {
		Group group = groups.get(groupID);
		User target = users.get(userID);
		if (group == null || target == null || group.isMember(target)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Invalid group,user or already a member");
		} else if (!group.isOwner(logged)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Not the group owner");
		}
		group.addMember(target);
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}

	private ResponseMessage getGroups() {
		StringBuilder sb = new StringBuilder("User's Groups: \n\n Owned: \n");
		StringBuilder owned = new StringBuilder();
		StringBuilder belongs = new StringBuilder();
		for (Group g : logged.getGroups()) {
			if (g.isOwner(logged)) {
				owned.append(g.getId() + "\n");
			} else {
				belongs.append(g.getId() + "\n");
			}
		}
		sb.append((owned.length() == 0 ? "None" : owned));
		sb.append("\n Belongs: \n" + ((belongs.length() == 0 ? "None" : belongs)));
		return new ResponseMessage(ResponseStatus.OK, sb.toString());
	}

	private ResponseMessage dividePayment(String groupID, double amount) {
		Group group = groups.get(groupID);
		if (group == null || !group.isOwner(logged)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Invalid group or not the owner");
		}
		group.dividePayment(amount);
		return new ResponseMessage(ResponseStatus.OK, "Operation Sucessful");
	}

	private ResponseMessage statusPayments(String groupID) {
		Group group = groups.get(groupID);
		if (group == null || !group.isOwner(logged)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Invalid group or not the owner");
		}
		StringBuilder sb = new StringBuilder("\n Ongoing payments for group " + groupID + ": \n\n");
		for (GroupPayment gp : group.getActive()) {
			sb.append("--------------- \n");
			for (PaymentRequest pr : gp.getActive()) {
				sb.append(pr.getRequested() + "\n");
			}
			sb.append("--------------- \n");
		}
		return new ResponseMessage(ResponseStatus.OK, sb.toString());
	}

	private ResponseMessage getHistory(String groupID) {
		Group group = groups.get(groupID);
		if (group == null || !group.isOwner(logged)) {
			return new ResponseMessage(ResponseStatus.ERROR, "Invalid group or not the owner");
		}
		StringBuilder sb = new StringBuilder("\n Completed payments of the group " + groupID + ": \n\n");
		for (GroupPayment gp : group.getComplete()) {
			sb.append("--------------- \n");
			for (PaymentRequest pr : gp.getComplete()) {
				sb.append(pr.getId() + " -------- " + pr.getAmount() + " -------- " + pr.getRequested().getId() + " -> "
						+ pr.getRequested().getId() + "\n");
			}
			sb.append("--------------- \n");
		}
		return new ResponseMessage(ResponseStatus.OK, sb.toString());
	}

	private boolean verifySignedTransaction(TransactionDto transaction) throws TrokosException {
		ResponseMessage msg = new ResponseMessage(ResponseStatus.TRANSACTION_REQ, transaction);
		try {
			conn.write(msg);
		} catch (IOException e) {
			throw new TrokosException("Error requesting signature");
		}

		RequestMessage signTransaction = null;

		try {
			signTransaction = (RequestMessage) conn.read();
		} catch (ClassNotFoundException | IOException e) {
			throw new TrokosException("Error recieving signed transaction");
		}

		try {
			byte[] recieved = signTransaction.getSignature();

			Signature signature = Signature.getInstance("MD5withRSA");

			byte[] data = TransactionDto.getBytes(transaction);

			PublicKey key = logged.getKey();
			signature.initVerify(key);

			signature.update(data);

			return signature.verify(recieved);

		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			throw new TrokosException("Error verifying signature");
		}
	}
}