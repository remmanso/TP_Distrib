import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerManager implements Runnable {

    ConcurrentLinkedQueue<Socket> sockets;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, String> messages_received = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, String> messages_sent = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, Boolean> context;
    private ConcurrentHashMap<String, Boolean> cont_connected;

    public ServerManager(ConcurrentLinkedQueue<Socket> sockets,
                        ConcurrentHashMap<String, Boolean> cont_connect) {
        this.sockets = sockets;
        this.cont_connected = cont_connect;
    }

    public ServerManager(
            ConcurrentLinkedQueue<Socket> sockets,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received,
            ConcurrentHashMap<String, String> messages_received,
            ConcurrentHashMap<String, String> messages_sent,
            ConcurrentHashMap<String, Boolean> context,
            ConcurrentHashMap<String, Boolean> cont_connected) {
        super();
        this.sockets = sockets;
        this.c_messages_sent = c_messages_sent;
        this.c_messages_received = c_messages_received;
        this.messages_received = messages_received;
        this.messages_sent = messages_sent;
        this.context = context;
        this.cont_connected = cont_connected;
    }

    @Override
    public void run() {
        try {
            
            //System.out.println("INSIDE SERVER MANAGER");
            Socket socketClient;
            long time_start = System.nanoTime();
            int b_read;
            DataOutputStream out;
            DataInputStream in;
            String s;
            byte down_packet[] = new byte[100];
            while (true) {
                int i = 0;
                while ((socketClient = sockets.poll()) == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                long time_all = System.nanoTime();
                 out = new DataOutputStream(
                        socketClient.getOutputStream());
                 in = new DataInputStream(
                        socketClient.getInputStream());
                b_read = in.read(down_packet);
                s = new String(down_packet);
                long time = System.nanoTime();
                if (s.contains("ping")) {
                    i=1;
                    new Thread(new Ping(socketClient,out)).start();
                } // cas de reception d'un acquitement
                else if (s.contains("ACK")) {
                    i=2;
                    new Thread(new Ack(socketClient, s, c_messages_sent, c_messages_received)).start();
                } else if (s.contains("Connected")) {
                    i = 3;
                    String Ip_origine = socketClient.getInetAddress()
                            .getHostAddress();
                    cont_connected.put(Ip_origine, true);
                    socketClient.close();
                }// cas reception d'un message
                else if (b_read != -1) {
                    i = 4;
                    new Thread(new MessageHandler(socketClient, s, c_messages_sent, c_messages_received, messages_received, messages_sent, context, in, out)).start();
                }
                s = "";
            }
        } catch (SocketException e) {
            //new Thread(new ServerManager(sockets, cont_connected)).start();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
