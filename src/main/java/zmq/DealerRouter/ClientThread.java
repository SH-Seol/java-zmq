package zmq.DealerRouter;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ClientThread{
    public static void main(String[] args) {
        String clientId = "client2";
        ClientTask clientTask = new ClientTask(clientId);
        clientTask.run();
    }

    static class ClientTask implements Runnable {
        private final String id;
        private ZMQ.Socket socket;
        private ZContext context;
        private ZMQ.Poller poller;
        public ClientTask(String id) {
            this.id = id;
        }

        public void recvHandler(){
            while (true){
                int poll = poller.poll(1000);
                if (poll == -1){
                    break;
                }
                if (poller.pollin(0)){
                    String msg = socket.recvStr();
                    System.out.println( id + "reveived: " + msg);
                }
            }
        }

        @Override
        public void run() {
            this.context = new ZContext();
            this.socket = this.context.createSocket(ZMQ.DEALER);
            this.socket.setIdentity(id.getBytes(ZMQ.CHARSET));
            socket.connect("tcp://localhost:5570");
            System.out.println("Client" + id + "started");
            this.poller = context.createPoller(1);
            this.poller.register(socket, ZMQ.Poller.POLLIN);
            int reqs = 0;

            Thread clientThread = new Thread(this::recvHandler);
            clientThread.setDaemon(true);
            clientThread.start();

            while(true){
                reqs++;
                System.out.printf("Req #%d sent..", reqs);
                this.socket.send("request #" + reqs);
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
