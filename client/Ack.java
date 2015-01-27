
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
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

public class Ack implements Runnable {
    
    private Socket socket;
    private String msg;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    
    public Ack(Socket socketclient,
            String msg,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received){
        this.socket = socketclient;
        this.msg = msg;
        this.c_messages_received = c_messages_received;
        this.c_messages_sent = c_messages_sent;
    }
    
    public void run(){
        try {
            byte down_packet[] = new byte[20];
            socket.getInputStream().read(down_packet);
            String id_msg = new String(down_packet);
            //int index_s = msg.indexOf("/");
            //int index_s2 = msg.indexOf("/", index_s+1);
            //String id_msg = msg.substring(index_s + 1,index_s2);
            String Ip_origine = socket.getInetAddress()
                    .getHostAddress();
            Ip_origine.replaceFirst("/", "");
            if (c_messages_received.containsKey(id_msg)) {
                c_messages_received.get(id_msg).put(Ip_origine, true);
            }
            if (c_messages_sent.containsKey(id_msg)) {
                c_messages_sent.get(id_msg).put(Ip_origine, true);
            }
            System.out.println("Send Ack");
        } catch (IOException ex) {
            Logger.getLogger(Ack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}