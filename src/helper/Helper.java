package helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Helper {
    
    public static byte[] StringArrayToBytes(String[] arr) throws IOException{
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        	for(String s : arr) {
        		baos.write(s.getBytes(StandardCharsets.UTF_8));
        	}
        	baos.flush();
        	
        	return baos.toByteArray();        	
        }
    }

    public static void printArray(String[] arr){
        for(String s : arr){
           System.out.println(s);
        }
    }

    public static void printBytes(String[] arr){
        for(String s : arr){
           System.out.println(s.getBytes());
        }
    }
}
