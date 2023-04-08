package lab02.jpf;

import gov.nasa.jpf.vm.Verify;

public class Assignment1LM {

    static class FileProcessed {
        private int count;

        public FileProcessed(){
            count = 0;
        }

        public synchronized void inc(){
            count++;
        }

        public int getCount(){
            return count;
        }
    }

    static class Worker extends Thread {
        protected void log(String msg) {
            synchronized (System.out) {
                System.out.println(msg);
            }
        }
    }

    static class MyWorkerA extends Worker {
        private FileProcessed c;
        public MyWorkerA(FileProcessed c){
            this.c = c;
        }
        public void run() {
            c.inc();
            log("A legge un file dalla directory");
        }
    }

    static class MyWorkerB extends Worker {
        private FileProcessed c;
        public MyWorkerB(FileProcessed c){
            this.c = c;
        }
        public void run() {

            c.inc();
            log("B Legge un file dalla directory");
        }
    }


    public static void main(String[] args) throws Exception {
        Verify.beginAtomic();
        FileProcessed c = new FileProcessed();
        Thread th0 = new MyWorkerA(c);
        Thread th1 = new MyWorkerB(c);
        th0.start();
        th1.start();
        Verify.endAtomic();
        th0.join();
        th1.join();
        int endOfDirFiles = c.getCount();
        System.out.println("Il valore finale del contatore: " + endOfDirFiles);
        assert endOfDirFiles == 2;
    }

}

