public class DeliveredCounter {

    private int cpt = 0;
    private final Object lock = new Object();

    public void inc() {
        synchronized (lock) {
            cpt++;
        }
    }

    public void reset() {
        synchronized (lock) {
            cpt = 0;
        }
    }

    public int get() {
        synchronized (lock) {
            return this.cpt;
        }
    }
}
