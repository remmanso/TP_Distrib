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

   Socket sockets;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, String> messages_received = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, String> messages_sent = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, Boolean> context;
    private ConcurrentHashMap<String, Boolean> cont_connected;

    public ServerManager(Socket sockets,
                        ConcurrentHashMap<String, Boolean> cont_connect) {
        this.sockets = sockets;
        this.cont_connected = cont_connect;
    }

    public ServerManager(
            Socket sockets,
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
            Socket socketClient = this.sockets;
//            long cpt_run = 0;
            long time_start = System.nanoTime();
            int b_read;
            DataOutputStream out;
            DataInputStream in;
            String s;
//            long time_moy;
            byte down_packet[] = new byte[100];
            while (true) {
                int i = 0;
                //cpt_run++;
//                while ((socketClient = sockets.poll()) == null) {
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
                long time_all = System.nanoTime();
                 out = new DataOutputStream(
                        socketClient.getOutputStream());
                 in = new DataInputStream(
                        socketClient.getInputStream());
               //System.out.println(cont_connected.size());
                
                b_read = in.read(down_packet);
                s = new String(down_packet);
                System.out.println(s);
                /*
                if (!s.contains("ping"))
                    System.out.println("socketClient :"+ socketClient);
                
                if (b_read != -1 && !s.
            new Thread(new ServerManager(sockets, c_messages_sent, c_messages_received, messages_received, messages_sent, context, cont_connected)).start();contains("ping")){
                    System.out.println("ACK ? or CONNECTED ?" + s.contains("ACK") + "," + s.contains("Connected") + ". taille packet : " + s.length());
                }*/
                // cas reception d'un ping
                //System.out.println("length() " + s.length());
                long time = System.nanoTime();
                if (s.contains("ping")) {
                    i=1;
//                    time = System.nanoTime() - time;
//                    System.out.println("temps pour contains " + time);
//                    byte data_out[] = new byte[64];
//                    out.write(data_out);
                    new Thread(new Ping(socketClient,out)).start();
                } // cas de reception d'un acquitement
                else if (s.contains("ACK")) {
                    i=2;
//                    int index_s = s.indexOf("/");
//                    int index_s2 = s.indexOf("/", index_s+1);
//                    //System.out.println(index_s + ", " + index_s2 + " ," + s.length());
//                    String id_msg = s.substring(index_s + 1,index_s2);
//                    String Ip_origine = socketClient.getInetAddress()
//                            .getHostAddress().toString();
//                    //System.out.println(Ip_origine + " taille : " + Ip_origine.length());
//                    Ip_origine = Ip_origine.replaceFirst("/", "");
//                    //long time = System.nanoTime();
//                    if (c_messages_received.containsKey(id_msg)) {
//                        c_messages_received.get(id_msg).put(Ip_origine, true);
//                    }
//                    if (c_messages_sent.containsKey(id_msg)) {
//                        c_messages_sent.get(id_msg).put(Ip_origine, true);
//                    }
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
//                if (System.nanoTime() - time_start >= Math.pow(10,9)){
//                    System.out.println(Thread.currentThread() + ", opération " + i +" temps mis : " + (System.nanoTime() - time_all) + "ns");
//                    time_start = System.nanoTime();
//                }
                //socketClient.close();
                //long time_since_start = System.nanoTime() - time_start;
                
                //time_moy = time_since_start/cpt_run;
                //System.out.println("temps moyen  & cpt Connected "+ time_since_start + " " + cpt);
            }
        } catch (SocketException e) {
            //new Thread(new ServerManager(sockets, cont_connected)).start();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
