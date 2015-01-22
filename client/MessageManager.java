import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

public class MessageManager implements Runnable {

    private ConcurrentHashMap<String, Boolean> context;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent;
    private ConcurrentHashMap<String, String> messages_sent;

    public MessageManager(
            ConcurrentHashMap<String, Boolean> context,
            ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
            ConcurrentHashMap<String, String> messages_sent) {
        super();
        this.context = context;
        this.c_messages_sent = c_messages_sent;
        this.messages_sent = messages_sent;
    }

    @Override
    public void run() {
        int cpt = 1;
        String message = "";
        //while (true) {
        try {
            message = "Hello n°" + cpt + " from " + InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Thread b = new Thread(new Broadcast(message, context, c_messages_sent, messages_sent));
        b.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cpt++;
        //}
    }

}
