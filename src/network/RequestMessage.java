package network;

public class RequestMessage extends Message {
	
	private static final long serialVersionUID = -4824772652048347630L;

	private RequestTypes type;
	private String[] args;
	private byte[] signature;
	
	public RequestMessage(RequestTypes type, String args[]) {
		super(Message.MessageType.REQUEST);
		this.type = type;
		this.args = args;
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

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public void setArgs(String[] args){
		this.args = args;
	}

	
}
