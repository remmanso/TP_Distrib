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
            while (true) {
                while ((socketClient = sockets.poll()) == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                DataOutputStream out = new DataOutputStream(
                        socketClient.getOutputStream());
                DataInputStream in = new DataInputStream(
                        socketClient.getInputStream());
                byte down_packet[] = new byte[1000000];

                int b_read = in.read(down_packet);
                String s = new String(down_packet, "UTF-8");
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
                    String id_msg = s.substring(s.indexOf("/") + 1,
                            s.indexOf("/", s.indexOf("/") + 1));
                    String Ip_origine = socketClient.getInetAddress()
                            .getHostAddress().toString();
                    Ip_origine = Ip_origine.replace("/", "");
                    if (c_messages_received.containsKey(id_msg)) {
                        c_messages_received.get(id_msg).put(Ip_origine, true);
                    }
                    if (c_messages_sent.containsKey(id_msg)) {
                        c_messages_sent.get(id_msg).put(Ip_origine, true);
                    }
                } else if (s.contains("Connected")) {
                    String Ip_origine = socketClient.getInetAddress()
                            .getHostAddress().toString();
                    cont_connected.put(Ip_origine, true);
                }// cas reception d'un message
                else if (b_read != -1) {
                    //System.out.println("Inside b_read: " + s.length());
                    while ((b_read = in.read(down_packet)) != -1) {
                        s += new String(down_packet, "UTF-8");
                    }
                    //System.out.println("Message lu: " + s.length());
                    String id_msg = s.substring(s.indexOf("/") + 1,
                            s.indexOf("/", s.indexOf("/") + 1));
                    String msg = s.replace("/" + id_msg + "/", "");
                    String Ip_origine = socketClient.getInetAddress()
                            .getHostAddress().toString();
                    //System.out.println(Ip_origine);
                    Ip_origine = Ip_origine.replace("/", "");
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
                    Broadcast broad = new Broadcast("/" + id_msg + "/" + "ACK", context);
                    broad.run();
                }
                socketClient.close();
            }
        } catch (SocketException e) {
            //new Thread(new ServerManager(sockets, cont_connected)).start();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
