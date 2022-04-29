package network;

import java.security.cert.Certificate;

public class AuthMessage extends Message{
    
	
	 public boolean flag;
	 public String nonce;
	 public byte[] signature;
	 public Certificate pub;
	 public String userId;
    //TODO: serializationuid

    public AuthMessage(){
        super(Message.MessageType.USERAUTH);
    }

}
