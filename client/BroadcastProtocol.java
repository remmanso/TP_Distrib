import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class BroadcastProtocol {

	public static void main(String[] args) throws IOException {
		
            if (args.length == 0) {
                System.out.println("Veuillez rentrer une ou plusieurs adresse IP en arguments");
                return;
            }

            ConcurrentHashMap<String, Boolean> context = new ConcurrentHashMap<String, Boolean>();

            for (String arg : args) {
                context.put(arg, true);
            }

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

            String message = "hello " + InetAddress.getLocalHost();
            Thread b = new Thread(new Broadcast(message, context, c_messages_sent, messages_sent));
            b.start();

            while (true) {
//                LinkedList<String> messages_to_deliver = new LinkedList<String>();
//                //on regarde quelle machine a acquite les messages envoye par cette machine
//                for (String s : c_messages_sent.keySet()) {
//                    boolean ok_delivery = true;
//                    //pour chaque message on parcourt les machines en reseau pour savoir lesquelles ont repondu
//                    //si elles n'ont pas repondu on regarde si elles sont en vies.
//                    for (String s1 : c_messages_sent.get(s).keySet()) {
//                        if (!c_messages_sent.get(s).get(s1)) {
//                            if (context.get(s1)) {
//                                ok_delivery = false;
//                                break;
//                            }
//                        }
//                    }
//                    
//                    if (ok_delivery){
//                        messages_to_deliver.add(s);
//                    }
//                }
//                for (String s :messages_to_deliver) {
//                    c_messages_sent.remove(s);
//                    deliver(messages_sent.get(s));
//                    messages_sent.remove(s);
//                }
//
//                messages_to_deliver.clear();
                checkDeliver(c_messages_sent,messages_sent,context);
                checkDeliver(c_messages_received,messages_received,context);
//                //on regarde quelle machine a acquite les messages recus par cette machine
//                for (String s : c_messages_received.keySet()) {
//                    boolean ok_delivery = true;
//                    //pour chaque message on parcourt les machines en r��seau pour savoir lesquelles ont r��pondu
//                    //si elles 'nont pas repondu on regarde si elles sont en vies.
//                    for (String s1 : c_messages_received.get(s).keySet()) {
//                        if (!c_messages_received.get(s).get(s1)) {
//                            if (context.get(s1)) {
//                                ok_delivery = false;
//                                break;
//                            }
//                        }
//                    }
//                    if (ok_delivery)
//                        messages_to_deliver.add(s);
//                } 
//                for (String s :messages_to_deliver) {
//                    c_messages_received.remove(s);
//                    deliver(messages_received.get(s));
//                    messages_received.remove(s);
//                }
//                messages_to_deliver.clear();
            }
            
	}
	
	public static void deliver(String s) {
            System.out.println("Message délivré: "+s);
	}
        
        public static void checkDeliver(ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> context_msg_hash,
                ConcurrentHashMap<String, String> message_hash, ConcurrentHashMap<String, Boolean> context){
            
                LinkedList<String> messages_to_deliver = new LinkedList<String>();
                //on regarde quelle machine a acquite les messages envoye par cette machine
                for (String s : context_msg_hash.keySet()) {
                    boolean ok_delivery = true;
                    //pour chaque message on parcourt les machines en reseau pour savoir lesquelles ont repondu
                    //si elles n'ont pas repondu on regarde si elles sont en vies.
                    for (String s1 : context_msg_hash.get(s).keySet()) {
                        if (!context_msg_hash.get(s).get(s1)) {
                            if (context.get(s1)) {
                                ok_delivery = false;
                                break;
                            }
                        }
                    }
                    
                    if (ok_delivery){
                        messages_to_deliver.add(s);
                    }
                }
                for (String s :messages_to_deliver) {
                    context_msg_hash.remove(s);
                    deliver(message_hash.get(s));
                    message_hash.remove(s);
                }
        }
}
