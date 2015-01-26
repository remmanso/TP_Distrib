
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
    private Socket socketClient;
    private DataInputStream in;
    private DataOutputStream out;
    private String s;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, String> messages_received = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, Boolean> context;
    //private byte[] down_packet;
    
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
        //this.down_packet = down_packet;
    }
    
    public void run(){
        try {
            byte down_packet[] = new byte[1000000];
            int b_read = 0;
            while ((b_read = in.read(down_packet,0,1000000)) != -1) {
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
            Broadcast broad = new Broadcast("/" + id_msg + "/" + "ACK", context);
            broad.run();
            //socketClient.close();
        } catch (IOException ex) {
            Logger.getLogger(Ack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
