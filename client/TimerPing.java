import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimerPing extends TimerTask {

    private String addresse;
    private int port;
    private ConcurrentHashMap<String, Boolean> context;
    private Socket socket;
    public TimerPing(String ad, int port) {
        this.addresse = ad;
        this.port = port;
    }

    public TimerPing(String ad, int port,
            ConcurrentHashMap<String, Boolean> context) {
        this.addresse = ad;
        this.port = port;
        this.context = context;
    }

    public void run() {
        try {
            
            DataInputStream in;
            DataOutputStream out;
            long time;
            int b_read = 0;
            
            time = System.nanoTime();
            byte b[] = new byte[60];
            String s = new String(b);
            s = "ping".concat(s);
            b = s.getBytes();
                    
            socket = new Socket(this.addresse, port);
            socket.setSoTimeout(2000);

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            out.write(b);

            b_read = in.read(b);

            context.put(addresse, true);
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            context.put(addresse, false);
            //e.printStackTrace();
        } catch (SocketTimeoutException e) {
            context.put(addresse, false);
            //e.printStackTrace();
        } catch (SocketException e) {
            context.put(addresse, false);
            System.out.println("La connexion avec " + socket.getInetAddress().getHostName() +" a été coupée.");
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
