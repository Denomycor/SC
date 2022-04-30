package network;

import server.blockchain.Transaction;

public class RequestMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4824772652048347630L;
	private RequestTypes type;
	private String[] args;
	
	private Transaction transaction;
	private byte[] signature;
	
	public RequestMessage(RequestTypes type, String args[]) {
		super(MessageType.REQUEST);
		this.type = type;
		this.args = args;
	}
	
	public RequestMessage(RequestTypes type, Transaction transaction, byte[] signature) {
		super(MessageType.REQUEST);
		this.type = type;
		this.transaction = transaction;
		this.signature = signature;
	}

	public RequestTypes getType() {
		return type;
	}

	public String[] getArgs() {
		return args;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public byte[] getSignature() {
		return signature;
	}
}
