import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class FaultDetector implements Runnable {

    private String args[];
    private ConcurrentHashMap<String, Boolean> context;
    private int port;

    public FaultDetector(String args[], ConcurrentHashMap<String, Boolean> context, int port) {
        this.args = args;
        this.context = context;
        this.port = port;
    }

    @Override
    public void run() {
        TimerPing t_ping;
        Timer timer = new Timer();
        for (int i = 0; i < args.length; i++) {
            t_ping = new TimerPing(args[i], port, context);
            timer.scheduleAtFixedRate(t_ping, 1000, 1000);
        }

        ServerSocket socketserver;

        try {
            socketserver = new ServerSocket(port);
            ConcurrentLinkedQueue<Socket> sockets = new ConcurrentLinkedQueue<Socket>();
			System.out.println("Le serveur est à l'écoute du port " + socketserver.getLocalPort());
			new Thread(new ServerManager(sockets)).start();
			new Thread(new ServerManager(sockets)).start();
			new Thread(new ServerManager(sockets)).start();
            while (true) {
            	sockets.add(socketserver.accept());
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
