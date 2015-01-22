import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


public class CalculDebitMessage implements Runnable {
	
    private DeliveredCounter counter_debit;
    private long size_msg;
    
	public CalculDebitMessage(
			DeliveredCounter counter_debit,
			int size_msg) {
		super();
		this.counter_debit = counter_debit;
		this.size_msg = size_msg;
	}

	@Override
	public void run() {
		System.out.println("Debit : " + counter_debit.get() * size_msg * 8 / 10 + " b/s");
		counter_debit.reset();
	}

}
