package network;

public class ResponseMessage extends Message {

	private ResponseStatus status;
	
	public ResponseMessage(ResponseStatus status) {
		this.status = status;
	}

	public ResponseStatus getStatus() {
		return status;
	}
}
