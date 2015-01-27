import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageManager implements Runnable {

    private ConcurrentHashMap<String, Boolean> context;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent;
    private ConcurrentHashMap<String, String> messages_sent;
    private DeliveredCounter counter_debit;
    private boolean debit;
    private ConcurrentLinkedQueue<Socket> sockets;
            
    public MessageManager(
            ConcurrentHashMap<String, Boolean> context,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
            ConcurrentHashMap<String, String> messages_sent,
            DeliveredCounter counter_debit, boolean debit,
            ConcurrentLinkedQueue<Socket> sockets) {
        super();
        this.context = context;
        this.c_messages_sent = c_messages_sent;
        this.messages_sent = messages_sent;
        this.counter_debit = counter_debit;
        this.debit = debit;
        this.sockets = sockets;
    }

    @Override
    public void run() {
        System.out.println("Started");
        int cpt = 1;
        byte[] downpacket = new byte[1000000];
        new Random().nextBytes(downpacket);
        String message = new String(downpacket);
        Broadcast b = new Broadcast("HEHE"+message, context,
                c_messages_sent, messages_sent, sockets);

        while (!debit || context.contains(true)) {
            if (!debit) {
                message = messageDefinition(cpt);
                b.setMessage(message);
            }
            b.run();

            try {
                if (!debit) {
                    Thread.sleep(2000);
                }
                /*else
                    Thread.sleep(1000);
                        */
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cpt++;
        }
    }

    private String messageDefinition(int cpt) {

        try {
            String s = "Hello n°" + cpt + " from " + InetAddress.getLocalHost();
            return s;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

}
