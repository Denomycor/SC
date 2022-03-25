package network;

public class RequestMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4824772652048347630L;
	private RequestTypes type;
	
	public RequestMessage(RequestTypes type, String args[]) {
		super(type.toString(), args);
		this.type = type;
	}

	public RequestTypes getType() {
		return type;
	}

	public String[] getArgs() {
		return super.body;
	}
}
