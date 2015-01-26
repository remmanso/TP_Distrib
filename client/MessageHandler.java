
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mansourr
 */
public class MessageHandler implements Runnable{
    private final Socket socketClient;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final String s;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, String> messages_received = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, Boolean> context;
    //private byte[] down;
    
    public MessageHandler(Socket socketclient,
            String msg,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received,
            ConcurrentHashMap<String, String> messages_received,
            ConcurrentHashMap<String, String> messages_sent,
            ConcurrentHashMap<String, Boolean> context,
            DataInputStream in, 
            DataOutputStream out) {
        this.socketClient = socketclient;
        this.s = msg;
        this.c_messages_sent = c_messages_sent;
        this.c_messages_received = c_messages_received;
        this.messages_received = messages_received;
        this.context = context;
        this.in = in;
        this.out = out;
        //this.down_packet = down_packet;
    }
    
    public void run(){
        try {
            long time_start = System.nanoTime();
            int b_read = 0;
            int i = 0;
            byte b;
            byte down_packet[] = new byte[1005000];
            long time = System.nanoTime();
            long time_moy = 0;
            while ((b_read = in.read(down_packet)) != -1) {
                i++;
                //time = System.nanoTime();
                //s += new String(down_packet);
                time_moy = (System.nanoTime() - time)/i;
            }
            System.out.println(s);
            System.out.println(Thread.currentThread() + ", réponse à message temps mis : " + (System.nanoTime() - time_start) + "ns, itérations :" + i + " tps_moy : " + time_moy);
            //System.out.println(i + " iteration sur la while b_read, tps moy par iteration  " + time_moy);
            //long time_bread = System.nanoTime() - time_max;

            //time_all = System.nanoTime();
            int index_s = s.indexOf("/");
            int index_s2 = s.indexOf("/", index_s+1);
            //System.out.println(index_s + ", " + index_s2 + " ," + s.length());
            String id_msg = s.substring(index_s + 1,index_s2);
            String msg = s.substring(index_s2+1, s.length()-1);
            String Ip_origine = socketClient.getInetAddress()
                    .getHostAddress();
            //System.out.println(Ip_origine + " taille : " + Ip_origine.length());
            Ip_origine = Ip_origine.replaceFirst("/", "");
            //long time_str = System.nanoTime() - time_all;


            //long time_ccr = System.nanoTime();
            ConcurrentHashMap<String, Boolean> context_message = new ConcurrentHashMap<String, Boolean>();
            for (String ip : context.keySet()) {
                context_message.put(ip, Ip_origine.equals(ip));
            }   
            c_messages_received.put(id_msg, context_message);
            messages_received.put(id_msg, msg);
            new Thread(new Broadcast("/" + id_msg + "/" + "ACK", context, socketClient)).start();
                    
                    
                
        } catch (IOException ex) {
            Logger.getLogger(Ack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
