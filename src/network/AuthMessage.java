package network;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class AuthMessage extends Message {

	private static final long serialVersionUID = -6354530460404215477L;
	private boolean flag;
	private String nonce;
	private byte[] signedObject;
	private byte[] certificate;
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

	public Certificate getCertificate() throws CertificateException {

		return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certificate));
	}

	public void setCertificate(Certificate certificate) throws CertificateEncodingException {
		this.certificate = certificate.getEncoded();
	}

}
