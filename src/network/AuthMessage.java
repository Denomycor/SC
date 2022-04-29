package network;

import java.security.PublicKey;

public class AuthMessage extends Message{
    
    //TODO: serializationuid

    public AuthMessage(){
        super(Message.MessageType.USERAUTH);
    }

    public boolean flag;
    public String nonce;
    public byte[] signature;
    public PublicKey pub;
    public String userId;

}
