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

   Socket socket;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, String> messages_received = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, String> messages_sent = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, Boolean> context;
    private ConcurrentHashMap<String, Boolean> cont_connected;
    private ConcurrentLinkedQueue<Socket> sockets;

    public ServerManager(Socket sockets,
                        ConcurrentHashMap<String, Boolean> cont_connect) {
        this.socket = sockets;
        this.cont_connected = cont_connect;
    }

    public ServerManager(
            Socket socket,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received,
            ConcurrentHashMap<String, String> messages_received,
            ConcurrentHashMap<String, String> messages_sent,
            ConcurrentHashMap<String, Boolean> context,
            ConcurrentHashMap<String, Boolean> cont_connected,
            ConcurrentLinkedQueue<Socket> sockets) {
        super();
        this.socket = socket;
        this.c_messages_sent = c_messages_sent;
        this.c_messages_received = c_messages_received;
        this.messages_received = messages_received;
        this.messages_sent = messages_sent;
        this.context = context;
        this.cont_connected = cont_connected;
        this.sockets = sockets;
    }

    @Override
    public void run() {
        try {
            
            //System.out.println("INSIDE SERVER MANAGER");
            Socket socketClient = this.socket;
//            long cpt_run = 0;
            long time_start = System.nanoTime();
            int b_read;
            DataOutputStream out = new DataOutputStream(
                        socketClient.getOutputStream());
            DataInputStream in = new DataInputStream(
                        socketClient.getInputStream());
            String s;
//            long time_moy;
            byte down_packet[] = new byte[100];
            while (true) {
                int i = 0;
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                long time_all = System.nanoTime();
                b_read = in.read(down_packet);
                s = new String(down_packet);
                System.out.println(s);
                // cas reception d'un ping
                long time = System.nanoTime();
                if (s.contains("ping")) {
                    i=1;
                    new Thread(new Ping(socketClient,out)).start();
                    in.skip(in.available());
                } // cas de reception d'un acquitement
                else if (s.contains("ACK")) {
                    i=2;
                    new Thread(new Ack(socketClient, s, c_messages_sent, c_messages_received)).start();
                    in.skip(in.available());
                } else if (s.contains("Connected")) {
                    i = 3;
                    String Ip_origine = socketClient.getInetAddress()
                            .getHostAddress();
                    cont_connected.put(Ip_origine, true);
                    in.skip(in.available());
                }// cas reception d'un message
                else if (b_read != -1) {
                    i = 4;
                    System.out.println("access");
                    new Thread(new MessageHandler(socket, s, c_messages_sent, c_messages_received, messages_received, messages_sent, context, in, out, sockets)).start();
                }
                
//                if (System.nanoTime() - time_start >= Math.pow(10,9)){
//                    System.out.println(Thread.currentThread() + ", opération " + i +" temps mis : " + (System.nanoTime() - time_all) + "ns");
//                    time_start = System.nanoTime();
//                }
                //socketClient.close();
                //long time_since_start = System.nanoTime() - time_start;
                
                //time_moy = time_since_start/cpt_run;
                //System.out.println("temps moyen  & cpt Connected "+ time_since_start + " " + cpt);
                //socket.shutdownInput();
            }
        } catch (SocketException e) {
            //new Thread(new ServerManager(socket, cont_connected)).start();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
