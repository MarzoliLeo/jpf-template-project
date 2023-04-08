package lab02.jpf;

public class Prova1 {
    static class Counter {
        private int count;

        public synchronized void increment() {
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }

    static class WorkerThread extends Thread {
        private Counter counter;

        public WorkerThread(Counter counter) {
            this.counter = counter;
        }

        public void run() {
            for (int i = 0; i < 1000000; i++) {
                counter.increment();
            }
        }
    }

    static class MainThread {
        public static void main(String[] args) throws InterruptedException {
            Counter counter = new Counter();

            WorkerThread worker1 = new WorkerThread(counter);
            WorkerThread worker2 = new WorkerThread(counter);
            WorkerThread worker3 = new WorkerThread(counter);

            worker1.start();
            worker2.start();
            worker3.start();

            worker1.join();
            worker2.join();
            worker3.join();

            System.out.println("Counter value: " + counter.getCount());
        }
    }
}
