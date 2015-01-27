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
        long time_start = System.nanoTime();
        long time = System.nanoTime();
        long time_ref = 2000000;
        while (true) {
            if (System.nanoTime() - time >= time_ref*1000) {
                System.out.printf("debit :  %.2f Mb/s \n",(double)(counter_debit.get()*8 
                        *Math.pow(10,9)/(System.nanoTime() - time_start)));
                time = System.nanoTime();
            }
        }
    }

}
