package network;

import java.security.cert.Certificate;

public class AuthMessage extends Message{
    
	
	private static final long serialVersionUID = -6354530460404215477L;
	private boolean flag;
	private String nonce;
	private byte[] signature;
	private Certificate pub;
	private String userId;

    public AuthMessage(){
        super(Message.MessageType.USERAUTH);
    }

	public boolean isFlag() {
		return flag;
	}

	public String getNonce() {
		return nonce;
	}

	public byte[] getSignature() {
		return signature;
	}

	public Certificate getPub() {
		return pub;
	}

	public String getUserId() {
		return userId;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public void setPub(Certificate pub) {
		this.pub = pub;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
