package server.blockchain;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

import exceptions.TrokosException;
import model.Transaction;

public class Block {
	
	protected static final String BLOCK_NAME_REGEX = "^block_([0-9])+.blk$";
	protected static final String BLOCK_FOLDER = "./blockchain/";
	private static int blockSize;
	private static MessageDigest md;
	private long id;
	private byte[] lastHash;
	private List<Transaction> content = new ArrayList<Transaction>();
	private byte[] signature;
	 
	
	public Block(long id) {
		lastHash = new byte[32];
		this.id = id;
	}

	public Block(long id, byte[] lastHash) {
		this.lastHash = lastHash;
		this.id = id;
	}

	public boolean isFull() {
		return content.size() == blockSize;
	}	

	public void add(Transaction transaction) {
		content.add(transaction);
	}
	
	public byte[] commit() throws TrokosException {
		byte[][] contentBytes = new byte[5][];
		int contentByteSize = 0;
		int index = 0;
		for (Transaction t : content) {
			contentBytes[index] = t.getBytes();
			contentByteSize += t.getBytes().length;
			index++;
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(lastHash.length + Long.BYTES * 2 + contentByteSize);
		buffer.put(lastHash);
		buffer.putLong(id);
		buffer.putLong((long) content.size());
		for (byte[] c : contentBytes ) {
			buffer.put(c);
		}
		
		byte[] sign;

		try{
			KeyStore kstore;
			FileInputStream kfile = new FileInputStream(System.getProperty("javax.net.ssl.keyStore"));
			kstore = KeyStore.getInstance("PKCS12");
			kstore.load(kfile, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
			PrivateKey priv = (PrivateKey) kstore.getKey("myserver",System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());

			byte[] data =  buffer.array();

			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(priv);
			signature.update(data);

			sign = signature.sign();
		}catch(Exception e){
			throw new TrokosException("Error signing the object");
		}

		//TODO: sign lastHash + id + size + content
		//TODO: write to file lastHash + id + size + content + signature
		
		return getHash();
	}
	
	public byte[] getHash() {
		byte[][] contentBytes = new byte[5][];
		int contentByteSize = 0;
		int index = 0;
		for (Transaction t : content) {
			contentBytes[index] = t.getBytes();
			contentByteSize += t.getBytes().length;
			index++;
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(lastHash.length + Long.BYTES * 2 + contentByteSize + signature.length);
		buffer.put(lastHash);
		buffer.putLong(id);
		buffer.putLong((long) content.size());
		for (byte[] c : contentBytes ) {
			buffer.put(c);
		}
		buffer.put(signature);
		
		return md.digest(buffer.array());
	}
	
	// Setters
	public static void setBlockSize(int blockSize) {
		Block.blockSize = blockSize;
	}

	public static void setMd(MessageDigest md) {
		Block.md = md;
	}
}
