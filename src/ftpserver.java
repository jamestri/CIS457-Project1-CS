import java.io.*; 
import java.net.*;
import java.util.*;
class FTPServer {

	public static void main(String argv[]) throws Exception {
		String fromClient;
		String clientCommand;
		byte[] data;


		ServerSocket welcomeSocket = new ServerSocket(12000);
		String frstln;
		int port;

		while (true) {


			Socket connectionSocket = welcomeSocket.accept();
			ClientHandler handler = new ClientHandler(connectionSocket);
		}

	}
}