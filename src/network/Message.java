package network;

import java.io.Serializable;

public abstract class Message implements Serializable {

	private MessageType type;

	public enum MessageType {
		REQUEST, RESPONSE, USERAUTH, TRANSACTION
	}

	public Message(MessageType type){
		this.type = type;
	}

	public MessageType getMessageType(){
		return type;
	}

	private static final long serialVersionUID = -1320019969100589827L;
}
