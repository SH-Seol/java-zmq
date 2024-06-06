package zmq.DealerRouter;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client {
    public static void main(String[] args) {
        String clientId = "client1";

        Thread clientTask = new Thread(new ClientTask(clientId));
        clientTask.start();
    }
    static class ClientTask implements Runnable {
        private final String id;
        public ClientTask(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket socket = context.createSocket(ZMQ.DEALER);
                String identity = id;
                socket.setIdentity(identity.getBytes(ZMQ.CHARSET));
                socket.connect("tcp://*:5570");
                System.out.printf("Client %s started \n", identity);

                ZMQ.Poller poll = context.createPoller(1);
                poll.register(socket, ZMQ.Poller.POLLIN);

                int reqs = 0;

                while (true) {
                    reqs++;
                    System.out.printf("Req #%d sent..\n", reqs);
                    socket.send("request #" + reqs);

                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    int poller = poll.poll(1000);
                    if(poller > 0){
                        if (poll.pollin(0)){
                            String message = socket.recvStr();
                            System.out.printf("%s received %s", identity, message);
                        }
                    }
                }
            }
        }

    }

}
