
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
public class Ping implements Runnable {
    
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    
    public Ping(Socket socketclient, 
            DataInputStream in, 
            DataOutputStream out){
        this.socket = socketclient;
        this.in =in;
        this.out=out;
        
    }
    
    public void run(){
        try {
            byte data_out[] = new byte[64];
            out.write(data_out);
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Ping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
