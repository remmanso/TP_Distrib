import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;


public class BroadcastProtocol {

	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
			System.out.println("Veuillez rentrer une ou plusieurs adresse IP en arguments");
			return;
		}
		
		HashMap<String, Boolean> context = new HashMap<String, Boolean>();
		for (int i = 0; i < args.length; i++)
			context.put(args[i], true);
		
		HashMap<String, HashMap<String, Boolean>> c_messages_sent = 
				new HashMap<String, HashMap<String,Boolean>>();
		HashMap<String, HashMap<String, Boolean>> c_messages_received = 
				new HashMap<String, HashMap<String,Boolean>>();
		
		Thread t = new Thread(new FaultDetector(args, context, 2009));
		t.start();
		
		Thread c2010 = new Thread(new Listener(2010, c_messages_sent, c_messages_received));
        c2010.start();
        
        String message = "hello";
        Thread b = new Thread(new Broadcast(message, context));
        b.start();
		
		while (true) {
			LinkedList<String> messages_to_deliver = new LinkedList<String>();
			//on regarde quelle machine a acquité les messages envoyé par cette machine
			for (String s : c_messages_sent.keySet()) {
				boolean ok_delivery = true;
				//pour chaque message on parcourt les machines en réseau pour savoir lesquels on répondu
				//si elles 'nont pas répondu on regarde si elles sont en vies.
				for (String s1 : c_messages_sent.get(s).keySet()) {
					if (!c_messages_sent.get(s).get(s1)) {
						if (context.get(s1)) {
							ok_delivery = false;
							break;
						}
					}
						
				}
				if (ok_delivery)
					messages_to_deliver.add(s);
			} 
			for (String s :messages_to_deliver) {
				c_messages_sent.remove(s);
				deliver(s);
			}
			
			messages_to_deliver.clear();
			//on regarde quelle machine a acquité les messages reçus par cette machine
			for (String s : c_messages_received.keySet()) {
				boolean ok_delivery = true;
				//pour chaque message on parcourt les machines en réseau pour savoir lesquels on répondu
				//si elles 'nont pas répondu on regarde si elles sont en vies.
				for (String s1 : c_messages_received.get(s).keySet()) {
					if (!c_messages_received.get(s).get(s1)) {
						if (context.get(s1)) {
							ok_delivery = false;
							break;
						}
					}
						
				}
				if (ok_delivery)
					messages_to_deliver.add(s);
			} 
			for (String s :messages_to_deliver) {
				c_messages_received.remove(s);
				deliver(s);
			}
		}

	}
	
	public static void deliver(String s) {
		
	}

}
