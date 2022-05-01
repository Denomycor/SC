package server;

public class Main {

	public static void main(String[] args) {

		int port = 45678;
		int i = 0;

		if (args.length == 4) {
			try {
				port = Integer.parseInt(args[0]);
				i++;
			} catch (NumberFormatException e) {
				System.out.println("Cannot Parse given port continued with default");
			}

		} else if (args.length != 3) {
			System.out.println("Incorrect number of arguments. usage:");
			System.out.println("TrokosServer <port> <password-cifra> <keystore> <password-keystore>");
			return;
		}

		System.setProperty("javax.net.ssl.keyStore", args[i + 1]);
		System.setProperty("javax.net.ssl.keyStorePassword", args[i + 2]);

		try (Server server = new Server(port, args[i])) {
			server.mainLoop();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

}
