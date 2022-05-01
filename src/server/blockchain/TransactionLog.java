package server.blockchain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import exceptions.TrokosException;

public class TransactionLog {

	private static final String ALGORITHM = "SHA-256";
	private static final int BLOCK_SIZE = 5;

	private static AtomicLong blockCounter;
	
	List<Transaction> previousTransactions;
	private Block block;
	

	public TransactionLog() throws TrokosException {
		Block.setBlockSize(BLOCK_SIZE);
		try {
			Block.setMd(MessageDigest.getInstance(ALGORITHM));
		} catch (NoSuchAlgorithmException e) {
			throw new TrokosException("Specified Algorithm(" + ALGORITHM + ") was not found");
		}

		byte[] lastHash = verifyBlockchain();

		block = new Block(blockCounter.getAndIncrement(), lastHash);
	}

	public synchronized void addTransaction(Transaction transaction) throws TrokosException {
		block.add(transaction);
		if (block.isFull()) {
			byte[] blockHash = block.commit();
			block = new Block(blockCounter.getAndIncrement(), blockHash);
		}
	}

	private byte[] verifyBlockchain() throws TrokosException {
		File folder = new File(Block.BLOCK_FOLDER);
		if (!folder.exists()) {
			folder.mkdir();
		}
			
		File[] blocks = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(Block.BLOCK_NAME_REGEX);
			}
		});

		blockCounter = new AtomicLong(blocks.length + 1);

		List<File> sortedblocks = Arrays.asList(blocks);
		sortedblocks.sort((b1, b2) -> Block.getIdFromFileName(b1.getName()).compareTo(Block.getIdFromFileName(b2.getName())));

		byte[] lastHash = new byte[32];
		
		previousTransactions = new ArrayList<>(BLOCK_SIZE * sortedblocks.size());
		
		for (File b : sortedblocks) {
			try (ObjectInputStream oi = new ObjectInputStream(new FileInputStream(b))) {
				Block block = (Block) oi.readObject();
				if (!Arrays.equals(block.getLastHash(), lastHash)) {
					throw new TrokosException("Blocks are corrupted");
				}
				lastHash = block.generateHash();

				// TODO: verify signature
				
				previousTransactions.addAll(block.getContent());
				
			} catch (IOException | ClassNotFoundException e) {
				throw new TrokosException("Error loading blockchain");
			} 
		}

		return lastHash;
	}

	public List<Transaction> getPreviousTransactions() {
		return previousTransactions;
	}
}
