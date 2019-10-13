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

            while (isOpen && clientgo) {

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
                    //System.out.println(inData.available());
                    while (inData.read() != -1) {
                        modifiedSentence = inData.readLine();
                        System.out.println(modifiedSentence);
                    }


                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } if (sentence.startsWith("retr: ")) {
                    String fileName = sentence.substring(6);

                    port += 2;

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    outToServer.writeBytes(port + "\n" + sentence + '\n');

                    if (inData.readUTF().equals("550")){
                        System.out.println("Cannot find file");
                    } else {
                        File file = new File(fileName);
                        OutputStream out = new FileOutputStream(file);
                        byte[] bytes = inData.readAllBytes();
                        out.write(bytes);
                        out.close();
                    }
                    welcomeData.close();
                    dataSocket.close();

                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } if (sentence.startsWith("stor: ")){
                    String fileName = sentence.substring(6);

                    port += 2;

                    ServerSocket socket = new ServerSocket(port);
                    Socket dataSocket = socket.accept();

                    DataOutputStream dataToServer = new DataOutputStream(dataSocket.getOutputStream());

                    outToServer.writeBytes(port + "\n" + sentence + '\n');

                    //PATH should be directory of client
                    File folder = new File("C:\\Users\\bunny\\IdeaProjects\\CIS457Proj1");
                    String[] files = folder.list();
                    //needs some refining
                    //finding our file in directory and sending it
                    for (String file: files){
                        if (file.equals(fileName)){
                            FileInputStream fis = new FileInputStream(fileName);
                            //add headers and stuff and codes
                            sendBytes(fis, dataToServer);
                        } else {
                            dataToServer.writeBytes("550");
                        }
                    }
                    socket.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } if (sentence.startsWith("close")){

                    port += 2;
                    outToServer.writeBytes(port + "\n" + sentence + '\n');
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