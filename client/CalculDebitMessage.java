import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class CalculDebitMessage implements Runnable {

    private DeliveredCounter counter_debit;
    private long size_msg;
    private long timer;
    private int nb_msg;

    public CalculDebitMessage(
            DeliveredCounter counter_debit,
            int size_msg,
            long timer,
            int nb_msg) {
        super();
        this.counter_debit = counter_debit;
        this.size_msg = size_msg;
        this.timer = timer;
        this.nb_msg = nb_msg;
    }

    @Override
    public void run() {
        System.out.println("NBMSG :" + nb_msg);
        System.out.println("TIMER EN mS :" + timer);
        System.out.println("Debit : " + (double)nb_msg*8*1000/(timer) + " Mb/s");
        counter_debit.reset();
        counter_debit.setFirst(true);
    }

}
