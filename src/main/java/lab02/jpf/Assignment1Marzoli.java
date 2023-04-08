package lab02.jpf;
import gov.nasa.jpf.vm.Verify;

public class Assignment1Marzoli {

    public static final int DIRECTORY_FILES = 1;

    static class Counter {
        private int count;

        public Counter(){
            count = 0;
        }

        public void inc(){
            count++;
        }

        public int getCount(){
            return count;
        }
    }

    static class MasterThread extends Thread {
        Counter c = new Counter();

        protected void log(String msg) {
            synchronized (System.out) {
                System.out.println(msg);
            }
        }

        protected void incrementFilesCounted() {
            synchronized (c) {
                c.inc();
            }
        }
    }

    static class MyWorker extends MasterThread  {

        private MyWaitNotify wn;


        public MyWorker( MyWaitNotify wn){

            this.wn = wn;
        }

        public void run() {
        while(true){
                if(c.getCount() == DIRECTORY_FILES ){
                    break;
                }
                wn.doNotifyAll();
                wn.doWait();
                incrementFilesCounted();
            }
            log("Ha eseguito dentro run(): " + Thread.currentThread().getName());
        }
    }


    static class MonitorObject{

    }

    static class MyWaitNotify extends MasterThread{
        MonitorObject myMonitorObject = new MonitorObject();
        boolean wasSignalled = false;

        public void doWait(){
            synchronized(myMonitorObject){
                String name = Thread.currentThread().getName();
                log(name+" wasSignalled: " + wasSignalled);
                if(!wasSignalled){ //NON CI ENTRA dentro questo IF.
                    try{
                        myMonitorObject.wait();
                    } catch(InterruptedException e){e.getMessage();}
                }
                //check if is last Thread.

                log("Ha eseguito dentro doWait(): " + name);
                wasSignalled = false;
            }
        }

        public void doNotifyAll() {
            synchronized(myMonitorObject){
                //produce...
                String name = Thread.currentThread().getName();
                log("Ha eseguito dentro doNotifyAll(): " + name);
                log(name+" wasSignalled: " + wasSignalled);

                wasSignalled = true;
                myMonitorObject.notifyAll();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Verify.beginAtomic();
        MyWaitNotify wn = new MyWaitNotify();
        Counter c = new Counter();
        Thread th0 = new MyWorker(wn);
        Thread th1 = new MyWorker(wn);
        Thread th2 = new MyWorker(wn);
        th0.start();
        th1.start();
        th2.start();
        Verify.endAtomic();
        th0.join();
        th1.join();
        th2.join();



        /*int size = Counter.getCount();
        assert size == DIRECTORY_FILES;*/

    }
}
