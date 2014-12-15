import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class PingClient {
    
    public static void main(String[] args) {
        
        
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        System.out.println(args[0]);

        try {
            
            if (args.length == 0) {
                System.out.println("veuillez insérer un argument");
                return;
            }

            socket = new Socket(args[0],2009);   
            System.out.println("Demande de connexion");
                
            in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
            String message_distant = in.readLine();
            System.out.println(message_distant);
            
            socket.close();
               
        }catch (UnknownHostException e) {
            
            e.printStackTrace();
        }catch (IOException e) {
            
            e.printStackTrace();
        }
    }

}
