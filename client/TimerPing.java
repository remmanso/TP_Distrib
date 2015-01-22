import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.TimerTask;

public class TimerPing extends TimerTask 
{
	private String addresse;
	private int port;
	private ConcurrentHashMap<String, Boolean> context;
	private Socket socket;

	public TimerPing(String ad, int port) {
		addresse = ad;
		this.port = port;
	}

	public TimerPing(String ad, int port, ConcurrentHashMap<String, Boolean> context) {
		addresse = ad;
		this.port = port;
		this.context = context;
	}
	
	public void run(){

		try {
			
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

			time = System.nanoTime();
			out = socket.getOutputStream();
			in = socket.getInputStream();
			out.write(b);
			b_read = in.read(b);

			time = System.nanoTime() - time;
			double time_ms = (double) time / 1000000.0;
			
			socket.close();
                        
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (ConnectException e) {
			context.put(addresse, false);
		}catch (SocketTimeoutException e){
			context.put(addresse, false);
		}catch (SocketException e) {
			context.put(addresse, false);
		}catch (IOException e) {
			
			e.printStackTrace();
		} 
	}
}
