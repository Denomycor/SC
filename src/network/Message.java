package network;

import java.io.Serializable;

public abstract class Message implements Serializable {

	private static final long serialVersionUID = -1320019969100589827L;
	
	protected final String header;
	
	protected final String body[];
	
	public Message(String header, String body[]) {
		this.header = header;
		this.body = body;
	}
	
}
