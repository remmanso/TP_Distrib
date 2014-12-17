import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ServerManager implements Runnable {

	private Socket socketClient;
	
	public ServerManager(Socket s, DataInputStream in,
			DataOutputStream out) {
		socketClient = s;
	}
	
	@Override
	public void run() {
		try {
			DataOutputStream out;
			DataInputStream in;
			byte ack_packet[] = new byte[1];
			byte down_packet[] = new byte[65000];
			in = new DataInputStream(socketClient.getInputStream());
			out = new DataOutputStream(socketClient.getOutputStream());
			while (true) {
				int b_read = in.read(down_packet);
				String s = new String(down_packet);
				if (s.contains("ping")) {
					byte data_out[] = new byte[64];
					s.ge
					out.write(new byte[b_read]);
				}
				// (b_read==1)?ack_packet:down_packet 
			    b_read = in.read(down_packet);
			    out.write(down_packet);
			    out.flush();
			}
		    //socketClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
