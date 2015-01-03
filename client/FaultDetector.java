import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;


public class FaultDetector {

	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println("veuillez insérer un argument 4 adresses");
			return;
		}
		TimerPing t_ping = new TimerPing(args[0], 2009);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(t_ping, 10000, 1000);
		/*t_ping = new TimerPing(args[1], 2009);
		timer.scheduleAtFixedRate(t_ping, 10000, 1000);
		/*t_ping = new TimerPing(args[2]);
		timer.scheduleAtFixedRate(t_ping, 10000, 1000);
		t_ping = new TimerPing(args[3]);
		timer.scheduleAtFixedRate(t_ping, 10000, 1000);*/
		
		ServerSocket socketserver  ;
		Socket socketduserveur ;
				
		try {
			/*if (args[4].equals("1"))
				socketserver = new ServerSocket(2009);
			else 
				socketserver = new ServerSocket(20008);*/
			socketserver = new ServerSocket(2009);
			System.out.println("Le serveur est à l'écoute du port "+socketserver.getLocalPort());
			while (true){
		    	socketduserveur = socketserver.accept(); 
			    
				Thread t = new Thread(new ServerManager(socketduserveur));
				t.start();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

}
