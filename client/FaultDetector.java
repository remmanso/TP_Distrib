import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;


public class FaultDetector implements Runnable {
	
	private String args[];
	private ConcurrentHashMap<String, Boolean> context;
	private int port;
	
	public FaultDetector(String args[], ConcurrentHashMap<String, Boolean> context, int port) {
		this.args = args;
		this.context = context;
		this.port = port;
	}
	
	@Override
	public void run() {		
		
		TimerPing t_ping;
		Timer timer = new Timer();
		for (int i = 0; i < args.length; i++) {
			t_ping = new TimerPing(args[i], port, context);
			timer.scheduleAtFixedRate(t_ping, 10000, 1000);
		}
		
		ServerSocket socketserver  ;
		Socket socketduserveur ;
				
		try {
			socketserver = new ServerSocket(port);
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
