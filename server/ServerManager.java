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
//            long cpt_run = 0;
//            long time_start = System.nanoTime();
//            long time_moy;
            while (true) {
                //cpt_run++;
                while ((socketClient = sockets.poll()) == null) {
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                }
                //long time_all = System.nanoTime();
                BufferedOutputStream out = new BufferedOutputStream(
                        socketClient.getOutputStream());
                BufferedInputStream in = new BufferedInputStream(
                        socketClient.getInputStream());
                byte down_packet[] = new byte[1000000];
                
                int b_read = in.read(down_packet);
                String s = new String(down_packet);
                /*
                if (!s.contains("ping"))
                    System.out.println("socketClient :"+ socketClient);
                
                if (b_read != -1 && !s.contains("ping")){
                    System.out.println("ACK ? or CONNECTED ?" + s.contains("ACK") + "," + s.contains("Connected") + ". taille packet : " + s.length());
                }*/
                // cas reception d'un ping
                if (s.contains("ping")) {
                    byte data_out[] = new byte[64];
                    out.write(data_out);
                } // cas de reception d'un acquitement
                else if (s.contains("ACK")) {
                    int index_s = s.indexOf("/");
                    int index_s2 = s.indexOf("/", index_s+1);
                    //System.out.println(index_s + ", " + index_s2 + " ," + s.length());
                    String id_msg = s.substring(index_s + 1,index_s2);
                    String Ip_origine = socketClient.getInetAddress()
                            .getHostAddress().toString();
                    //System.out.println(Ip_origine + " taille : " + Ip_origine.length());
                    Ip_origine = Ip_origine.replaceFirst("/", "");
                    //long time = System.nanoTime();
                    if (c_messages_received.containsKey(id_msg)) {
                        c_messages_received.get(id_msg).put(Ip_origine, true);
                    }
                    if (c_messages_sent.containsKey(id_msg)) {
                        c_messages_sent.get(id_msg).put(Ip_origine, true);
                    }
                    //time = System.nanoTime() - time;
                    //System.out.println("Ack traitement : "+ time +" length c_sent pui c_receive " + c_messages_sent.size() + " ," + c_messages_received.size());
                } else if (s.contains("Connected")) {
                    String Ip_origine = socketClient.getInetAddress()
                            .getHostAddress().toString();
                    cont_connected.put(Ip_origine, true);
                }// cas reception d'un message
                else if (b_read != -1) {
                    int cpt = 0;
                    //long time_max = System.nanoTime();
                    //long cpt = 0;
                    while ((b_read = in.read(down_packet)) != -1) {
                        cpt++;
                        s += new String(down_packet, "UTF-8");
                    }
                    if (s.length() < 200){
                        System.out.println(s);
                    }
                    //System.out.println(cpt + " iteration sur la while b_read");
                    //long time_bread = System.nanoTime() - time_max;
                    
                    //time_all = System.nanoTime();
                    int index_s = s.indexOf("/");
                    int index_s2 = s.indexOf("/", index_s+1);
                    //System.out.println(index_s + ", " + index_s2 + " ," + s.length());
                    String id_msg = s.substring(index_s + 1,index_s2);
                    String msg = s.substring(index_s2+1, s.length()-1);
                    String Ip_origine = socketClient.getInetAddress()
                            .getHostAddress().toString();
                    //System.out.println(Ip_origine + " taille : " + Ip_origine.length());
                    Ip_origine = Ip_origine.replaceFirst("/", "");
                    //long time_str = System.nanoTime() - time_all;

                    
                    //long time_ccr = System.nanoTime();
                    ConcurrentHashMap<String, Boolean> context_message = new ConcurrentHashMap<String, Boolean>();
                    for (String ip : context.keySet()) {
                        if (Ip_origine.equals(ip)) {
                            context_message.put(ip, true);
                        } else {
                            context_message.put(ip, false);
                        }
                    }
                    c_messages_received.put(id_msg, context_message);
                    messages_received.put(id_msg, msg);
                    //time_ccr = System.nanoTime() - time_ccr;
                    
                    
                    //time_all = System.nanoTime();
                    Broadcast broad = new Broadcast("/" + id_msg + "/" + "ACK", context);
                    broad.run();
                    //long time_brd = System.nanoTime() - time_all;
                    
                    //time_max = System.nanoTime() - time_max;
                    //System.out.println("Temps total: " + time_max + "%bread, %str, %ccr, %brd "+ (float)time_bread*100/time_max + " ," + (float)time_str*100/time_max + " ," +(float) time_ccr*100/time_max +" ,"+(float)time_brd*100/time_max);
                }
                socketClient.close();
                //long time_since_start = System.nanoTime() - time_start;
                //time_moy = time_since_start/cpt_run;
                //System.out.println("temps moyen : "+ time_moy);
            }
        } catch (SocketException e) {
            //new Thread(new ServerManager(sockets, cont_connected)).start();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
