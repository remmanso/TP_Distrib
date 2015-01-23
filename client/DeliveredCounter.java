public class DeliveredCounter {

    private int cpt = 0;
    //private boolean first = true;
    private final Object lock = new Object();
    //private final Object lock1 = new Object();

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
/*
    public void setFirst(boolean first) {
        synchronized (lock1) {
            this.first = first;
        }
    }

    public boolean getFirst() {
        synchronized (lock1) {
            return this.first;
        }
    }
    */
}
