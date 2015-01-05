import java.util.TimerTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.SocketException;

public class TimerPing extends TimerTask 
{
	private String addresse;

	public TimerPing(String ad) {
		addresse = ad;
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

			socket = new Socket(this.addresse, 2009);  
			socket.setSoTimeout(1000); 

			System.out.println("Ping process...");

			time = System.nanoTime();
			out = socket.getOutputStream();
			in = socket.getInputStream();
			out.write(b);
			b_read = in.read(b);
			

			time = System.nanoTime() - time;
			double time_ms = (double) time / 1000000.0;
			System.out.println("nb octet reçu : " + b_read + " en " + time_ms + "ms ");
			
			socket.close();


		}catch (UnknownHostException e) {
			
			e.printStackTrace();
		}catch (SocketTimeoutException e) {

			System.out.println("Request ping time out");
		}catch (IOException e) {
			
			e.printStackTrace();
		} 
	}
}
