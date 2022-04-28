package network;

import java.security.PublicKey;

public class AuthMessage extends Message{
    
    //TODO: serializationuid


    public boolean flag;
    public String nonce;
    public byte[] signature;
    public PublicKey pub;
    public String userId;

}
