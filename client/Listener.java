
import java.io.IOException;
import java.net.ServerSocket;
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
            socketserver.accept();
        } catch (IOException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
