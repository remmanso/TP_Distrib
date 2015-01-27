
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
    
    private final Socket socket;
    private final DataOutputStream out;
    
    public Ping(Socket socketclient, 
            DataOutputStream out){
        this.socket = socketclient;
        this.out=out;
    }
    
    public void run(){
        try {
            byte b[] = new byte[64];
            //b[0]=1;
            //System.out.println(new String(b));
            String s = new String(b);
            s = "ping".concat(s);
            b = s.getBytes();
            //data_out = ("pong" + data_out.toString()).getBytes();
            out.write(b);
        } catch (IOException ex) {
            Logger.getLogger(Ping.class.getName()).log(Level.SEVERE, "etat des variable out & socket" + out + "  ," + socket, ex);
        }
    }
}
