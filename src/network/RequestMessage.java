package network;

import model.Transaction;

public class RequestMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4824772652048347630L;
	private RequestTypes type;
	private String[] args;
	
	private byte[] signature;
	
	public RequestMessage(RequestTypes type, String args[]) {
		super(MessageType.REQUEST);
		this.type = type;
		this.args = args;
	}
	
	public RequestMessage(RequestTypes type, byte[] signature) {
		super(MessageType.REQUEST);
		this.type = type;
		this.signature = signature;
	}

	public RequestTypes getType() {
		return type;
	}

	public String[] getArgs() {
		return args;
	}

	public byte[] getSignature() {
		return signature;
	}
}
