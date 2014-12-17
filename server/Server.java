// import java.io.BufferedReader;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.InputStream;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.PrintWriter;

public class Server {
	
	public static void main(String[] zero) {
		
		ServerSocket socketserver  ;
		Socket socketduserveur ;

		DataOutputStream out;
		DataInputStream in;
		byte ack_packet[] = new byte[1];
		byte down_packet[] = new byte[65000];
				
		try {
			socketserver = new ServerSocket(2009);
			System.out.println("Le serveur est à l'écoute du port "+socketserver.getLocalPort());
			
	    	socketduserveur = socketserver.accept(); 
		    in = new DataInputStream(socketduserveur.getInputStream());
			out = new DataOutputStream(socketduserveur.getOutputStream());
			int b_read = in.read(down_packet);
	        System.out.println(b_read);
	        out.write(ack_packet);
			// (b_read==1)?ack_packet:down_packet 
	        b_read = in.read(down_packet);
	        out.write(down_packet);
	        out.flush();
	        socketduserveur.close();		       
			socketserver.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
