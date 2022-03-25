package network;

public class ResponseMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8831947998289447696L;
	private ResponseStatus status;
	private String body;
	
	public ResponseMessage(ResponseStatus status) {
		this.status = status;
	}
	
	public ResponseMessage(ResponseStatus status, String msg) {
		this.body = msg;
		this.status = status;
	}

	public ResponseStatus getStatus() {
		return status;
	}
	
	public String getBody() {
		return body;
	}
}
