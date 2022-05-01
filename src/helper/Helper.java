package helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Helper {
    
    public static byte[] StringArrayToBytes(String[] arr) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(String s : arr){
            baos.write(s.getBytes());
        }
        //No need to close
        return baos.toByteArray();
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
