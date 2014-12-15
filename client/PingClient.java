import java.*;
import java.util.Timer; 

public class PingClient {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("veuillez insérer un argument");
            return;
        }

        TimerPing t_ping = new TimerPing(args[0]);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(t_ping, 0, 1000);
            
        
    }

}