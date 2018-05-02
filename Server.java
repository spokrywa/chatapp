import java.io.*;
import java.util.*;
import java.net.*;

public class Server
{
    static Vector<ClientHandler> ar = new Vector<>();

    static int i = 0;

    public static void main(String[] args) throws IOException
    {
        ServerSocket ss = new ServerSocket(1234);
        Socket s;

        while(true)
        {
            s = ss.accept();

            System.out.println("New client request received : " + s);

            //input output stream
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating new handler for this client...");

            ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);

            Thread t = new Thread(mtch);

            System.out.println("adding this client to active client list");

            ar.add(mtch);

            t.start();

            i++;
        }
    }
}

class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    public String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    //constructor
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos)
    {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run()
    {
        String received;
        while(true)
        {
            try
            {
                //receive string
                received = dis.readUTF();

                System.out.println(received);

                if(received.equals("logout"))
                {
                    this.isloggedin=false;
                    this.s.close();
                    Server.ar.remove(this);
                    break;
                }

                String MsgToSend = received;
                String recipient = null;

                // private message a user with <message>@<user>
                if (received.contains("@"))
                {
                    StringTokenizer st = new StringTokenizer(received, "@");
                    MsgToSend = st.nextToken();
                    recipient = st.nextToken();
                }

                //listing users command with *list
                if (received.startsWith("*"))
                {
                    if(received.contains("list"))
                    {
                        this.dos.writeUTF("List of current users: ");
                        for (ClientHandler mc : Server.ar)
                        {
                            this.dos.writeUTF(mc.name);
                        }
                    }
                }
                
                //change name with &<new_name>
                if (received.startsWith("&"))
                {
                    StringTokenizer nc = new StringTokenizer(received, "&");
                    String temp = nc.nextToken();
                    for (ClientHandler mc : Server.ar)
                    {
                        mc.dos.writeUTF(this.name+" changed name to "+temp);
                    }
                    this.name = temp;
                }

                //broadcast message if no @, &, * delimiters
                if(!received.startsWith("&") && !received.startsWith("*") )
                {
                   for (ClientHandler mc : Server.ar)
                    {
                        if (mc.name.equals(recipient) && mc.isloggedin==true)
                        {
                            mc.dos.writeUTF("<"+this.name+">"+" : "+MsgToSend);
                            break;
                        }
                        else if(received.contains("@") != true)
                        {
                            mc.dos.writeUTF(this.name+" : "+MsgToSend);
                        }
                    } 
                }
            }catch (IOException e ){
                e.printStackTrace();
            }
        }
        try
        {
            this.dis.close();
            this.dos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
