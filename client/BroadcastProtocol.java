import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BroadcastProtocol {

	public static void main(String[] args) throws IOException,
			InterruptedException {

		if (args.length == 0) {
			System.out.println("Veuillez rentrer une ou plusieurs adresse IP en arguments");
			return;
		}
		
		if (!args[args.length - 1].equals("1") && !args[args.length - 1].equals("0")) {
			System.out.println("Veuillez insérer 1 pour calculer le débit 0 sinon en fin d'argument");
		}
		
		boolean debit = args[args.length - 1].equals("1");
		
		DeliveredCounter counter_debit = new DeliveredCounter();
		
		ConcurrentHashMap<String, Boolean> context = new ConcurrentHashMap<String, Boolean>();
		ConcurrentHashMap<String, Boolean> cont_connected = new ConcurrentHashMap<String, Boolean>();
		String[] addresses = new String[args.length - 1];
		int i = 0;
		for (String arg : args) {
			if (i == args.length - 1)
				continue;
			addresses[i] = InetAddress.getByName(arg).getHostAddress();
			context.put(addresses[i], false);
			cont_connected.put(addresses[i], false);
			i++;
		}

		ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
		ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();

		ConcurrentHashMap<String, String> messages_received = new ConcurrentHashMap<String, String>();

		ConcurrentHashMap<String, String> messages_sent = new ConcurrentHashMap<String, String>();

		Thread t = new Thread(new FaultDetector(addresses, context, 2009));
		t.start();

		Thread c2010 = new Thread(new Listener(2010, c_messages_sent,
				c_messages_received, messages_received, messages_sent, context,
				cont_connected));

		Thread b = new Thread(new MessageManager(context, c_messages_sent,
				messages_sent, counter_debit, debit));

		c2010.start();
		while (context.contains(false)) {
			Thread.sleep(10);
		}
		new Thread(new Broadcast("Connected", context)).start();
		while (cont_connected.contains(false)) {
			Thread.sleep(100);
			new Thread(new Broadcast("Connected", context)).start();
			if (context.contains(false)) {
				for (String s : context.keySet()) {
					if (context.get(s) == false) {
						cont_connected.remove(s);
					}
				}
			}
		}
		System.out.println("Connected");
		b.start();
		if (debit) {
			new Thread(new CalculDebitMessage(counter_debit, 10000)).start();
		}

		while (true) {
			checkDeliver(c_messages_sent, messages_sent, context, true, counter_debit, debit);
			checkDeliver(c_messages_received, messages_received, context, false, counter_debit, debit);
		}

	}

	public static void deliver(String s, boolean sent, DeliveredCounter counter_debit, boolean debit) {
		if (!debit)
			System.out.println("Message délivré: " + s);
		else {
			counter_debit.inc();
		}
	}

	public static void checkDeliver(
			ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> context_msg_hash,
			ConcurrentHashMap<String, String> message_hash,
			ConcurrentHashMap<String, Boolean> context,
			boolean sent,
			DeliveredCounter counter_debit,
			boolean debit) {

		LinkedList<String> messages_to_deliver = new LinkedList<String>();
		// on regarde quelle machine a acquite les messages envoye par cette
		// machine
		for (String s : context_msg_hash.keySet()) {
			boolean ok_delivery = true;
			// pour chaque message on parcourt les machines en reseau pour
			// savoir lesquelles ont repondu
			// si elles n'ont pas repondu on regarde si elles sont en vies.
			for (String s1 : context_msg_hash.get(s).keySet()) {
				if (!context_msg_hash.get(s).get(s1)) {
					if (context.get(s1)) {
						ok_delivery = false;
						break;
					}
				}
			}
			if (ok_delivery) {
				messages_to_deliver.add(s);
			}
		}
		for (String s : messages_to_deliver) {
			context_msg_hash.remove(s);
			deliver(message_hash.get(s), sent, counter_debit, debit);
			message_hash.remove(s);
		}
	}
}
