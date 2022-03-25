package network;

public class ResponseMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8831947998289447696L;
	private ResponseStatus status;
	
	public ResponseMessage(ResponseStatus status) {
		super(status.toString(), new String[] {});
		this.status = status;
	}

	public ResponseStatus getStatus() {
		return status;
	}
}
