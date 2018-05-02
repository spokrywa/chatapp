import java.io.*;
import java.util.Scanner;
import java.net.*;

public class Client {

    public static void main(String args[]) throws UnknownHostException, IOException
    {
        Scanner scn = new Scanner(System.in);

        System.out.println("Server IP: ");
        InetAddress ip = InetAddress.getByName(scn.next());
        System.out.println("Server port: ");
        final int ServerPort = Integer.parseInt(scn.next());


        Socket s = new Socket(ip, ServerPort);

        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    String msg = scn.nextLine();
                    
                   

                    try
                    {
                        dos.writeUTF(msg);
                    }catch (IOException e ) {
                        e.printStackTrace();
                    }

                    if(msg.startsWith("logout")){
                        System.exit(0);
                    }
                }
            }
        });

        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
}
