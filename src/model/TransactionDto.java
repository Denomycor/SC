package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import exceptions.TrokosException;
import server.blockchain.Transaction;

public class TransactionDto implements Serializable {

	private static final long serialVersionUID = -220512868026077576L;

	private String destId;
	private double amount;

	public TransactionDto(Transaction transaction) {
		destId = transaction.getDestId();
		amount = transaction.getAmount();
	}

	public String getDestId() {
		return destId;
	}

	public double getAmount() {
		return amount;
	}

	public static byte[] getBytes(TransactionDto t) throws TrokosException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(t);
			oos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new TrokosException("Failed getting bytes from transaction");
		}
	}
}
