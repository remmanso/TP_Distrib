import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class FaultDetector implements Runnable {

    private String args[];
    private ConcurrentHashMap<String, Boolean> context;
    private ConcurrentHashMap<String, Boolean> cont_connected;
    private int port;
    
    public FaultDetector(String args[], ConcurrentHashMap<String, Boolean> context, ConcurrentHashMap<String, Boolean> cont_connect, int port) {
        this.args = args;
        this.context = context;
        this.cont_connected = cont_connect;
        this.port = port;
    }

    @Override
    public void run() {
        TimerPing t_ping;
        Timer timer = new Timer();
        ConcurrentLinkedQueue<Socket> sockets = new ConcurrentLinkedQueue<Socket>();
        for (int i = 0; i < args.length; i++) {
            t_ping = new TimerPing(args[i], port, context);
            timer.scheduleAtFixedRate(t_ping, 1000, 1000);
        }
        
        ServerSocket socketserver;
        try {
            socketserver = new ServerSocket(port);
            System.out.println("Le serveur est à l'écoute du port " + socketserver.getLocalPort());
            new Thread(new ServerManager(sockets, cont_connected)).start();

            while (true) {
                sockets.add(socketserver.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
