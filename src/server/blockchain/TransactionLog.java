package server.blockchain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicLong;

import exceptions.TrokosException;

public class TransactionLog {

	private static final String ALGORITHM = "SHA-256";
	
	private static TransactionLog singleton = null;
	private static AtomicLong blockCounter = new AtomicLong(1);
	private Block block;
	
	// private constructor to ensure there is only one instance
	private TransactionLog() throws TrokosException {
		Block.setBlockSize(5);
		try {
			Block.setMd(MessageDigest.getInstance(ALGORITHM));
		} catch (NoSuchAlgorithmException e) {
			throw new TrokosException("Specified Algorithm(" + ALGORITHM + ") was not found");
		}
		block = new Block( blockCounter.getAndIncrement());
	}
	
	public static TransactionLog getTransactionLog() throws TrokosException {
		if (singleton == null) {
			singleton = new TransactionLog(); 
		}
		return singleton;
	}
	
	public void addTransaction(Transaction transaction) {
		block.add(transaction);
		if (block.isFull()) {
			byte[] blockHash = block.commit();
			block = new Block( blockCounter.getAndIncrement(), blockHash);
		}
	}
}
