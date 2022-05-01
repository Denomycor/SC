package network;

import java.security.cert.Certificate;

public class AuthMessage extends Message {

	private static final long serialVersionUID = -6354530460404215477L;
	private boolean flag;
	private String nonce;
	private byte[] signedObject;
	private Certificate certificate;
	private String userId;

	public AuthMessage() {
		super(Message.MessageType.USERAUTH);
	}

	public boolean isFlag() {
		return flag;
	}

	public String getNonce() {
		return nonce;
	}

	public byte[] getSignedObject() {
		return signedObject;
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

	public void setSignedObject(byte[] signedObject) {
		this.signedObject = signedObject;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

}
