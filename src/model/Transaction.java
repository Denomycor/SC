package model;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Transaction implements Serializable {
	
	private static final long serialVersionUID = -220512868026077576L;
	
	private String destId;
	private double ammount;
	
	public Transaction(String destId, double ammount) {
		this.destId = destId;
		this.ammount = ammount;
	}

	public String getDestId() {
		return destId;
	}

	public double getAmmount() {
		return ammount;
	}
	
	public byte[] getBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES + Double.BYTES);
		buffer.putLong(destId);
		buffer.putDouble(ammount);
		return buffer.array();
	}
}
