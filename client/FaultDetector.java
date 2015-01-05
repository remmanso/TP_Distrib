import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;


public class FaultDetector {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("veuillez insérer un argument 4 adresses");
			return;
		}
		
		HashMap<String, Boolean> context = new HashMap<String, Boolean>();
		context.put("LocalHost", true);
		context.put(args[0], true);
		
		TimerPing t_ping;
		t_ping = new TimerPing(args[0], 2009, context);
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
