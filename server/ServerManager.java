import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;


public class ServerManager implements Runnable {

	private Socket socketClient;
	private HashMap<String, HashMap<String, Boolean>> c_messages_sent = 
			new HashMap<String, HashMap<String,Boolean>>();
	private HashMap<String, HashMap<String, Boolean>> c_messages_received = 
			new HashMap<String, HashMap<String,Boolean>>();
	
	private HashMap<String, String> messages_received = 
			new HashMap<String, String>();
	
	private HashMap<String, String> messages_sent = 
			new HashMap<String, String>();
	private HashMap<String, Boolean> context;
	
	public ServerManager(Socket s) {
		socketClient = s;
	}
	
	
	
	public ServerManager(Socket socketClient,
			HashMap<String, HashMap<String, Boolean>> c_messages_sent,
			HashMap<String, HashMap<String, Boolean>> c_messages_received,
			HashMap<String, String> messages_received,
			HashMap<String, String> messages_sent,
			HashMap<String, Boolean> context) {
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
			byte down_packet[] = new byte[65000];
			while (true) {
				int b_read = in.read(down_packet);
				String s = new String(down_packet);
				//cas reception d'un ping
				if (s.contains("ping")) {
					byte data_out[]=s.getBytes();
					out.write(data_out);
					out.flush();
				}
				//cas de reception d'un acquitement
				else if (s.contains("ACK")) {
					String id_msg = s.substring(s.indexOf("/")+1, s.indexOf("/", s.indexOf("/")+1));
					String Ip_origine = socketClient.getInetAddress().toString();
					if (c_messages_received.containsKey(Ip_origine + id_msg)) {
						c_messages_received.get(Ip_origine + id_msg).put(Ip_origine, true);
					}
					if (c_messages_sent.containsKey(Ip_origine + id_msg)) {
						c_messages_sent.get(Ip_origine + id_msg).put(Ip_origine, true);
					}
				} 
				else {
					String id_msg = s.substring(s.indexOf("/")+1, s.indexOf("/", s.indexOf("/")+1));
					String msg = s.replace("/"+id_msg+"/", "");
					HashMap<String, Boolean> context_message = new HashMap<String, Boolean>();
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

}
