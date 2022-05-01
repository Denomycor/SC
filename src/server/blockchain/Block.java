package server.blockchain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.TrokosException;
import model.Transaction;

public class Block {
	
	protected static final String BLOCK_NAME_START = "block_";
	protected static final String BLOCK_NAME_EXT = ".blk";
	protected static final String BLOCK_NAME_REGEX = "^" + BLOCK_NAME_START + "([0-9]+)" + BLOCK_NAME_EXT + "$";
	protected static final String BLOCK_FOLDER = "./blockchain/";
	private static int blockSize;
	private static MessageDigest md;
	private long id;
	private byte[] lastHash;
	private List<Transaction> content = new ArrayList<Transaction>();
	private byte[] signature;

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
		
		byte[] toSign = null; 
		
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.write(lastHash);
			oos.writeLong(id);
			oos.writeLong((long) content.size());
			oos.writeObject(content);
			oos.flush();
			toSign = bos.toByteArray();
		} catch (IOException e) {
			throw new TrokosException("Failed serializing Block");
		}
		
		try{
			KeyStore kstore;
			FileInputStream kfile = new FileInputStream(System.getProperty("javax.net.ssl.keyStore"));
			kstore = KeyStore.getInstance("PKCS12");
			kstore.load(kfile, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
			PrivateKey priv = (PrivateKey) kstore.getKey("myserver",System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());

			Signature signer = Signature.getInstance("MD5withRSA");
			signer.initSign(priv);
			signer.update(toSign);

			signature = signer.sign();
		}catch(Exception e){
			throw new TrokosException("Error signing the object");
		}
		
		byte[] finalData = null;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.write(toSign);
			oos.write(this.signature);
			oos.flush();
			finalData = bos.toByteArray();
		} catch (IOException e) {
			throw new TrokosException("Failed serializing Block");
		}

		
		try (FileOutputStream f = new FileOutputStream(new File(BLOCK_FOLDER + BLOCK_NAME_START + id + BLOCK_NAME_EXT))) {
			f.write(finalData);
		} catch (IOException e) {
			throw new TrokosException("Failed writing Block to file");
		}
		
		return getHash(finalData);
	}
	
	public byte[] getHash() throws TrokosException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.write(lastHash);
			oos.writeLong(id);
			oos.writeLong((long) content.size());
			oos.writeObject(content);
			oos.write(signature);
			oos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new TrokosException("Failed serializing Block");
		}
	}
	
	public byte[] getHash(byte[] data) throws TrokosException {
		return md.digest(data);
	}
	
	public static Long getIdFromFileName(String name) {
		Pattern pat = Pattern.compile(BLOCK_NAME_REGEX);
    	Matcher matcher = pat.matcher(name);
		matcher.find();
    	return Long.parseLong(matcher.group(1));
	}
	
	// Setters
	public static void setBlockSize(int blockSize) {
		Block.blockSize = blockSize;
	}

	public static void setMd(MessageDigest md) {
		Block.md = md;
	}
}
