package client;

import java.io.IOException;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import exceptions.TrokosException;
import network.AuthMessage;
import network.Connection;
import network.Message;

public class UserAuth {
    
    private ClientConnectionProperties connProps;
    private Connection connection;

    public UserAuth(ClientConnectionProperties connProps, Connection connection){
        this.connProps = connProps;
        this.connection = connection;
    }

    private void sendMessage(Message msg) throws TrokosException{
        try {
			connection.write(msg);
		} catch (IOException e) {
			throw new TrokosException("Failed sending a message");
		}
    }

    private AuthMessage readMessage() throws TrokosException{
        try {
			return (AuthMessage) connection.read();
		} catch (Exception e) {
			throw new TrokosException("Failed sending a message");
		}
    }

    public Boolean checkAuthentication(String userID) throws TrokosException, Exception{
        //send userId
        AuthMessage msg = new AuthMessage();
        msg.userId = userID;
        sendMessage(msg);

        //receive res
        msg = readMessage();

        PrivateKey priv = connProps.getPrivateKey();
        PublicKey pub = connProps.getPublicKey();
        Cipher c = Cipher.getInstance("RSA");
        
        c.init(Cipher.ENCRYPT_MODE, priv);
        msg.signature = c.doFinal(msg.nonce.getBytes());
        msg.userId = "";

        if(msg.flag){
            //User exists
            msg.nonce = "";
            msg.pub = null;
            sendMessage(msg); 
        }else{
            //User doesnt exist
            msg.pub = pub;
            sendMessage(msg);
        }

        
        msg = readMessage();

        //is authenticated
        return msg.flag;
    }

}
