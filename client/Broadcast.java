
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mansourr
 */
public class Broadcast implements Runnable{
    private String message;
    HashMap<String, Boolean> list_adr;
    
    public Broadcast(String message, HashMap<String, Boolean> list_adr){
        this.message = message;
        this.list_adr = list_adr;
    }
    
    @Override
    public void run(){
        try{
                InputStream in;
                OutputStream out;               
                System.out.println("Broadcast in process...");

                for(String s : list_adr.keySet()){
                    if("LocalHost".equals(s) || !list_adr.get(s))
                        continue;
                    
                    Socket socket = new Socket(s, 2010);
                    
                    if(!message.contains("ACK")){
                        String m = message + socket.getInetAddress().toString();
                        message = "/" + m.hashCode() + "/" + message;
                    }
                    
                    byte b[] = message.getBytes();
                    out = socket.getOutputStream();
                    in = socket.getInputStream();
                    out.write(b);
                    //System.out.println(message);
                    socket.close();
                }
                
        }catch (UnknownHostException e) {

                e.printStackTrace();
        }catch (SocketException e) {

                //e.printStackTrace();
        }catch (IOException e) {

                e.printStackTrace();
        }
    }
}
