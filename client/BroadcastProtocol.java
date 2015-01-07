import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;


public class BroadcastProtocol {

	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
			System.out.println("Veuillez rentrer une ou plusieurs adresse IP en arguments");
			return;
		}
		
		HashMap<String, Boolean> context = new HashMap<String, Boolean>();
		context.put("LocalHost", true);
		for (int i = 0; i < args.length; i++)
			context.put(args[i], true);
		
		Thread t = new Thread(new FaultDetector(args, context, 2009));
                t.start();
                
                Thread c2010 = new Thread(new Listener(2010));
                c2010.start();
                
                String message = "hello";
                Thread b = new Thread(new Broadcast(message, context));
                b.start();
                
	}

}
