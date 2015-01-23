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
        long time = System.nanoTime()/1000;
        long time_ref = 1000000;
        //System.out.println("size_msg: " + size_msg);
        while (true) {
            if (System.nanoTime() / 1000 - time >= time_ref) {
                System.out.println("debit :  " + (double)(counter_debit.get()*8 
                        *Math.pow(10,9)/(System.nanoTime() - time_start)) + " Mb/s");
                time = System.nanoTime() / 1000;
                //counter_debit.reset();
            }
        }
    }

}
