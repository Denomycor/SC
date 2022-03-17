package server;

public class Main {
	
	public static void main(String[] args){
		int port = 45678;
        if(args.length > 1){
            try {
            	port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
				System.out.println("Cannot Parse given port continued with default");
			}
        }
        
        try ( Server server = new Server(port) ) {
        	server.mainLoop();
        	
        } catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
    }

}
