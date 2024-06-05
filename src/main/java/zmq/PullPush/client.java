package zmq.PullPush;

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

            Random random = new Random(System.currentTimeMillis());

            while (!Thread.currentThread().isInterrupted()) {
                ZMQ.Poller poller = context.createPoller(1);
                poller.register(subscriber, ZMQ.Poller.POLLIN);

                if(poller.poll(100) > 0) {
                    String message = subscriber.recvStr();
                    System.out.println("I: received message " + message);
                }
                else{
                    int rand = new Random().nextInt(1,100);
                    if(rand < 10) {
                        String message = String.valueOf(rand);
                        publisher.send(message.getBytes(ZMQ.CHARSET), 0);
                        System.out.println("I: sending message " + message);
                    }

                }
            }
        }
    }
}
