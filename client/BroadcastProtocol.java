import java.util.HashMap;
import java.util.LinkedList;


public class BroadcastProtocol {

	public static void main(String[] args) {
		
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
		
		while (true) {
			LinkedList<String> messages_to_deliver = new LinkedList<String>();
			int nb_machine_alive = 0;
			for (String s : context.keySet()) {
				if (context.get(s))
					nb_machine_alive ++;
			}
			for (String s : c_messages_sent.keySet()) {
				int nb_machine_response = 0;
				for (String s1 : c_messages_sent.get(s).keySet()) {
					if (c_messages_sent.get(s).get(s1))
						nb_machine_response ++;
				}
				if (nb_machine_response == nb_machine_alive)
					messages_to_deliver.add(s);
			}
		}

	}

}
