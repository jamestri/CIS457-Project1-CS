import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;


public class ClientHandler extends Thread{
    Socket socket;

    public ClientHandler(Socket connection) throws Exception{
        this.socket = socket;
    }


    @Override
    public void run() {
        try{
            processRequest();
        } catch (Exception e){
            System.out.println(e);
        }

    }

    private void processRequest() throws Exception{
        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String fromClient = inFromClient.readLine();

        StringTokenizer tokens = new StringTokenizer(fromClient);
        String frstln = tokens.nextToken();
        int port = Integer.parseInt(frstln);
        String clientCommand = tokens.nextToken();
        if (clientCommand.equals("list:")) {

            Socket dataSocket = new Socket(socket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            //For when we need to use on a machine, set PATH to directory of server
            File folder = new File("PATH");
            String[] files = folder.list();

            for (String file : files){
                dataOutToClient.writeBytes(file);
            }
            dataOutToClient.writeBytes("\n");

            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        }


        if (clientCommand.startsWith("retr:")){
            Socket dataSocket = new Socket(socket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            String fileName = clientCommand.substring(6);//starting after the space

            File folder = new File("PATH");
            String[] files = folder.list();

            //finding our file in directory and sending it
            for (String file: files){
                if (file.equals(fileName)){
                    FileInputStream fis = new FileInputStream(fileName);
                    //add headers and stuff and codes
                    sendBytes(fis, dataOutToClient);
                } else {
                    dataOutToClient.writeBytes("550");
                }
            }
            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        }

        if (clientCommand.startsWith("stor:")){
            Socket dataSocket = new Socket(socket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            String fileName = clientCommand.substring(6);

            //TODO get bytes, and turn into file somehow

            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        }


        if (clientCommand.equals("close")){
            socket.close();
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

