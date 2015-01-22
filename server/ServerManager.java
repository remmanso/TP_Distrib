import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager implements Runnable {

	private Socket socketClient;
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
	private ConcurrentHashMap<String, String> messages_received = new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, String> messages_sent = new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, Boolean> context;
	private ConcurrentHashMap<String, Boolean> cont_connected;

	public ServerManager(Socket s) {
		socketClient = s;
	}

	public ServerManager(
			Socket socketClient,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received,
			ConcurrentHashMap<String, String> messages_received,
			ConcurrentHashMap<String, String> messages_sent,
			ConcurrentHashMap<String, Boolean> context,
			ConcurrentHashMap<String, Boolean> cont_connected) {
		super();
		this.socketClient = socketClient;
		this.c_messages_sent = c_messages_sent;
		this.c_messages_received = c_messages_received;
		this.messages_received = messages_received;
		this.messages_sent = messages_sent;
		this.context = context;
		this.cont_connected = cont_connected;
	}

	@Override
	public void run() {
		try {
			/*DataOutputStream out = new DataOutputStream(
					socketClient.getOutputStream());
			DataInputStream in = new DataInputStream(
					socketClient.getInputStream());*/
			BufferedInputStream in = new BufferedInputStream(socketClient.getInputStream());
			BufferedOutputStream out = new BufferedOutputStream(socketClient.getOutputStream());

			byte down_packet[] = new byte[150000];
			int b_read = in.read(down_packet);
			String s = new String(down_packet);
			
			// cas reception d'un ping
			if (s.contains("ping")) {
				byte data_out[] = s.getBytes();
				out.write(data_out);
				out.flush();
			} // cas de reception d'un acquitement
			else if (s.contains("ACK")) {
				String id_msg = s.substring(s.indexOf("/") + 1,
						s.indexOf("/", s.indexOf("/") + 1));
				String Ip_origine = socketClient.getInetAddress()
						.getHostAddress().toString();
				Ip_origine = Ip_origine.replace("/", "");
				if (c_messages_received.containsKey(id_msg)) {
					c_messages_received.get(id_msg).put(Ip_origine, true);
				}
				if (c_messages_sent.containsKey(id_msg)) {
					c_messages_sent.get(id_msg).put(Ip_origine, true);
				}
			} else if (s.contains("Connected")) {
				String Ip_origine = socketClient.getInetAddress()
						.getHostAddress().toString();
				cont_connected.put(Ip_origine, true);
			}// cas reception d'un message
			else if (b_read != -1) {
				b_read = in.read(down_packet);
				while (b_read != -1) {
					b_read = in.read(down_packet);
					s += new String(down_packet);
				}
				//System.out.println("message longueur : " + s.length());
				String id_msg = s.substring(s.indexOf("/") + 1,
						s.indexOf("/", s.indexOf("/") + 1));
				String msg = s.replace("/" + id_msg + "/", "");
				String Ip_origine = socketClient.getInetAddress()
						.getHostAddress().toString();
				Ip_origine = Ip_origine.replace("/", "");
				ConcurrentHashMap<String, Boolean> context_message = new ConcurrentHashMap<String, Boolean>();
				for (String ip : context.keySet()) {
					if (Ip_origine.equals(ip)) {
						context_message.put(ip, true);
					} else {
						context_message.put(ip, false);
					}
				}
				c_messages_received.put(id_msg, context_message);
				messages_received.put(id_msg, msg);
				Thread b = new Thread(new Broadcast("/" + id_msg + "/" + "ACK",
						context));
				b.start();
			}
			socketClient.close();
		} catch (SocketException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
