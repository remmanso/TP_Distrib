import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

public class MessageManager implements Runnable {

	private ConcurrentHashMap<String, Boolean> context;
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent;
	private ConcurrentHashMap<String, String> messages_sent;
	private DeliveredCounter counter_debit;
	private boolean debit;

	public MessageManager(
			ConcurrentHashMap<String, Boolean> context,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> c_messages_sent,
			ConcurrentHashMap<String, String> messages_sent,
			DeliveredCounter counter_debit, boolean debit) {
		super();
		this.context = context;
		this.c_messages_sent = c_messages_sent;
		this.messages_sent = messages_sent;
		this.counter_debit = counter_debit;
		this.debit = debit;
	}

	@Override
	public void run() {
		int cpt = 1;
		byte[] downpacket = new byte[10000];
		new Random().nextBytes(downpacket);
		String message = new String(downpacket);
		while (true) {
			if (!debit)
				message = messageDefinition(cpt);
			Thread b = new Thread(new Broadcast(message, context,
					c_messages_sent, messages_sent));
			b.start();
			try {
				if (debit)
					Thread.sleep(5000);
				else
					Thread.sleep(500);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
