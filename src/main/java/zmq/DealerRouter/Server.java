package zmq.DealerRouter;

import org.zeromq.ZMQ;

import java.util.ArrayList;

public class Server {
    public static void main(String[] args) {
        int numWorkers = 1;

        Thread serverTask = new Thread(new ServerTask(numWorkers));
        serverTask.start();
        try {
            serverTask.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    static class ServerTask implements Runnable {
        private final int numServer;
        public ServerTask(int numServer) {
            this.numServer = numServer;
        }

        @Override
        public void run(){
            try (ZMQ.Context context = ZMQ.context(numServer)){
                ZMQ.Socket frontend = context.socket(ZMQ.ROUTER);
                frontend.bind("tcp://*:5570");

                ZMQ.Socket backend = context.socket(ZMQ.DEALER);
                backend.bind("inproc://backend");

                ArrayList<Thread> workers = new ArrayList<>();

                for (int i = 0; i < numServer; i++) {
                    Thread worker = new Thread(new ServerWorker(context, i));
                    worker.start();
                    workers.add(worker);
                }
                ZMQ.proxy(frontend, backend, null);

                frontend.close();
                backend.close();
                context.term();
            }
        }
    }

    static class ServerWorker implements Runnable {
        private final ZMQ.Context context;
        private final int id;

        public ServerWorker(ZMQ.Context context, int id) {
            this.context = context;
            this.id = id;
        }

        @Override
        public void run() {
            ZMQ.Socket worker = context.socket(ZMQ.DEALER);
            worker.connect("inproc://backend");
            System.out.printf("Worker# %d started\n", id);

            while(!Thread.interrupted()) {
                byte[] ident = worker.recv();
                byte[] msg = worker.recv();
                System.out.printf("Worker# %d received: %s from %s\n", id, new String(msg), new String(ident));
                worker.sendMore(ident);
                worker.send(msg);
            }
        }
    }
}
