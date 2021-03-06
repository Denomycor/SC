package network;

import model.TransactionDto;

public class ResponseMessage extends Message {

	private static final long serialVersionUID = -8831947998289447696L;
	private ResponseStatus status;
	private String body;
	private TransactionDto transaction;

	public ResponseMessage(ResponseStatus status) {
		super(Message.MessageType.RESPONSE);
		this.status = status;
	}

	public ResponseMessage(ResponseStatus status, String msg) {
		super(Message.MessageType.RESPONSE);
		this.body = msg;
		this.status = status;
	}

	public ResponseMessage(ResponseStatus status, TransactionDto transaction) {
		super(Message.MessageType.RESPONSE);
		this.status = status;
		this.transaction = transaction;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public String getBody() {
		return body;
	}

	public TransactionDto getTransaction() {
		return transaction;
	}
}
