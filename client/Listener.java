import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mansourr
 */
public class Listener implements Runnable {

    private int port;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent
            = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received
            = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();

    private ConcurrentHashMap<String, String> messages_received
            = new ConcurrentHashMap<String, String>();

    private ConcurrentHashMap<String, String> messages_sent
            = new ConcurrentHashMap<String, String>();

    private ConcurrentHashMap<String, Boolean> context;
    private ConcurrentHashMap<String, Boolean> cont_connected;

    public Listener(int port) {
        this.port = port;
    }

    public Listener(int port,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received,
            ConcurrentHashMap<String, String> messages_received,
            ConcurrentHashMap<String, String> messages_sent,
            ConcurrentHashMap<String, Boolean> context,
            ConcurrentHashMap<String, Boolean> cont_connected) {
        super();
        this.port = port;
        this.c_messages_sent = c_messages_sent;
        this.c_messages_received = c_messages_received;
        this.messages_received = messages_received;
        this.messages_sent = messages_sent;
        this.context = context;
        this.cont_connected = cont_connected;
    }

    public void run() {
        try {
            ServerSocket socketserver = new ServerSocket(port);
            ConcurrentLinkedQueue<Socket> sockets = new ConcurrentLinkedQueue<Socket>();
            //System.out.println("Le serveur est à l'écoute du port " + socketserver.getLocalPort());
            //System.out.println(sockets);
            new Thread(new ServerManager(sockets, c_messages_sent, c_messages_received, messages_received, messages_sent, context, cont_connected)).start();
            new Thread(new ServerManager(sockets, c_messages_sent, c_messages_received, messages_received, messages_sent, context, cont_connected)).start();
            new Thread(new ServerManager(sockets, c_messages_sent, c_messages_received, messages_received, messages_sent, context, cont_connected)).start();
            //long cpt = 0;
            while (true) {
                sockets.add(socketserver.accept());
            }
            //socketserver.close();
        } catch (IOException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
