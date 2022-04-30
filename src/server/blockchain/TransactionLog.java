package server.blockchain;

import java.io.File;
import java.io.FilenameFilter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import exceptions.TrokosException;
import model.Transaction;

public class TransactionLog {

	private static final String ALGORITHM = "SHA-256";
	
	private static TransactionLog singleton = null;
	private static AtomicLong blockCounter;
	private Block block;
	
	// private constructor to ensure there is only one instance
	private TransactionLog() throws TrokosException {
		Block.setBlockSize(5);
		try {
			Block.setMd(MessageDigest.getInstance(ALGORITHM));
		} catch (NoSuchAlgorithmException e) {
			throw new TrokosException("Specified Algorithm(" + ALGORITHM + ") was not found");
		}
		
		byte[] lastHash = verifyBlockchain();
		
		if (lastHash == null) {			
			block = new Block( blockCounter.getAndIncrement());
		} else {
			block = new Block( blockCounter.getAndIncrement(), lastHash);
		}
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
	
	
	public static byte[] verifyBlockchain() throws TrokosException {
		File folder = new File(Block.BLOCK_FOLDER);
		File[] blocks = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(Block.BLOCK_NAME_REGEX);
			}
		});
		
		blockCounter = new AtomicLong(blocks.length + 1);
		
		List<File> sortedblocks = Arrays.asList(blocks);
		sortedblocks.sort( (b1, b2) -> b1.getName().compareTo(b2.getName()));
		
		byte[] lastHash = null;
		for (File b : sortedblocks) {
			//TODO: verify hash and signature update lastHash
			//TODO: throw exception if fails to validate
		}
		
		return lastHash;
	}
}
