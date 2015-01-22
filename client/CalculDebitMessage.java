import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class CalculDebitMessage implements Runnable {

    private DeliveredCounter counter_debit;
    private long size_msg;
    private long timer;

    public CalculDebitMessage(
            DeliveredCounter counter_debit,
            int size_msg,
            long timer) {
        super();
        this.counter_debit = counter_debit;
        this.size_msg = size_msg;
        this.timer = timer;
    }

    @Override
    public void run() {
        System.out.println(counter_debit.get());
        System.out.println(timer);
        System.out.println("Debit : " + counter_debit.get()/(timer/1000) + " Mb/s");
        counter_debit.reset();
    }

}
