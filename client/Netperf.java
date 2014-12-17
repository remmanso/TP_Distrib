import java.util.TimerTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.SocketException;

public class Netperf extends TimerTask{

	String address;
	// Integer size;
	public Netperf(String address){
		this.address = address;
		// this.size = (size != null) ? new Integer(20*64) : size;
		//this.port = (port == null)? "2009" : port;
	}

	public void run(){
		try{
			Socket socket;
			DataInputStream in;
			DataOutputStream out;
			long downtime, latency, time;
			int b_read = 0;
			byte lat[] = new byte[64];
			byte ask_down[] = new byte[1];
			byte down_packet[] = new byte[65000]; // 1 MibiOctets.

			socket = new Socket(this.address, 2009);  
			//socket.setSoTimeout(500); 

			System.out.println("Netperf process...");

			
			
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			time = System.nanoTime();
			//Latency Calc
			out.write(lat);
			b_read = in.read(lat);

			latency = System.nanoTime() - time;
			double latency_ms = (double) time / 1000000.0;
			System.out.println("nb octet reçu : " + b_read + " en " + latency_ms + "ms ");
			
			out.write(ask_down);

			time = System.nanoTime();
			b_read = in.read(down_packet);

			downtime = System.nanoTime() - time;
			double downtime_ms = (double) (downtime - latency) / 1000000.0;
			System.out.println("Reception de : " + b_read + " bits en " + downtime_ms + "ms : " + b_read*1000/downtime_ms + " bps.");
			
			socket.close();


		}catch (UnknownHostException e) {
			
			e.printStackTrace();
		}catch (SocketException e) {

			System.out.println("Request ping time out");
		}catch (IOException e) {
			
			e.printStackTrace();
		} 
	}
}