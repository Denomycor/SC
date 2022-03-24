package network;

public class RequestMessage extends Message {
	
	private RequestTypes type;
	private String args[];
	
	public RequestMessage(RequestTypes type, String args[]) {
		this.type = type;
		this.args = args;
	}

	public RequestTypes getType() {
		return type;
	}

	public String[] getArgs() {
		return args;
	}
}
