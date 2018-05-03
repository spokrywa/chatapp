import java.io.*;
import java.util.*;
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


	System.out.println("\nHello.! Welcome to the chatroom.");
	System.out.println("Here's how to navigate it:");
	System.out.println("1. Simply type the message to send broadcast to all active clients");
	System.out.println("2. Type '<yourmessage>@username' without quotes to send message to desired client");
	System.out.println("3. To change your username, type '&<desired username>' without quotes");
	System.out.println("4. Type '*list' without quotes to see list of active clients");
	System.out.println("5. Type 'logout' without quotes to logoff from server");

        
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
			//e.printStackTrace();
			System.out.println("Server has closed the connection:" + e);   
			break;                     

                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    
    }

}
