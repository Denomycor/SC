package model;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Transaction implements Serializable {
	
	private static final long serialVersionUID = -220512868026077576L;
	
	private long destId;
	private double ammount;
	
	public Transaction(long destId, double ammount) {
		this.destId = destId;
		this.ammount = ammount;
	}

	public long getDestId() {
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
