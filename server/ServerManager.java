import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class ServerManager implements Runnable {

	private Socket socketClient;
	
	public ServerManager(Socket s) {
		socketClient = s;
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
				if (s.contains("ping")) {
					byte data_out[]=s.getBytes();
					out.write(data_out);
					out.flush();
				}
				
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
