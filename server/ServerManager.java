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
	
	public ServerManager(Socket s) {
		socketClient = s;
	}
	
	
	
	public ServerManager(Socket socketClient,
			HashMap<String, HashMap<String, Boolean>> c_messages_sent,
			HashMap<String, HashMap<String, Boolean>> c_messages_received) {
		super();
		this.socketClient = socketClient;
		this.c_messages_sent = c_messages_sent;
		this.c_messages_received = c_messages_received;
	}



	@Override
	public void run() {
		try {
			DataOutputStream out = new DataOutputStream(socketClient.getOutputStream());
			DataInputStream in = new DataInputStream(socketClient.getInputStream());;
			byte ack_packet[] = new byte[1];
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
					String id_msg = new String();
					String Ip_origine = new String(socketClient.getInetAddress().toString());
					if (c_messages_received.containsKey(Ip_origine + id_msg)) {
						c_messages_received.get(Ip_origine + id_msg).put(Ip_origine, true);
					}
					if (c_messages_sent.containsKey(Ip_origine + id_msg)) {
						c_messages_sent.get(Ip_origine + id_msg).put(Ip_origine, true);
					}
				} 
				else {
					
				}
				//cas de reception d'un message
				String m = new String(b_read + " origine: " + socketClient.getInetAddress().toString());
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
