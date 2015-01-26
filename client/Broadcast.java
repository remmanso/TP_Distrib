import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
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
public class Broadcast implements Runnable {

    private String message;
    private Socket socket;
    private Socket socketClient;
    private ConcurrentHashMap<String, Boolean> list_adr;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, String> messages_sent = new ConcurrentHashMap<String, String>();

    public Broadcast(
            String message,
            ConcurrentHashMap<String, Boolean> list_adr,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
            ConcurrentHashMap<String, String> messages_sent) {
        //super();
        this.message = message;
        this.list_adr = list_adr;
        this.c_messages_sent = c_messages_sent;
        this.messages_sent = messages_sent;
    }

    public Broadcast(String message, ConcurrentHashMap<String, Boolean> list_adr) {
        this.message = message;
        this.list_adr = list_adr;
    }
    
    public Broadcast(String message, ConcurrentHashMap<String, Boolean> list_adr, Socket socketClient) {
        this.message = message;
        this.list_adr = list_adr;
        this.socketClient = socketClient;
    }

    public void run() {
        try {
            DataInputStream in;
            DataOutputStream out;
//            long time_total = System.nanoTime();
            long time_start = System.nanoTime();
            if (!message.contains("ACK") && !message.isEmpty()) {
                
                String m = System.nanoTime() 
                        + InetAddress.getLocalHost().getHostAddress();
                
                String original_message = message;
                message = "/" + m.hashCode() + "/" + message;
                
                //System.out.println("temps pour str : " + time);
                ConcurrentHashMap<String, Boolean> context_message = new ConcurrentHashMap<String, Boolean>();

                for (String ip : list_adr.keySet()) {
                    context_message.put(ip, false);
                }
                c_messages_sent.put(Integer.toString(m.hashCode()),
                        context_message);
                messages_sent.put(Integer.toString(m.hashCode()),
                        original_message);
            }
            byte b[] = message.getBytes();
//            time = System.nanoTime();
            int i = 0;
            for (String s : list_adr.keySet()) {
                i ++;
                if ("LocalHost".equals(s) || !list_adr.get(s)) {
                    continue;
                }
                socket = new Socket(s, 2010);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
                
                out.write(b,0,b.length);
//                time = System.nanoTime();
                /*if (!message.contains("ping")) {
                    System.out.println("BROADCAST : " + message.length());
                }*/
                out.flush();
                socket.close();
            }
//            time = System.nanoTime() - time;
//            if (!message.contains("ACK") && !message.isEmpty()){
//                time_total = System.nanoTime() - time_total;
//               // System.out.println("total time , prcent " + time_total + " stringprcent " + (float)time*100/time_total);
//            }
            if (socketClient != null)
                socketClient.close();
//            System.out.println(Thread.currentThread() + ", envoi d'un message temps mis : " + (System.nanoTime() - time_start) + "ns, itérations :" + i);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            if(socket != null)
                System.out.println("UnknownHostException : La connexion avec " + socket.getInetAddress().getHostName() +" a été coupée.");
            else
                System.out.println("UnknownHostException : une socket est passé à null");
        } catch (SocketException e) {
            if(socket != null)
                System.out.println("SocketException : La connexion avec " + socket.getInetAddress().getHostName() +" a été coupée.");
            else
                System.out.println("SocketException : une socket est passé à null");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
