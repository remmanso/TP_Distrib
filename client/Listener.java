import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mansourr
 */
public class Listener implements Runnable{
    private int port;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent = 
			new ConcurrentHashMap<String, ConcurrentHashMap<String,Boolean>>();
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received = 
			new ConcurrentHashMap<String, ConcurrentHashMap<String,Boolean>>();
	
	private ConcurrentHashMap<String, String> messages_received = 
			new ConcurrentHashMap<String, String>();
	
	private ConcurrentHashMap<String, String> messages_sent = 
			new ConcurrentHashMap<String, String>();
	
	private ConcurrentHashMap<String, Boolean> context;
    
    public Listener(int port){
        this.port = port;
    }
    
    
    



	public Listener(int port,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_received,
			ConcurrentHashMap<String, String> messages_received,
			ConcurrentHashMap<String, String> messages_sent,
			ConcurrentHashMap<String, Boolean> context) {
		super();
		this.port = port;
		this.c_messages_sent = c_messages_sent;
		this.c_messages_received = c_messages_received;
		this.messages_received = messages_received;
		this.messages_sent = messages_sent;
		this.context = context;
	}






	public void run() {
        try {
            ServerSocket socketserver = new ServerSocket(port);
            while(true){
                Socket socketduserveur = socketserver.accept();
                Thread t = new Thread(new ServerManager(socketduserveur, c_messages_sent, c_messages_received,
                            messages_received, messages_sent, context));
                t.start();
            }
            //socketserver.close();
        } catch (IOException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}