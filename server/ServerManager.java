import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class ServerManager implements Runnable {

	private Socket socketClient;
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = 
			new ConcurrentHashMap<String, ConcurrentHashMap<String,Boolean>>();
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = 
			new ConcurrentHashMap<String, ConcurrentHashMap<String,Boolean>>();
	
	private ConcurrentHashMap<String, String> messages_received = 
			new ConcurrentHashMap<String, String>();
	
	private ConcurrentHashMap<String, String> messages_sent = 
			new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, Boolean> context;
	
	public ServerManager(Socket s) {
		socketClient = s;
	}
	
	//new ServerManager(socketduserveur, c_messages_sent, c_messages_received,
            		//messages_received, messages_sent, context));
	
	public ServerManager(Socket socketClient,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received,
			ConcurrentHashMap<String, String> messages_received,
			ConcurrentHashMap<String, String> messages_sent,
			ConcurrentHashMap<String, Boolean> context) {
		super();
		this.socketClient = socketClient;
		this.c_messages_sent = c_messages_sent;
		this.c_messages_received = c_messages_received;
		this.messages_received = messages_received;
		this.messages_sent = messages_sent;
		this.context = context;
	}


	@Override
	public void run() {
		try {
			DataOutputStream out = new DataOutputStream(socketClient.getOutputStream());
			DataInputStream in = new DataInputStream(socketClient.getInputStream());;
			
			while (true) {
				byte down_packet[] = new byte[65000];
				int b_read = in.read(down_packet);
				String s = new String(down_packet);
				//cas reception d'un ping
                                if (b_read != -1)
                                    System.out.println("debug : " + s);
				if (s.contains("ping")) {
                                    byte data_out[]=s.getBytes();
                                    out.write(data_out);
                                    out.flush();
				}
				//cas de reception d'un acquitement
				else if (s.contains("ACK")) {
					String id_msg = s.substring(s.indexOf("/")+1, s.indexOf("/", s.indexOf("/")+1));
					String Ip_origine = socketClient.getInetAddress().toString();
					//System.out.println("ip + id : " + Ip_origine + " " + id_msg);
					if (c_messages_received.containsKey(id_msg)) {
						c_messages_received.get(id_msg).put(Ip_origine, true);
					}
					if (c_messages_sent.containsKey(id_msg)) {
						c_messages_sent.get(id_msg).put(Ip_origine, true);
					}
				} 
				//cas reception d'un message
				else if (b_read != - 1) {
					String id_msg = s.substring(s.indexOf("/")+1, s.indexOf("/", s.indexOf("/")+1));
					String msg = s.replace("/"+id_msg+"/", "");
					ConcurrentHashMap<String, Boolean> context_message = new ConcurrentHashMap<String, Boolean>();
					for (String ip : context.keySet())
						context_message.put(ip, false);
					c_messages_received.put(id_msg, context_message);
					messages_received.put(id_msg, msg);
					Thread b = new Thread(new Broadcast("/"+id_msg + "/"+ "ACK", context));
			        b.start();
				}
				//cas de reception d'un message
				String m = new String(b_read + " origine: " + socketClient.getInetAddress().toString());
                                //String hash = m.substring(m.indexOf("/")+1, m.indexOf("/", m.indexOf("/")+1));
                                //String message_sans_hascode = m.replace("/"+hash+"/", "");

			}
		} catch (SocketException e) {
			try {
				socketClient.close();
			}catch(IOException exc) {
				exc.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
        
//        public static void mem(DataOutputStream out,
//			DataInputStream in,
//			byte down_packet){
//                                int b_read = in.read(down_packet);
//				String s = new String(down_packet);
//				//System.out.println("Debug : " + s);
//                                //System.out.println(b_read);
//				//cas reception d'un ping
//				if (s.contains("ping")) {
//                                    byte data_out[]=s.getBytes();
//                                    out.write(data_out);
//                                    out.flush();
//				}
//				//cas de reception d'un acquitement
//				else if (s.contains("ACK")) {
//                                    System.out.println("Acquitement");
//                                    String id_msg = s.substring(s.indexOf("/")+1, s.indexOf("/", s.indexOf("/")+1));
//                                    String Ip_origine = socketClient.getInetAddress().toString();
//                                    if (c_messages_received.containsKey(Ip_origine + id_msg)) {
//                                        c_messages_received.get(Ip_origine + id_msg).put(Ip_origine, true);
//                                    }
//                                    if (c_messages_sent.containsKey(Ip_origine + id_msg)) {
//                                        c_messages_sent.get(Ip_origine + id_msg).put(Ip_origine, true);
//                                    }
//				} 
//				//cas reception d'un message
//				else if (b_read != -1){
//                                    String id_msg = s.substring(s.indexOf("/")+1, s.indexOf("/", s.indexOf("/")+1));
//                                    String msg = s.replace("/"+id_msg+"/", "");
//                                    ConcurrentHashMap<String, Boolean> context_message = new ConcurrentHashMap<String, Boolean>();
//                                    for (String ip : context.keySet())
//                                            context_message.put(ip, false);
//                                    c_messages_received.put(id_msg, context_message);
//                                    messages_received.put(id_msg, msg);
//                                    Thread b = new Thread(new Broadcast("/"+id_msg + "/"+ "ACK", context));
//                                    b.start();
//				}
//				//cas de reception d'un message
//				String m = new String(b_read + " origine: " + socketClient.getInetAddress().toString());
//                                //String hash = m.substring(m.indexOf("/")+1, m.indexOf("/", m.indexOf("/")+1));
//                                //String message_sans_hascode = m.replace("/"+hash+"/", "");
//        }

}
