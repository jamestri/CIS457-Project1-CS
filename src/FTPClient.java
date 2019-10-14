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
        int port1;

        System.out.println("Enter connect to connect to server");

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);


        if (sentence.startsWith("connect")) {
            String serverName = tokens.nextToken(); // pass the connect command
            serverName = tokens.nextToken();
            port1 = Integer.parseInt(tokens.nextToken());
            System.out.println(port1);
            System.out.println("You are connected to " + serverName);

            Socket ControlSocket = new Socket(serverName, port1);

            while (true) {

                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());

                DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));

                sentence = inFromUser.readLine();

                int port = port1;

                if (sentence.equals("list:")) {


                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
                    while (inData.read() != -1) {
                        modifiedSentence = inData.readLine();
                        System.out.println(modifiedSentence);
                    }

                    inData.close();
                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } if (sentence.startsWith("retr: ")) {
                    StringTokenizer tok = new StringTokenizer(sentence);
                    tok.nextToken();
                    String fileName = tok.nextToken();

                    port += 2;

                    outToServer.writeBytes(port + " " + sentence + '\n');
                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

                    if (inData.readLine().equals("550")){
                        System.out.println("Cannot find file");
                    }
                    if (inData.readLine().equals("200 OK")){
                        File file = new File(fileName);
                        OutputStream out = new FileOutputStream(file);
                        out.write(inData.read());
                        out.close();
                    }
                    inData.close();
                    welcomeData.close();
                    dataSocket.close();

                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } if (sentence.startsWith("stor: ")){
                    StringTokenizer tok = new StringTokenizer(sentence);
                    tok.nextToken();
                    String fileName = tok.nextToken();
                    port += 2;

                    outToServer.writeBytes(port + " " + sentence + '\n');
                    ServerSocket socket = new ServerSocket(port);
                    Socket dataSocket = socket.accept();

                    DataOutputStream dataToServer = new DataOutputStream(dataSocket.getOutputStream());

                    //PATH should be directory of client
                    File folder = new File("C:\\Users\\bunny\\Desktop\\folder");
                    String[] files = folder.list();
                    //needs some refining
                    //finding our file in directory and sending it
                    for (String file: files){
                        if (file.equals(fileName)){
                            FileInputStream fis = new FileInputStream("C:\\Users\\bunny\\Desktop\\folder\\"+fileName);
                            //add headers and stuff and codes
                            sendBytes(fis, dataToServer);
                            fis.close();
                        } else {
                            dataToServer.writeBytes("550");
                            dataToServer.writeBytes("\n");
                        }
                    }
                    socket.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } if (sentence.startsWith("close")){

                    port += 2;
                    outToServer.writeBytes(port + " " + sentence + '\n');
                    ControlSocket.close();
                    System.out.println("Connection closed");
                    return;
                }
                //inFromUser.close();
            }
        }


    }

    //might need bigger buffer
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1){
            os.write(buffer, 0, bytes);
        }
    }
}