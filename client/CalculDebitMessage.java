import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class CalculDebitMessage implements Runnable {

	private DeliveredCounter counter_debit;
	private long size_msg;

	public CalculDebitMessage(DeliveredCounter counter_debit, int size_msg) {
		super();
		this.counter_debit = counter_debit;
		this.size_msg = size_msg;
	}

	@Override
	public void run() {
		long time = System.nanoTime() / 1000;
		long time_ref = 5000000;
		while (true) {
			if (System.nanoTime() / 1000 - time >= time_ref) {
				System.out.println("debit :  " + counter_debit.get() * size_msg
						* 8 / 5 + " b/s");
				time = System.nanoTime() / 1000;
				counter_debit.reset();
			}
		}
	}

}
