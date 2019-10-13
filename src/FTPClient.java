import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient {

    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;
        boolean isOpen = true;
        int number = 1;
        boolean notEnd = true;
        String statusCode;
        boolean clientgo = true;
        int port1;


        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);


        if (sentence.startsWith("connect")) {
            String serverName = tokens.nextToken(); // pass the connect command
            serverName = tokens.nextToken();
            port1 = Integer.parseInt(tokens.nextToken());
            System.out.println("You are connected to " + serverName);

            Socket ControlSocket = new Socket(serverName, port1);

            while (isOpen && clientgo) {

                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());

                DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));

                sentence = inFromUser.readLine();

                int port = port1;

                if (sentence.equals("list:")) {

                    port = port + 2;
                    outToServer.writeBytes(sentence + " " + port + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();

                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
                        System.out.println(modifiedSentence);
                        if (modifiedSentence.equals(-1)){
                            notEnd = false;
                        }
                    }

                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } if (sentence.startsWith("retr: ")) {
                    String fileName = sentence.substring(6);

                    port += 2;
                    outToServer.writeBytes(sentence + " " + port + '\n');


                } if (sentence.startsWith("stor: ")){
                    String fileName = sentence.substring(6);

                    port += 2;
                    outToServer.writeBytes(sentence + " " + port + '\n');

                } if (sentence.startsWith("close")){

                    port += 2;
                    outToServer.writeBytes(sentence + " " + port + '\n');
                    ControlSocket.close();
                    return;
                }
            }
        }

        inFromUser.close();
    }
}