import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class BroadcastProtocol {

	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
			System.out.println("Veuillez rentrer une ou plusieurs adresse IP en arguments");
			return;
		}
		
		ConcurrentHashMap<String, Boolean> context = new ConcurrentHashMap<String, Boolean>();
		for (int i = 0; i < args.length; i++)
			context.put(args[i], true);
		
		ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = 
				new ConcurrentHashMap<String, ConcurrentHashMap<String,Boolean>>();
		ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = 
				new ConcurrentHashMap<String, ConcurrentHashMap<String,Boolean>>();
		
		ConcurrentHashMap<String, String> messages_received = 
				new ConcurrentHashMap<String, String>();
		
		ConcurrentHashMap<String, String> messages_sent = 
				new ConcurrentHashMap<String, String>();
		
		Thread t = new Thread(new FaultDetector(args, context, 2009));
		t.start();
		
		Thread c2010 = new Thread(new Listener(2010, c_messages_sent, c_messages_received,
				messages_received, messages_sent, context));
        c2010.start();
        
        String message = "hello";
        Thread b = new Thread(new Broadcast(message, context, c_messages_sent, messages_sent));
        b.start();
		
		while (true) {
			LinkedList<String> messages_to_deliver = new LinkedList<String>();
			//on regarde quelle machine a acquit�� les messages envoy�� par cette machine
			for (String s : c_messages_sent.keySet()) {
				boolean ok_delivery = true;
				//pour chaque message on parcourt les machines en r��seau pour savoir lesquelles ont r��pondu
				//si elles 'nont pas r��pondu on regarde si elles sont en vies.
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
				deliver(messages_sent.get(s));
				messages_sent.remove(s);
			}
			
			messages_to_deliver.clear();
			//on regarde quelle machine a acquit�� les messages re��us par cette machine
			for (String s : c_messages_received.keySet()) {
				boolean ok_delivery = true;
				//pour chaque message on parcourt les machines en r��seau pour savoir lesquelles ont r��pondu
				//si elles 'nont pas r��pondu on regarde si elles sont en vies.
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
				deliver(messages_received.get(s));
				messages_received.remove(s);
			}
		}

	}
	
	public static void deliver(String s) {
		
	}

}
