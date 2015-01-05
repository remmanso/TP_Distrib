import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;


public class FaultDetector {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("veuillez insérer un argument 4 adresses");
			return;
		}
		
		HashMap<String, Boolean> context = new HashMap<String, Boolean>();
		context.put("LocalHost", true);
		for (int i = 0; i < args.length; i++)
			context.put(args[i], true);
		
		TimerPing t_ping;
		Timer timer = new Timer();
		for (int i = 0; i < args.length; i++) {
			t_ping = new TimerPing(args[i], 2009, context);
			timer.scheduleAtFixedRate(t_ping, 10000, 1000);
		}
		
		ServerSocket socketserver  ;
		Socket socketduserveur ;
				
		try {
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
