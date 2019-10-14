import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;


public class ClientHandler extends Thread{
    Socket socket;

    public ClientHandler(Socket connection) throws Exception{
        super();
        this.socket = connection;
    }


    @Override
    public void run() {
        try{
            while(socket.isConnected()){
                processRequest();
            }
        } catch (Exception e){
            System.out.println("Client Disconnected");
        }

    }

    private void processRequest() throws Exception{
        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Client connected " + socket.getInetAddress());
        String fromClient = inFromClient.readLine();

        //may need to change where it get the port number
        StringTokenizer tokens = new StringTokenizer(fromClient);
        String frstln = tokens.nextToken();
        int port = Integer.parseInt(frstln);
        String clientCommand = tokens.nextToken();
        System.out.println(clientCommand + socket.getInetAddress());
        if (clientCommand.equals("list:")) {

            Socket dataSocket = new Socket(socket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            //For when we need to use on a machine, set PATH to directory of server
            File folder = new File("C:\\Users\\bunny\\IdeaProjects\\CIS457Proj1");
            String[] files = folder.list();

            for (String file : files){
                dataOutToClient.writeBytes(file);
                dataOutToClient.writeBytes(" ");
            }

            dataOutToClient.writeBytes("\n");

            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        }


        if (clientCommand.startsWith("retr:")){
            Socket dataSocket = new Socket(socket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            String fileName = tokens.nextToken();//starting after the space
            File folder = new File("C:\\Users\\bunny\\Desktop\\folder");
            String[] files = folder.list();

            //finding our file in directory and sending it
            for (String file: files){
                if (file.equals(fileName)){
                    dataOutToClient.writeBytes("200 OK");
                    FileInputStream fis = new FileInputStream("C:\\Users\\bunny\\Desktop\\folder\\" + fileName);
                    sendBytes(fis, dataOutToClient);
                    fis.close();
                } else {
                    dataOutToClient.writeBytes("550");
                    dataOutToClient.writeBytes("\n");
                }
            }
            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        }

        if (clientCommand.startsWith("stor:")){
            Socket dataSocket = new Socket(socket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            //should turn into a file need to add codes and stuff
            String fileName = tokens.nextToken();
            File file = new File(fileName);
            BufferedReader dataIn = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            
            OutputStream byteWriter = new FileOutputStream(file);
            byteWriter.write(dataIn.read());
            byteWriter.close();

            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        }


        if (clientCommand.equals("close")){
            socket.close();
            System.out.println("Client has disconnected");
            return;
        }

    }


    //might need bigger buffer depending on file
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1){
            os.write(buffer, 0, bytes);
        }
    }
}

