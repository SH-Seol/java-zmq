package zmq.PullPushv2;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class client {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()) {
            ZMQ.Socket subscriber = context.createSocket(ZMQ.SUB);
            subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);
            subscriber.connect("tcp://localhost:5557");

            ZMQ.Socket publisher = context.createSocket(ZMQ.PUSH);
            publisher.connect("tcp://localhost:5558");

            String clientId = "client1";
            Random rand = new Random(System.currentTimeMillis());

            while (!Thread.currentThread().isInterrupted()) {
                ZMQ.Poller poller = context.createPoller(1);
                poller.register(subscriber, ZMQ.Poller.POLLIN);

                if (poller.poll(100) > 0 && poller.pollin(0)){
                    String message = subscriber.recvStr();
                    System.out.printf("%s: receive status => %s%n", clientId, message);
                }
                else{
                    int random = rand.nextInt(1, 100);

                    if (random < 10) {
                        try{
                            Thread.sleep(1000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        String msg = "(" + clientId + ":ON)";
                        publisher.send(msg.getBytes(ZMQ.CHARSET));
                        System.out.printf("%s: send status - activated", clientId);
                    } else if (random > 90) {
                        try{
                            Thread.sleep(1000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        String msg = "(" + clientId + ":OFF)";
                        publisher.send(msg.getBytes(ZMQ.CHARSET));
                        System.out.printf("%s: send status - deactivated", clientId);
                    }
                }
            }
        }
    }
}
