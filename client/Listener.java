import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
public class Listener implements Runnable{
    private int port;
    
    public Listener(int port){
        this.port = port;
    }
    
    public void run() {
        try {
            ServerSocket socketserver = new ServerSocket(port);
            
            Socket socketduserveur = socketserver.accept();
            Thread t = new Thread(new ServerManager(socketduserveur));
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
