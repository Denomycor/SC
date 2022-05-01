package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import exceptions.TrokosException;

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
	
	public static byte[] getBytes(Transaction t) throws TrokosException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(t);
			oos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new TrokosException("Failed getting bytes from transaction");
		}		
	}
}
