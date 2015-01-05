import java.util.HashMap;
import java.util.TimerTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.SocketException;

public class TimerPing extends TimerTask 
{
	private String addresse;
	private int port;
	private HashMap<String, Boolean> context;

	public TimerPing(String ad, int port) {
		addresse = ad;
		this.port = port;
	}

	public TimerPing(String ad, int port, HashMap<String, Boolean> context) {
		addresse = ad;
		this.port = port;
		this.context = context;
	}
	
	public void run(){

		try {
			Socket socket;
			InputStream in;
			OutputStream out;
			long time;
			int b_read = 0;
			byte b[]= "000102030405060708091011121314151617181920212223242526272829".getBytes();
			String s = new String(b);
			s = "ping".concat(s);
			b = s.getBytes();

			socket = new Socket(this.addresse, port);  
			socket.setSoTimeout(1000); 

			System.out.println("Ping process...");

			time = System.nanoTime();
			out = socket.getOutputStream();
			in = socket.getInputStream();
			out.write(b);
			System.out.println("envoie de nb octet : " + b.length);
			b_read = in.read(b);
			

			time = System.nanoTime() - time;
			double time_ms = (double) time / 1000000.0;
			System.out.println("nb octet re��u : " + b_read + " en " + time_ms + "ms ");
			
			socket.close();


		}catch (UnknownHostException e) {
			
			e.printStackTrace();
		}catch (ConnectException e) {
			
			System.out.println("Connection perdue");
			context.put(addresse, false);
			System.out.println(context.toString());
		}catch (SocketTimeoutException e) {

			System.out.println("Request ping time out");
			System.out.println("Connection perdue");
			context.put(addresse, false);
			System.out.println(context.toString());
		}catch (SocketException e) {

			System.out.println("Request ping time out");
			System.out.println("Connection perdue");
			context.put(addresse, false);
			System.out.println(context.toString());
		}catch (IOException e) {
			
			e.printStackTrace();
		} 
	}
}
