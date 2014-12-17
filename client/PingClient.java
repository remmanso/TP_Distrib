import java.*;
import java.util.Timer; 

public class PingClient {
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("veuillez insérer un argument [adresse] puis:\n1 pour ping, \n2 pour Netperf");
			return;
		}

		if (args[1].equals("1")){
			TimerPing t_ping = new TimerPing(args[0]);
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(t_ping, 0, 1000);
		}
		else if (args[1].equals("2")){
			Netperf t_netperf = new Netperf(args[0]);
			t_netperf.run();
			// Timer timer = new Timer();
			// timer.scheduleAtFixedRate(t_netperf, 0, 1000);
		}
	}
}