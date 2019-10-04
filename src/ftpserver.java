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

			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			fromClient = inFromClient.readLine();

			StringTokenizer tokens = new StringTokenizer(fromClient);
			frstln = tokens.nextToken();
			port = Integer.parseInt(frstln);
			clientCommand = tokens.nextToken();

			if (clientCommand.equals("list:")) {

				Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
				//TODO Fill in the rest of this

				dataSocket.close();
				System.out.println("Data Socket closed");
			}

			//this might be in wrong place in code, dunno
			if (clientCommand.equals("retr:")){
				Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

				//TODO Fill in the rest of this and get file from this

				dataSocket.close();
				System.out.println("Data Socket closed");
			}

			if (clientCommand.equals("stor:")){
				Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

				//TODO Fill in the rest of this and get file from this

				dataSocket.close();
				System.out.println("Data Socket closed");
			}

			//
			if (clientCommand.equals("close")){
				connectionSocket.close();
				break;
			}

		}

	}
}